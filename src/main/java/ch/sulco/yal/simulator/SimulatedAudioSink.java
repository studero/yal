package ch.sulco.yal.simulator;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.audio.AudioSink;
import ch.sulco.yal.dsp.audio.onboard.Synchronizer;

public class SimulatedAudioSink extends AudioSink {
	private final static Logger log = LoggerFactory.getLogger(SimulatedAudioSink.class);

	@Inject
	private Synchronizer synchronizer;

	@Override
	protected void playSample(Sample sample, long position, int count) {

	}

	@Override
	protected long getSamplePosition(Sample sample) {
		return this.synchronizer.getCurrentPosition();
	}

	@Override
	protected void resetSamplePosition(Sample sample) {

	}

	@Override
	protected void endSample(Sample sample) {

	}

	@Override
	protected void finishSample(Sample sample) {

	}
}
