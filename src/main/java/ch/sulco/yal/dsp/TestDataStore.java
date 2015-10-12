package ch.sulco.yal.dsp;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.OutputChannel;
import ch.sulco.yal.dm.Sample;

@Singleton
public class TestDataStore extends DataStore {
	private final static Logger log = LoggerFactory.getLogger(TestDataStore.class);

	public TestDataStore() {
		log.info("Create Test Data");

		this.addChannel(new InputChannel(0L));

		this.addChannel(new OutputChannel(1L));

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
