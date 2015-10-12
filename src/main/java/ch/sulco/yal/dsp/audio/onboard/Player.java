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

	@Override
	protected void playSample(Sample sample, int position, int count) {
		log.info("Play sample [id=" + sample.getId() + "][position=" + position + "][count=" + count + "]");
		Clip clip = this.idToClipMap.get(sample.getId());
		try {
			if (clip == null) {
				clip = this.audioSystemProvider.getClip(null, sample.getData(), 0, sample.getData().length);
			}
			clip.setFramePosition(position);
			clip.loop(count);
		} catch (LineUnavailableException e) {
			log.error("Unable to play sample", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void setSampleLoopCount(Sample sample, int count) {
		this.idToClipMap.get(sample.getId()).loop(count);
	}
}
