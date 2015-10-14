package ch.sulco.yal.dsp.audio.onboard;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.audio.AudioSink;

public class Player extends AudioSink {
	private final static Logger log = LoggerFactory.getLogger(Player.class);

	@Inject
	private AudioSystemProvider audioSystemProvider;

	private Map<Long, Clip> idToClipMap = new HashMap<>();
	
	private Clip getSampleClip(Sample sample){
		Clip clip = this.idToClipMap.get(sample.getId());
		try {
			if (clip == null) {
				clip = this.audioSystemProvider.getClip(null, sample.getData(), 0, sample.getData().length);
				this.idToClipMap.put(sample.getId(), clip);
			}
		} catch (LineUnavailableException e) {
			log.error("Unable to play sample", e);
			throw new RuntimeException(e);
		}
		return clip;
	}

	@Override
	protected void playSample(Sample sample, long position, int count) {
		log.info("Play sample [id=" + sample.getId() + "][position=" + position + "][count=" + count + "]");
		Clip clip = this.getSampleClip(sample);
		clip.setMicrosecondPosition(position);
		clip.loop(count);
	}

	@Override
	protected long getSamplePosition(Sample sample) {
		return this.getSampleClip(sample).getMicrosecondPosition();
	}

	@Override
	protected void resetSamplePosition(Sample sample) {
		this.getSampleClip(sample).setFramePosition(0);
	}
	
	@Override
	protected void endSample(Sample sample){
		Clip clip = this.getSampleClip(sample);
		clip.stop();
		clip.setFramePosition(0);
	}

	@Override
	protected void finishSample(Sample sample) {
		this.getSampleClip(sample).loop(0);
	}
}
