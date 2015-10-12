package ch.sulco.yal.dsp.audio;

import java.util.LinkedList;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.audio.onboard.LoopListener;
import ch.sulco.yal.dsp.audio.onboard.Synchronizer;

public abstract class AudioSink implements LoopListener {
	private final static Logger log = LoggerFactory.getLogger(AudioSink.class);

	@Inject
	private Synchronizer synchronizer;

	private LinkedList<Sample> playingSamples = new LinkedList<>();

	public void startSample(Sample sample) {
		log.info("Start sample [" + sample + "]");
		if (!this.playingSamples.contains(sample)) {
			this.playingSamples.add(sample);
			if (this.playingSamples.size() == 1) {
				this.synchronizer.addLoopListerner(this);
			} else {
				this.synchronizer.checkLine();
			}
		}
	}

	private void startAllSamples() {
		for (Sample sample : this.playingSamples) {
			this.playSample(sample, 0, -1);
		}
	}

	protected abstract void playSample(Sample sample, int position, int count);

	protected abstract void setSampleLoopCount(Sample sample, int count);

	public void stopSample(Sample sample) {
		log.info("Stop sample [" + sample + "]");
		if (this.playingSamples.contains(sample)) {
			this.setSampleLoopCount(sample, 0);
			this.playingSamples.remove(sample);
			if (this.playingSamples.isEmpty()) {
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
