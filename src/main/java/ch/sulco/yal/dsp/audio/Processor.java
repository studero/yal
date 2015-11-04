package ch.sulco.yal.dsp.audio;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import ch.sulco.yal.Application;
import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dm.LooperState;
import ch.sulco.yal.dm.OutputChannel;
import ch.sulco.yal.dm.RecordingState;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.DataStore.ChannelCreated;
import ch.sulco.yal.dsp.DataStore.DataEvent;
import ch.sulco.yal.dsp.DataStore.DataEventListener;
import ch.sulco.yal.dsp.SampleMutator;

@Singleton
public class Processor implements DataEventListener {

	private final static Logger log = LoggerFactory.getLogger(Processor.class);

	@Inject
	private DataStore dataStore;

	@Inject
	private SampleMutator sampleMutator;

	private final Map<Long, AudioSource> audioSources = new HashMap<>();
	private final Map<Long, AudioSink> audioSinks = new HashMap<>();

	public Set<Long> getSampleIds() {
		return FluentIterable.from(this.dataStore.getCurrentLoop().getSamples()).transform(new Function<Sample, Long>() {
			@Override
			public Long apply(Sample input) {
				return input.getId();
			}
		}).toSet();
	}

	@PostConstruct
	public void setup() {
		log.info("Setup");
		this.dataStore.addListener(this);
	}

	public void play() {
		log.info("Play");
		dataStore.setLooperState(LooperState.PLAYING);
	}

	public void pause() {
		// TODO pause players
		dataStore.setLooperState(LooperState.PAUSED);
	}

	public void stop() {
		// TODO stop players
		dataStore.setLooperState(LooperState.STOPPED);
	}

	public void loop() {
		log.info("Loop");
		for (AudioSource audioSource : this.audioSources.values()) {
			audioSource.stopRecord();
		}

		log.info("Start Sample");
		Optional<AudioSink> firstPlayer = FluentIterable.from(this.audioSinks.values()).first();
		if (firstPlayer.isPresent()) {
			firstPlayer.get().startSample(this.dataStore.getCurrentLoopSample(0), true);
			if (!this.dataStore.getCurrentLoop().isClickTrackMuted())
				firstPlayer.get().startSample(this.dataStore.getCurrentLoop().getClickTrack(), false);
		}
	}

	public void setChannelRecording(Long channelId, Boolean recording) {
		if (recording) {
			this.audioSources.get(channelId).startRecord();
		} else {
			this.audioSources.get(channelId).stopRecord();
		}
	}

	public RecordingState getChannelRecordingState(Long channelId) {
		return this.audioSources.get(channelId).getRecordingState();
	}

	public boolean getChannelMonitoring(Long channelId) {
		return this.audioSources.get(channelId).isMonitoring();
	}

	public void setChannelMonitoring(Long channelId, boolean monitoring) {
		this.audioSources.get(channelId).setMonitoring(monitoring);
	}

	public void setSampleMute(Long loopId, Long sampleId, boolean mute) {
		Optional<AudioSink> firstPlayer = FluentIterable.from(this.audioSinks.values()).first();
		if (firstPlayer.isPresent()) {
			setSampleMute(loopId, sampleId, firstPlayer.get(), mute);
		}
	}

	public void setSampleMute(Long loopId, Long sampleId, Long playerId, boolean mute) {
		AudioSink player = audioSinks.get(playerId);
		setSampleMute(loopId, sampleId, player, mute);
	}

	public void setSampleMute(Long loopId, Long sampleId, AudioSink player, boolean mute) {
		log.info("setSampleMute [loopId=" + loopId + "][sampleId=" + sampleId + "][mute=" + mute + "]");
		if (player != null) {
			sampleMutator.setMute(loopId, sampleId, mute, player, true);
		}
	}

	@Override
	public void onDataEvent(DataEvent event) {
		if (event instanceof ChannelCreated) {
			ChannelCreated channelCreated = (ChannelCreated) event;
			Channel channel = channelCreated.getChannel();
			if (channel instanceof InputChannel) {
				InputChannel inputChannel = (InputChannel) channel;
				if (!this.audioSources.containsKey(channel.getId())) {
					AudioSource audioSource = Application.injector.getInstance(AudioSource.class);
					audioSource.setInputChannel(inputChannel);
					audioSource.initialize();
					this.audioSources.put(inputChannel.getId(), audioSource);
				}
			} else if (channel instanceof OutputChannel) {
				OutputChannel outputChannel = (OutputChannel) channel;
				if (!this.audioSinks.containsKey(outputChannel.getId())) {
					AudioSink audioSink = Application.injector.getInstance(AudioSink.class);
					this.audioSinks.put(outputChannel.getId(), audioSink);
				}
			}
		}
	}
}
