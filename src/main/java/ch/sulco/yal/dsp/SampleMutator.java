package ch.sulco.yal.dsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.audio.AudioSink;

@Singleton
public class SampleMutator {

	@Inject
	private DataStore dataStore;

	private Map<Long, List<AudioSink>> samplePlayers = new HashMap<>();

	public List<AudioSink> getSamplePlayers(Long sampleId) {
		return samplePlayers.containsKey(sampleId) ? samplePlayers.get(sampleId) : new ArrayList<>();
	}

	public void setMute(Long loopId, Long sampleId, boolean mute, AudioSink player, boolean doSynchronization) {
		if (!samplePlayers.containsKey(sampleId)) {
			samplePlayers.put(sampleId, new ArrayList<>());
		}
		if (player != null) {
			Sample sample = dataStore.getSample(loopId, sampleId);
			Loop loop = dataStore.getLoop(loopId);
			if (sample != null && loop != null) {
				// if (mute && samplePlayers.get(sampleId).contains(player)) {
				// samplePlayers.get(sampleId).remove(player);
				// if (loop != null && loop.isActive() &&
				// dataStore.getLooperState() == LooperState.PLAYING) {
				// player.stopSample(sample, doSynchronization);
				// }
				// } else if (!mute &&
				// !samplePlayers.get(sampleId).contains(player)) {
				// samplePlayers.get(sampleId).add(player);
				// if (loop != null && loop.isActive() &&
				// dataStore.getLooperState() == LooperState.PLAYING) {
				// player.startSample(sample, doSynchronization);
				// }
				// }
				player.muteSample(sample, mute);
				sample.setMute(mute);
				dataStore.updateSample(loopId, sample);
			}
		}
	}

	public void stopSample(Long loopId, Long sampleId, AudioSink player, boolean doSynchronization) {
		if (!samplePlayers.containsKey(sampleId)) {
			samplePlayers.put(sampleId, new ArrayList<>());
		}
		if (player != null) {
			Sample sample = dataStore.getSample(loopId, sampleId);
			Loop loop = dataStore.getLoop(loopId);
			if (sample != null && loop != null) {
				if (samplePlayers.get(sampleId).contains(player)) {
					samplePlayers.get(sampleId).remove(player);
					player.stopSample(sample, doSynchronization);
				}
			}
		}
	}

	public void startSample(Long loopId, Long sampleId, AudioSink player, boolean doSynchronization) {
		if (!samplePlayers.containsKey(sampleId)) {
			samplePlayers.put(sampleId, new ArrayList<>());
		}
		if (player != null) {
			Sample sample = dataStore.getSample(loopId, sampleId);
			Loop loop = dataStore.getLoop(loopId);
			if (sample != null && loop != null) {
				if (!samplePlayers.get(sampleId).contains(player)) {
					samplePlayers.get(sampleId).add(player);
				}
				player.startSample(sample, doSynchronization);

			}
		}
	}
}
