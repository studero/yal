package ch.sulco.yal.dsp.audio.onboard;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;

import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.dm.Sample;

public class LoopStore {
	private final static Logger log = Logger.getLogger(LoopStore.class.getName());

	private final AudioSystemProvider audioSystemProvider;
	private final AppConfig appConfig;
	private int sampleLength;
	private Map<Integer, Sample> samples = new HashMap<Integer, Sample>();

	public LoopStore(AppConfig appConfig, AudioSystemProvider audioSystemProvider) {
		this.appConfig = appConfig;
		this.audioSystemProvider = audioSystemProvider;
	}

	public int addSample(String fileName) {
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
		return 0;
	}

	public int addSample(byte[] data) {
		int id = this.addSample(this.appConfig.getAudioFormat(), data);
		log.info("New Sample Id [" + id + "]");
		return id;
	}

	public int addSample(AudioFormat format, byte[] data) {
		int newId = -1;
		try {
			if (this.samples.isEmpty()) {
				this.sampleLength = data.length;
			} else if (data.length < this.sampleLength) {
				byte[] longerData = Arrays.copyOf(data, this.sampleLength);
				data = longerData;
			}
			Clip clip = this.audioSystemProvider.getClip(format, data, 0, this.sampleLength);
			log.info("Added Clip [length=" + clip.getMicrosecondLength() + "]");
			newId = this.samples.size() == 0 ? 0 : Collections.max(this.samples.keySet()) + 1;
			Sample sample = new Sample(newId, clip);
			this.samples.put(newId, sample);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newId;
	}

	public void removeSample(int id) {
		Sample sample = this.samples.remove(id);
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
	}

	public Collection<Sample> getSamples() {
		return this.samples.values();
	}

	public Set<Integer> getSampleIds() {
		return this.samples.keySet();
	}

	public Sample getSample(int id) {
		return this.samples.get(id);
	}
}
