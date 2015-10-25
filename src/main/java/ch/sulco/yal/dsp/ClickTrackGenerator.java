package ch.sulco.yal.dsp;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dm.SpecialSample;
import ch.sulco.yal.dsp.DataStore.LoopUpdated;
import ch.sulco.yal.event.Event;
import ch.sulco.yal.event.EventListener;

@Singleton
public class ClickTrackGenerator implements EventListener {

	private final static Logger log = LoggerFactory.getLogger(ClickTrackGenerator.class);

	@Inject
	private DataStore dataStore;

	@PostConstruct
	public void setup() {
		this.dataStore.addListener(this);
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof LoopUpdated) {
			LoopUpdated loopUpdated = (LoopUpdated) event;
			if (loopUpdated.getLoop().getClickTrack() == null
					&& loopUpdated.getLoop().getBars() != null
					&& loopUpdated.getLoop().getBeats() != null
					&& loopUpdated.getLoop().getDataLength() > 0) {
				loopUpdated.getLoop().setClickTrack(createClickTrackSample(
						loopUpdated.getLoop().getBars(),
						loopUpdated.getLoop().getBeats(),
						loopUpdated.getLoop().getDataLength()));
				this.dataStore.updateLoop(loopUpdated.getLoop());
			}
		}
	}

	private Sample createClickTrackSample(int bars, int beats, int dataLength) {
		log.info("create ClickTrack [bars=" + bars + "][beats=" + beats + "][dataLength=" + dataLength + "]");
		Sample clickSample = new Sample();
		clickSample.setId(SpecialSample.CLICK.getId());
		clickSample.setChannelId(null);
		clickSample.setData(createClickTrackData(bars, beats, dataLength));
		return clickSample;
	}

	private byte[] createClickTrackData(int bars, int beats, int dataLength) {
		byte[] clickBytes = new byte[dataLength];
		Arrays.fill(clickBytes, (byte) 0);
		int bytesPerBeat = dataLength / (bars * beats);
		for (int beat = 0; beat < bars * beats; beat++) {
			if (beat % beats == 0) {
				Arrays.fill(clickBytes, beat * bytesPerBeat, beat * bytesPerBeat + 100, (byte) 80);
			} else {
				Arrays.fill(clickBytes, beat * bytesPerBeat, beat * bytesPerBeat + 100, (byte) 20);
			}
		}
		return clickBytes;
	}
}
