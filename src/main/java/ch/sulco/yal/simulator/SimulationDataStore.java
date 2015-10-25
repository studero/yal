package ch.sulco.yal.simulator;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dsp.DataStore;

@Singleton
public class SimulationDataStore extends DataStore {
	private final static Logger log = LoggerFactory.getLogger(SimulationDataStore.class);

	@Override
	public void setup() {
		log.info("Create Test Data");

		this.createLoop(aLoop()
				.id(0L)
				.name("Chorus")
				.length(5423120L)
				.active(true)
				.sample(aSample().id(0L).channelId(1L).build())
				.sample(aSample().id(1L).channelId(2L).build())
				.sample(aSample().id(4L).channelId(1L).build())
				.sample(aSample().id(6L).channelId(0L).build())
				.build());

		this.createLoop(aLoop()
				.id(1L)
				.name("Verse")
				.length(7234650L)
				.sample(aSample().id(2L).channelId(3L).build())
				.build());

		this.createLoop(aLoop()
				.id(2L)
				.name("Bridge")
				.length(5345780L)
				.sample(aSample().id(3L).channelId(0L).build())
				.sample(aSample().id(5L).channelId(0L).build())
				.build());
	}

	private static LoopBuilder aLoop() {
		return new LoopBuilder();
	}

	private static SampleBuilder aSample() {
		return new SampleBuilder();
	}
}
