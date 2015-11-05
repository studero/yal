package ch.sulco.yal.dsp.audio;

import java.util.LinkedList;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.audio.onboard.LoopListener;
import ch.sulco.yal.dsp.audio.onboard.SyncAdjustment;
import ch.sulco.yal.dsp.audio.onboard.Synchronizer;

public abstract class AudioSink implements LoopListener {
	private final static Logger log = LoggerFactory.getLogger(AudioSink.class);

	@Inject
	private Synchronizer synchronizer;

	private LinkedList<Sample> playingSamples = new LinkedList<>();

	public void startSample(Sample sample, boolean doSynchronization) {
		log.info("Start sample [" + sample.getId() + "]");
		if (!this.playingSamples.contains(sample)) {
			this.playingSamples.add(sample);
			this.resetSamplePosition(sample);
			synchronizer.addLoopListerner(this);
			if (!doSynchronization) {
				log.info("Play position " + synchronizer.getCurrentPosition());
				this.playSample(sample, synchronizer.getCurrentPosition(), -1);
			}
		}
	}

	protected abstract void playSample(Sample sample, long position, int count);

	protected abstract long getSamplePosition(Sample sample);

	protected abstract void resetSamplePosition(Sample sample);

	protected abstract void endSample(Sample sample);

	protected abstract void finishSample(Sample sample);

	public void stopSample(Sample sample, boolean doSynchronization) {
		log.info("Stop sample [" + sample.getId() + "]");
		if (this.playingSamples.contains(sample)) {
			if (doSynchronization) {
				this.finishSample(sample);
			} else {
				this.endSample(sample);
			}
			this.playingSamples.remove(sample);
			if (this.playingSamples.isEmpty()) {
				this.synchronizer.removeLoopListerner(this);
			}
		}
	}

	public abstract void muteSample(Sample sample, boolean mute);

	@Override
	public SyncAdjustment loopStarted(boolean firstLoop) {
		long halfLoopLength = synchronizer.getLoopLength() / 2;
		SyncAdjustment syncAdjustment = new SyncAdjustment(halfLoopLength, -halfLoopLength, 0L);
		for (Sample sample : this.playingSamples) {
			long samplePosition = this.getSamplePosition(sample);
			if (samplePosition == 0) {
				this.playSample(sample, 0, -1);
			} else {
				samplePosition += halfLoopLength;
				samplePosition = samplePosition % synchronizer.getLoopLength();
				samplePosition -= halfLoopLength;
				if (samplePosition < syncAdjustment.getLowestSamplePosition()) {
					syncAdjustment.setLowestSamplePosition(samplePosition);
				}
				syncAdjustment.setAverageSamplePosition(syncAdjustment.getAverageSamplePosition() + samplePosition);
				if (samplePosition > syncAdjustment.getHighestSamplePosition()) {
					syncAdjustment.setHighestSamplePosition(samplePosition);
				}
			}
		}
		syncAdjustment.setAverageSamplePosition(syncAdjustment.getAverageSamplePosition() / this.playingSamples.size());
		return syncAdjustment;
	}
}
