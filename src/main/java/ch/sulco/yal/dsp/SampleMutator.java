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
			Sample sample = dataStore.getSample(sampleId);
			Loop loop = dataStore.getLoop(loopId);
			if (sample != null && loop != null) {
				if (mute && samplePlayers.get(sampleId).contains(player)) {
					samplePlayers.get(sampleId).remove(player);
					if (loop != null && loop.isActive()) {
						player.stopSample(sample, doSynchronization);
					}
					if (samplePlayers.get(sampleId).isEmpty()) {
						sample.setMute(true);
					}
				} else if (!mute && !samplePlayers.get(sampleId).contains(player)) {
					samplePlayers.get(sampleId).add(player);
					if (loop != null && loop.isActive()) {
						player.startSample(sample, doSynchronization);
					}
					sample.setMute(false);
				}
			}
		}
	}
}
