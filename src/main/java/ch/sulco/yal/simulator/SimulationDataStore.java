package ch.sulco.yal.simulator;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dsp.DataStore;

@Singleton
public class SimulationDataStore extends DataStore {
	private final static Logger log = LoggerFactory.getLogger(SimulationDataStore.class);

	@PostConstruct
	public void setup() {
		log.info("Create Test Data");

		this.addChannel(anInputChannel().id(0L).name("Drum Module").build());
		this.addChannel(anInputChannel().id(1L).name("Bass").build());
		this.addChannel(anInputChannel().id(2L).name("Guitar").build());
		this.addChannel(anInputChannel().id(3L).name("Piano").build());

		this.addChannel(anOutputChannel().id(4L).name("Main").build());
		this.addChannel(anOutputChannel().id(5L).name("Click").build());

		this.addLoop(aLoop()
				.id(0L)
				.name("Chorus")
				.sample(aSample().id(0L).channelId(1L).build())
				.sample(aSample().id(1L).channelId(2L).build())
				.sample(aSample().id(4L).channelId(1L).build())
				.sample(aSample().id(6L).channelId(0L).build())
				.build());
		this.setCurrentLoopId(0L);

		this.addLoop(aLoop()
				.id(1L)
				.name("Verse")
				.sample(aSample().id(2L).channelId(3L).build())
				.build());

		this.addLoop(aLoop()
				.id(2L)
				.name("Bridge")
				.sample(aSample().id(3L).channelId(0L).build())
				.sample(aSample().id(5L).channelId(0L).build())
				.build());
	}

	private static InputChannelBuilder anInputChannel() {
		return new InputChannelBuilder();
	}

	private static OutputChannelBuilder anOutputChannel() {
		return new OutputChannelBuilder();
	}

	private static LoopBuilder aLoop() {
		return new LoopBuilder();
	}

	private static SampleBuilder aSample() {
		return new SampleBuilder();
	}
}
