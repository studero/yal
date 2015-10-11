package ch.sulco.yal.dsp.audio.onboard;

import java.util.LinkedList;

import javax.inject.Inject;
import javax.sound.sampled.Clip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dsp.dm.SampleClip;

public class Player implements LoopListener {
	private final static Logger log = LoggerFactory.getLogger(Player.class);

	@Inject
	private Synchronizer synchronizer;

	private LinkedList<Clip> playingClips = new LinkedList<Clip>();

	public void startSample(SampleClip sample) {
		log.info("Play sample " + sample.getId());
		Clip clip = sample.getClip();
		if (clip != null) {
			if (!this.playingClips.contains(clip)) {
				this.playingClips.add(clip);
				if (this.playingClips.size() == 1) {
					this.synchronizer.addLoopListerner(this);
				}
			}
		}
	}

	private void startAllSamples() {
		for (Clip clip : this.playingClips) {
			clip.setFramePosition(0);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	public void stopSample(SampleClip sample) {
		log.info("Stop sample " + sample.getId());
		Clip clip = sample.getClip();
		if (clip != null) {
			clip.loop(0);
			this.playingClips.remove(clip);
			if (this.playingClips.isEmpty()) {
				this.synchronizer.removeLoopListerner(this);
			}
		}
	}

	@Override
	public void loopStarted(boolean firstLoop) {
		this.startAllSamples();
	}

	@Override
	public boolean isRecorder() {
		return false;
	}
}
