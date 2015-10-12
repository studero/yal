package ch.sulco.yal.dsp.audio.onboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.AppConfig;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.dm.SampleClip;
import ch.sulco.yal.event.EventManager;

@Singleton
public class LoopStore {
	private final static Logger log = LoggerFactory.getLogger(LoopStore.class);

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

	public Long addSample(byte[] data) {
		Long id = this.addSample(this.appConfig.getAudioFormat(), data);
		log.info("New Sample Id [" + id + "]");
		return id;
	}

	public Long addSample(AudioFormat format, byte[] data) {
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
			sample.setData(data);
			sample.setChannelId(null);
			this.eventManager.createSample(sample);
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
}
