package ch.sulco.yal.dsp.audio.onboard;

import java.util.LinkedList;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.sound.sampled.Clip;

import ch.sulco.yal.dsp.dm.SampleClip;

public class Player implements LoopListener {
	private final static Logger log = Logger.getLogger(Player.class.getName());

	@Inject
	private Synchronizer synchronizer;

	private LinkedList<Clip> playingClips = new LinkedList<Clip>();

	public void startSample(SampleClip sample) {
		log.info("Play sample "+sample.getId());
		Clip clip = sample.getClip();
		if (clip != null) {
			if (!this.playingClips.contains(clip)) {
				this.playingClips.add(clip);
				if(playingClips.size() == 1){
					synchronizer.addLoopListerner(this);
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
		log.info("Stop sample "+sample.getId());
		Clip clip = sample.getClip();
		if (clip != null) {
			clip.loop(0);
			this.playingClips.remove(clip);
			if(playingClips.isEmpty()){
				synchronizer.removeLoopListerner(this);
			}
		}
	}

	public void loopStarted(boolean firstLoop) {
		startAllSamples();
	}

	public boolean isRecorder() {
		return false;
	}
}
