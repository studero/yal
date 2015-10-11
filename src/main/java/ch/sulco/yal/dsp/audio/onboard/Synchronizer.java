package ch.sulco.yal.dsp.audio.onboard;

import java.util.Arrays;
import java.util.LinkedList;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dsp.AppConfig;

@Singleton
public class Synchronizer {
	private final static Logger log = LoggerFactory.getLogger(Synchronizer.class);

	@Inject
	private AppConfig appConfig;

	@Inject
	private AudioSystemProvider audioSystemProvider;

	private LineListener lineListener;
	private LinkedList<LoopListener> loopListeners = new LinkedList<LoopListener>();
	private int recorderListeners = 0;
	private Clip synchronizeClip;

	public void initialize(int length) {
		try {
			byte[] data = new byte[length];
			Arrays.fill(data, 0, length, (byte) 0x00);
			this.synchronizeClip = this.audioSystemProvider.getClip(this.appConfig.getAudioFormat(), data, 0, length);
			log.info("Synchronizer loop initialized, length " + length);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		this.synchronizeClip = null;
		log.info("Synchronizer loop cleared");
	}

	public void checkLine() {
		if (this.lineListener == null) {
			this.lineListener = new LineListener() {
				@Override
				public void update(LineEvent event) {
					log.info("Synchronization event [" + event + "]");
					if (event.getType() == Type.START) {
						for (LoopListener loopListener : Synchronizer.this.loopListeners) {
							loopListener.loopStarted(false);
						}
					} else if (event.getType() == Type.STOP) {
						if (Synchronizer.this.recorderListeners == 0) {
							Synchronizer.this.synchronizeClip.removeLineListener(this);
							Synchronizer.this.lineListener = null;
							if (!Synchronizer.this.loopListeners.isEmpty()) {
								log.info("Synchronization loop playing");
								Synchronizer.this.synchronizeClip.setFramePosition(0);
								Synchronizer.this.synchronizeClip.loop(Clip.LOOP_CONTINUOUSLY);
							}
						} else {
							Synchronizer.this.synchronizeClip.setFramePosition(0);
							Synchronizer.this.synchronizeClip.loop(0);
						}
					}
				}
			};
			this.synchronizeClip.addLineListener(this.lineListener);
			this.synchronizeClip.loop(0);
			log.info("Synchronization loop event set up");
		}
	}

	public void addLoopListerner(LoopListener loopListerer) {
		if (this.synchronizeClip == null) {
			loopListerer.loopStarted(true);
		} else {
			this.checkLine();
		}
		this.loopListeners.add(loopListerer);
		if (loopListerer.isRecorder()) {
			this.recorderListeners++;
		}
		log.info("Synchronization listener added, now has " + this.loopListeners.size());
	}

	public void removeLoopListerner(LoopListener loopListerer) {
		this.loopListeners.remove(loopListerer);
		if (loopListerer.isRecorder()) {
			this.recorderListeners--;
		}
		log.info("Synchronization listener removed, now has " + this.loopListeners.size());
		if (this.loopListeners.isEmpty() && this.synchronizeClip != null) {
			this.synchronizeClip.stop();
			this.synchronizeClip.setFramePosition(0);
		}
	}
}
