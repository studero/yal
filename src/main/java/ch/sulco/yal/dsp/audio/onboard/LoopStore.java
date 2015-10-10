package ch.sulco.yal.dsp.audio.onboard;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;

import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.dm.SampleClip;
import ch.sulco.yal.event.EventManager;
import ch.sulco.yal.event.SampleCreated;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

@Singleton
public class LoopStore {
	private final static Logger log = Logger.getLogger(LoopStore.class.getName());

	@Inject
	private AppConfig appConfig;

	@Inject
	private AudioSystemProvider audioSystemProvider;

	@Inject
	private EventManager eventManager;

	@Inject
	private Synchronizer synchronizer;

	private Integer sampleLength;
	private Map<Long, SampleClip> samples = new HashMap<>();

	public Long addSample(String fileName) {
		log.info("Add Sample [fileName=" + fileName + "]");
		try {
			File file = new File(fileName);
			AudioInputStream ais = this.audioSystemProvider.getAudioInputStream(file);
			byte[] data = new byte[(int) file.length()];
			ais.read(data);
			return this.addSample(ais.getFormat(), data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Long addSample(byte[] data) {
		Long id = this.addSample(this.appConfig.getAudioFormat(), data);
		log.info("New Sample Id [" + id + "]");
		return id;
	}

	private Long addSample(AudioFormat format, byte[] data) {
		log.info("Add Sample [ bytes=" + data.length + "]");
		Long newId = null;
		try {
			if (this.samples.isEmpty()) {
				this.sampleLength = data.length;
				this.synchronizer.initialize(data.length);
			} else if (data.length < this.sampleLength) {
				byte[] longerData = Arrays.copyOf(data, this.sampleLength);
				data = longerData;
			}
			Clip clip = this.audioSystemProvider.getClip(format, data, 0, this.sampleLength);
			log.info("Added Clip [length=" + clip.getMicrosecondLength() + "]");
			newId = this.samples.size() == 0 ? 0 : Collections.max(this.samples.keySet()) + 1;
			SampleClip sampleClip = new SampleClip(newId, clip);
			this.samples.put(newId, sampleClip);
			Sample sample = new Sample();
			sample.setId(newId);
			sample.setMute(true);
			this.eventManager.addEvent(new SampleCreated(sample));
			log.info("Sample added [" + newId + "][" + clip + "]");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newId;
	}

	public void removeSample(Long id) {
		SampleClip sample = this.samples.remove(id);
		final Clip clip = sample.getClip();
		if (clip != null) {
			new Thread() {
				@Override
				public void run() {
					clip.loop(0);
					clip.drain();
					clip.close();
				}

			}.start();
		}
		if (this.samples.isEmpty()) {
			this.synchronizer.reset();
		}
	}

	public Collection<SampleClip> getSamples() {
		return this.samples.values();
	}

	public Set<Long> getSampleIds() {
		return this.samples.keySet();
	}

	public SampleClip getSample(Long id) {
		return this.samples.get(id);
	}

	public Integer getLoopLength() {
		return this.sampleLength;
	}

	public Long getLoopPosition() {
		Optional<SampleClip> first = FluentIterable.from(this.samples.values()).first();
		if (first.isPresent()) {
			return first.get().getClip().getMicrosecondPosition();
		}
		return null;
	}
}
