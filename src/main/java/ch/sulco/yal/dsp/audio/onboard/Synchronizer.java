package ch.sulco.yal.dsp.audio.onboard;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

@Singleton
public class Synchronizer {
	private final static Logger log = Logger.getLogger(Synchronizer.class.getName());

	@Inject
	private AudioSystemProvider audioSystemProvider;
	
	private LineListener lineListener;
	private LinkedList<LoopListener> loopListeners = new LinkedList<LoopListener>();
	private Clip synchronizeClip;


	public void initialize(int length) {
		try {
			byte[] data = new byte[length];
			Arrays.fill(data, 0, length, (byte)0x00);
			synchronizeClip = this.audioSystemProvider.getClip(null, data, 0, length);
			log.info("Synchronizer loop initialized, length "+length);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		synchronizeClip = null;
		log.info("Synchronizer loop cleared");
	}

	public void checkLine() {
		if (this.lineListener == null) {
			this.lineListener = new LineListener() {
				@Override
				public void update(LineEvent event) {
					log.info("Synchronization event [" + event + "]");
					synchronizeClip.removeLineListener(this);
					lineListener = null;
					if(!loopListeners.isEmpty()){
						log.info("Synchronization loop playing");
						synchronizeClip.setFramePosition(0);
						synchronizeClip.loop(Clip.LOOP_CONTINUOUSLY);
					}
					for (LoopListener loopListener : loopListeners) {
						loopListener.loopStarted(false);
					}
				}
			};
			synchronizeClip.addLineListener(this.lineListener);
			synchronizeClip.loop(0);
			log.info("Synchronization loop event set up");
		}
	}

	public void addLoopListerner(LoopListener loopListerer) {
		if (synchronizeClip == null) {
			loopListerer.loopStarted(true);
		} else {
			this.checkLine();
		}
		this.loopListeners.add(loopListerer);
		log.info("Synchronization listener added, now has "+loopListeners.size());
	}

	public void removeLoopListerner(LoopListener loopListerer) {
		this.loopListeners.remove(loopListerer);
		log.info("Synchronization listener removed, now has "+loopListeners.size());
		if(loopListeners.isEmpty() && synchronizeClip != null){
			synchronizeClip.stop();
			synchronizeClip.setFramePosition(0);
		}
	}
}
