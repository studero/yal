package ch.sulco.yal.dsp.audio.onboard;

import java.util.LinkedList;
import java.util.logging.Logger;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import ch.sulco.yal.dsp.dm.Sample;

public class Player {
	private final static Logger log = Logger.getLogger(Player.class.getName());

	private LineListener lineListener;
	private LinkedList<LoopListener> loopListeners = new LinkedList<LoopListener>();
	private LinkedList<Clip> playingClips = new LinkedList<Clip>();

	public void startSample(Sample sample) {
		Clip clip = sample.getClip();
		if (clip != null) {
			if (!this.playingClips.contains(clip)) {
				if (this.playingClips.isEmpty()) {
					clip.loop(Clip.LOOP_CONTINUOUSLY);
				} else {
					this.checkLine();
				}
				this.playingClips.add(clip);
			}
		}
	}

	private void checkLine() {
		if (this.lineListener == null) {
			this.lineListener = new LineListener() {
				@Override
				public void update(LineEvent event) {
					log.info("Clip event [" + event + "]");
					Player.this.playingClips.getFirst().removeLineListener(this);
					Player.this.lineListener = null;
					for (LoopListener loopListener : Player.this.loopListeners) {
						loopListener.loopStarted();
					}
					Player.this.startAllSamples();
				}
			};
			this.playingClips.getFirst().addLineListener(this.lineListener);
			this.playingClips.getFirst().loop(0);
		}
	}

	private void startAllSamples() {
		for (Clip clip : this.playingClips) {
			clip.setFramePosition(0);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	public void stopSample(Sample sample) {
		Clip clip = sample.getClip();
		if (clip != null) {
			clip.loop(0);
			this.playingClips.remove(clip);
		}
	}

	public void addLoopListerner(LoopListener loopListerer) {
		if (this.playingClips.isEmpty()) {
			loopListerer.loopStarted();
		} else {
			this.checkLine();
		}
		this.loopListeners.add(loopListerer);
	}

	public void removeLoopListerner(LoopListener loopListerer) {
		this.loopListeners.remove(loopListerer);
	}
}
