package ch.sulco.yal.dsp.audio.onboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.Application;
import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.OutputChannel;
import ch.sulco.yal.dm.RecordingState;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.event.ChannelCreated;
import ch.sulco.yal.event.Event;
import ch.sulco.yal.event.EventListener;
import ch.sulco.yal.event.EventManager;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

@Singleton
public class OnboardProcessor implements Processor, EventListener {

	private final static Logger log = LoggerFactory.getLogger(OnboardProcessor.class);

	@Inject
	private DataStore dataStore;

	@Inject
	private EventManager eventManager;

	private final Map<Long, Recorder> recorders = new HashMap<>();
	private final Map<Long, Player> players = new HashMap<>();

	@PostConstruct
	public void setup() {
		log.info("Setup");
		this.eventManager.addListener(this);
	}

	@Override
	public Set<Long> getSampleIds() {
		return FluentIterable.from(this.dataStore.getCurrentLoop().getSamples()).transform(new Function<Sample, Long>() {
			@Override
			public Long apply(Sample input) {
				return input.getId();
			}
		}).toSet();
	}

	@Override
	public void play() {
		log.info("Play");
		for (Recorder recorder : this.recorders.values()) {
			recorder.startRecord();
		}
	}

	@Override
	public void loop() {
		log.info("Loop");
		for (Recorder recorder : this.recorders.values()) {
			recorder.stopRecord();
		}

		log.info("Start Sample");
		Optional<Player> firstPlayer = FluentIterable.from(this.players.values()).first();
		if (firstPlayer.isPresent()) {
			Loop currentLoop = this.dataStore.getCurrentLoop();
			Sample sample = currentLoop.getSamples().get(0);
			firstPlayer.get().startSample(sample);
		}
	}

	@Override
	public void setChannelRecording(Long channelId, boolean recording) {
		if (recording) {
			this.recorders.get(channelId).startRecord();
		} else {
			this.recorders.get(channelId).stopRecord();
		}
	}

	@Override
	public void setSampleMute(Long sampleId, boolean mute) {
		Optional<Player> firstPlayer = FluentIterable.from(this.players.values()).first();
		if (firstPlayer.isPresent()) {
			Sample sample = this.dataStore.getCurrentLoopSample(sampleId);
			if (sample != null) {
				if (mute) {
					firstPlayer.get().stopSample(sample);
				} else {
					firstPlayer.get().startSample(sample);
				}
				Sample s = new Sample();
				s.setId(sampleId);
				s.setMute(mute);
				this.eventManager.updateSample(s);
			}
		}

	}

	@Override
	public void setSampleVolume(Long sampleId, float volume) {
		this.dataStore.getCurrentLoopSample(sampleId).setGain(volume);
	}

	@Override
	public RecordingState getChannelRecordingState(Long channelId) {
		return this.recorders.get(channelId).getRecordingState();
	}

	@Override
	public boolean isSampleMute(Long sampleId) {
		return this.dataStore.getCurrentLoopSample(sampleId).isMute();
	}

	@Override
	public float getSampleVolume(Long sampleId) {
		return this.dataStore.getCurrentLoopSample(sampleId).getGain();
	}

	@Override
	public boolean getChannelMonitoring(Long channelId) {
		return this.recorders.get(channelId).isMonitoring();
	}

	@Override
	public void setChannelMonitoring(Long channelId, boolean monitoring) {
		this.recorders.get(channelId).setMonitoring(monitoring);
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof ChannelCreated) {
			ChannelCreated channelCreated = (ChannelCreated) event;
			Channel channel = channelCreated.getChannel();
			if (channel instanceof InputChannel) {
				InputChannel inputChannel = (InputChannel) channel;
				if (!this.recorders.containsKey(channel.getId())) {
					Recorder recorder = Application.injector.getInstance(Recorder.class);
					recorder.setInputChannel(inputChannel);
					recorder.initialize();
					this.recorders.put(inputChannel.getId(), recorder);
				}
			} else if (channel instanceof OutputChannel) {
				OutputChannel outputChannel = (OutputChannel) channel;
				if (!this.players.containsKey(outputChannel.getId())) {
					Player player = Application.injector.getInstance(Player.class);
					this.players.put(outputChannel.getId(), player);
				}
			}
		}
	}

}
