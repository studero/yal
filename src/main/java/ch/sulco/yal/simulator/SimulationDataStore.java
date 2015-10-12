package ch.sulco.yal.simulator;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.OutputChannel;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.DataStore;

@Singleton
public class SimulationDataStore extends DataStore {
	private final static Logger log = LoggerFactory.getLogger(SimulationDataStore.class);

	public SimulationDataStore() {
		log.info("Create Test Data");

		this.addChannel(new InputChannel(0L));
		this.addChannel(new InputChannel(1L));
		this.addChannel(new InputChannel(2L));
		this.addChannel(new InputChannel(3L));

		this.addChannel(new OutputChannel(4L));
		this.addChannel(new OutputChannel(5L));

		Loop loop = new Loop(0L);
		loop.getSamples().add(new Sample(0L));
		loop.getSamples().add(new Sample(1L));
		this.addLoop(loop);
		this.setCurrentLoopId(0L);

		Loop loop1 = new Loop(1L);
		this.addLoop(loop1);

		Loop loop2 = new Loop(2L);
		this.addLoop(loop2);
	}
}
