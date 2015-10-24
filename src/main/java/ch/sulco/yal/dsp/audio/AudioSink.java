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

	public void startSample(Sample sample, boolean doSynchronization) {
		log.info("Start sample [" + sample.getId() + "]");
		if (!this.playingSamples.contains(sample)) {
			this.playingSamples.add(sample);
			this.resetSamplePosition(sample);
			synchronizer.addLoopListerner(this);
			if(!doSynchronization){
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
			if(doSynchronization){
				this.finishSample(sample);
			}else{
				this.endSample(sample);
			}
			this.playingSamples.remove(sample);
			if (this.playingSamples.isEmpty()) {
				this.synchronizer.removeLoopListerner(this);
			}
		}
	}

	@Override
	public long[] loopStarted(boolean firstLoop) {
		long halfLoopLength = synchronizer.getLoopLength() / 2;
		long position[] = {halfLoopLength,0,-halfLoopLength};
		for (Sample sample : this.playingSamples) {
			long samplePosition = this.getSamplePosition(sample);
			if(samplePosition == 0){
				this.playSample(sample, 0, -1);
			}else{
				samplePosition += halfLoopLength;
				samplePosition = samplePosition % synchronizer.getLoopLength();
				samplePosition -= halfLoopLength;
				if(samplePosition < position[0]){
					position[0] = samplePosition;
				}
				position[1] += samplePosition;
				if(samplePosition > position[2]){
					position[2] = samplePosition;
				}
			}
		}
		position[1] /= this.playingSamples.size();
		return position;
	}
}
