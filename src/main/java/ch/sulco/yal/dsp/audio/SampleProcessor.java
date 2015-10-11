package ch.sulco.yal.dsp.audio;

import java.util.Arrays;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.event.EventManager;
import ch.sulco.yal.event.LoopLengthChanged;

public class SampleProcessor {
	private final static Logger log = LoggerFactory.getLogger(SampleProcessor.class);

	@Inject
	private DataStore dataStore;

	@Inject
	private EventManager eventManager;

	public Sample createNewSample(byte[] data, long channelId) {
		log.info("Process new Sample [channelId=" + channelId + "][sample length=" + data.length + "]");

		Sample sample = new Sample();
		sample.setChannelId(channelId);

		Loop currentLoop = this.dataStore.getLoop(this.dataStore.getCurrentLoopId());
		if (currentLoop.getLength() == null) {
			currentLoop.setLength(Long.valueOf(data.length));
			this.eventManager.addEvent(new LoopLengthChanged(currentLoop.getLength()));
		} else if (data.length < currentLoop.getLength()) {
			byte[] longerData = Arrays.copyOf(data, currentLoop.getLength().intValue());
			data = longerData;
		}
		sample.setData(data);

		return sample;
	}
}
