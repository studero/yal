package ch.sulco.yal.dsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.LoopState;
import ch.sulco.yal.dm.Mapping;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.event.EventManager;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Singleton
public class DataStore {
	private final static Logger log = LoggerFactory.getLogger(DataStore.class);

	@Inject
	private EventManager eventManager;

	private final List<Loop> loops = new ArrayList<>();
	private final List<Channel> channels = new ArrayList<>();
	private final List<Mapping> mappings = new ArrayList<>();

	public void setup() {
		log.info("Setup");
		Loop loop = new Loop();
		loop.setId(0L);
		loop.setActive(true);
		loop.setLoopState(LoopState.STOPPED);
		this.addLoop(loop);

		Mapping channelRecordingMapping = new Mapping();
		channelRecordingMapping.setSource("nanoKontrol2");
		Map<String, Object> triggerValueMap = new HashMap<>();
		triggerValueMap.put("command", 176);
		triggerValueMap.put("channel", 0);
		triggerValueMap.put("data1", 64);
		channelRecordingMapping.setTriggerValueMap(triggerValueMap);
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("data2", "recording");
		channelRecordingMapping.setValueMap(valueMap);
		channelRecordingMapping.setProcessorMethod("setChannelRecording");
		this.mappings.add(channelRecordingMapping);

		Mapping playMapping = new Mapping();
		playMapping.setSource("nanoKontrol2");
		Map<String, Object> triggerValueMap2 = new HashMap<>();
		triggerValueMap2.put("command", 176);
		triggerValueMap2.put("channel", 0);
		triggerValueMap2.put("data1", 41);
		triggerValueMap2.put("data2", 0);
		playMapping.setTriggerValueMap(triggerValueMap2);
		Map<String, String> valueMap2 = new HashMap<>();
		playMapping.setValueMap(valueMap2);
		playMapping.setProcessorMethod("play");
		this.mappings.add(playMapping);

		Mapping loopMapping = new Mapping();
		loopMapping.setSource("nanoKontrol2");
		Map<String, Object> triggerValueMap3 = new HashMap<>();
		triggerValueMap3.put("command", 176);
		triggerValueMap3.put("channel", 0);
		triggerValueMap3.put("data1", 42);
		triggerValueMap3.put("data2", 0);
		loopMapping.setTriggerValueMap(triggerValueMap3);
		Map<String, String> valueMap3 = new HashMap<>();
		loopMapping.setValueMap(valueMap3);
		loopMapping.setProcessorMethod("loop");
		this.mappings.add(loopMapping);
	}

	public List<Loop> getLoops() {
		return this.loops;
	}

	public Loop getCurrentLoop() {
		return FluentIterable.from(this.loops).firstMatch(new Predicate<Loop>() {
			@Override
			public boolean apply(Loop input) {
				return input.isActive();
			}
		}).orNull();
	}

	public Sample getCurrentLoopSample(long sampleId) {
		return FluentIterable.from(this.getCurrentLoop().getSamples()).firstMatch(new Predicate<Sample>() {
			@Override
			public boolean apply(Sample input) {
				return input.getId() == sampleId;
			}
		}).orNull();
	}

	public Loop getLoop(final Long id) {
		return FluentIterable.from(this.loops).firstMatch(new Predicate<Loop>() {
			@Override
			public boolean apply(Loop input) {
				return id == input.getId();
			}
		}).orNull();
	}

	public void addLoop(Loop loop) {
		this.loops.add(loop);
	}

	public void addSample(Long loopId, Sample sample) {
		this.getLoop(loopId).getSamples().add(sample);
	}

	public List<Channel> getChannels() {
		return this.channels;
	}

	public Channel getChannel(final Long id) {
		return FluentIterable.from(this.channels).firstMatch(new Predicate<Channel>() {
			@Override
			public boolean apply(Channel input) {
				return Objects.equals(id, input.getId());
			}
		}).orNull();
	}

	public void addChannel(Channel channel) {
		this.channels.add(channel);
	}

	public void setCurrentLoopId(Long currentLoopId) {
		Loop before = this.getCurrentLoop();
		Loop after = this.getLoop(currentLoopId);
		if (before != null)
			before.setActive(false);
		after.setActive(true);
		if (before != null)
			this.eventManager.updateLoop(before);
		this.eventManager.updateLoop(after);
	}

	public List<Mapping> getMappings() {
		return this.mappings;
	}

}
