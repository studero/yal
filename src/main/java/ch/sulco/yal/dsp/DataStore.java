package ch.sulco.yal.dsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import ch.sulco.yal.Application;
import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.LoopState;
import ch.sulco.yal.dm.Mapping;
import ch.sulco.yal.dm.MappingMethodArgument;
import ch.sulco.yal.dm.Sample;

@Singleton
public class DataStore {
	private final static Logger log = LoggerFactory.getLogger(DataStore.class);

	private final List<Loop> loops = new ArrayList<>();
	private final List<Channel> channels = new ArrayList<>();
	private final List<Mapping> mappings = new ArrayList<>();

	public void setup() {
		log.info("Setup");
		Loop loop = Application.injector.getInstance(Loop.class);
		// Loop loop = new Loop();
		loop.setId(0L);
		loop.setActive(true);
		loop.setLoopState(LoopState.STOPPED);
		this.addLoop(loop);

		Mapping channelRecordingMapping = new Mapping();
		channelRecordingMapping.setSource("nanoKontrol2");
		channelRecordingMapping.setProcessorMethodArguments(new LinkedList<>());
		channelRecordingMapping.getProcessorMethodArguments().add(new MappingMethodArgument("channelId", Long.class.getName()));
		channelRecordingMapping.getProcessorMethodArguments().add(new MappingMethodArgument("recording", Boolean.class.getName()));
		channelRecordingMapping.setTriggerValueMap(new HashMap<>());
		channelRecordingMapping.getTriggerValueMap().put("command", 176);
		channelRecordingMapping.getTriggerValueMap().put("channel", 0);
		channelRecordingMapping.getTriggerValueMap().put("data1", 64);
		channelRecordingMapping.setValueExpressionMap(new HashMap<>());
		channelRecordingMapping.getValueExpressionMap().put("channelId", "#{data1} - 64");
		channelRecordingMapping.getValueExpressionMap().put("recording", "#{data2} > 0");
		channelRecordingMapping.setProcessorMethod("setChannelRecording");
		this.mappings.add(channelRecordingMapping);

		Mapping playMapping = new Mapping();
		playMapping.setSource("nanoKontrol2");
		playMapping.setTriggerValueMap(new HashMap<>());
		playMapping.getTriggerValueMap().put("command", 176);
		playMapping.getTriggerValueMap().put("channel", 0);
		playMapping.getTriggerValueMap().put("data1", 41);
		playMapping.getTriggerValueMap().put("data2", 0);
		playMapping.setProcessorMethod("play");
		this.mappings.add(playMapping);

		Mapping loopMapping = new Mapping();
		loopMapping.setSource("nanoKontrol2");
		loopMapping.setTriggerValueMap(new HashMap<>());
		loopMapping.getTriggerValueMap().put("command", 176);
		loopMapping.getTriggerValueMap().put("channel", 0);
		loopMapping.getTriggerValueMap().put("data1", 42);
		loopMapping.getTriggerValueMap().put("data2", 0);
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

	public List<Mapping> getMappings() {
		return this.mappings;
	}

}
