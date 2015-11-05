package ch.sulco.yal.dsp;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.audio.AudioSink;
import ch.sulco.yal.dsp.audio.onboard.Synchronizer;

public class LoopActivator {

	private final static Logger log = LoggerFactory.getLogger(LoopActivator.class);

	@Inject
	private DataStore dataStore;

	@Inject
	private SampleMutator sampleMutator;

	@Inject
	private transient Synchronizer synchronizer;

	public void setCurrentLoopId(Long currentLoopId) {
		log.info("activate loop [" + currentLoopId + "]");
		Loop before = dataStore.getCurrentLoop();
		Loop after = dataStore.getLoop(currentLoopId);
		if (before != null) {
			before.setActive(false);
			updateLoopSamples(before, false);
		}
		after.setActive(true);
		updateLoopSamples(after, true);
		if (before != null) {
			this.dataStore.updateLoop(before);
		}
		this.dataStore.updateLoop(after);
		this.dataStore.setLooperState(this.dataStore.getLooperState());
	}

	private void updateLoopSamples(Loop loop, boolean active) {
		if (active && !loop.getSamples().isEmpty()) {
			this.synchronizer.setLength(loop.getTimeLength());
		}
		for (Sample sample : loop.getSamples()) {
			for (AudioSink player : sampleMutator.getSamplePlayers(sample.getId())) {
				if (active) {
					player.startSample(sample, true);
				} else {
					player.stopSample(sample, false);
				}
			}
		}
	}

}
