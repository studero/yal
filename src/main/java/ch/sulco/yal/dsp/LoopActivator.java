package ch.sulco.yal.dsp;

import javax.inject.Inject;

import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.audio.AudioSink;
import ch.sulco.yal.dsp.audio.onboard.Synchronizer;

public class LoopActivator {

	@Inject
	private DataStore dataStore;

	@Inject
	private SampleMutator sampleMutator;

	@Inject
	private transient Synchronizer synchronizer;

	public void setCurrentLoopId(Long currentLoopId) {
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
	}

	private void updateLoopSamples(Loop loop, boolean active) {
		for (Sample sample : loop.getSamples()) {
			for (AudioSink player : sampleMutator.getSamplePlayers(sample.getId())) {
				if (active) {
					player.startSample(sample, true);
				} else {
					player.stopSample(sample, true);
				}
			}
		}
		if (active && !loop.getSamples().isEmpty()) {
			this.synchronizer.initialize(loop.getTimeLength());
		}
	}

}
