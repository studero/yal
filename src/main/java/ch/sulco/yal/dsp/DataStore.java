package ch.sulco.yal.dsp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ch.sulco.yal.Application;
import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.LoopState;
import ch.sulco.yal.dm.Mapping;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.event.EventManager;

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
		Loop loop = Application.injector.getInstance(Loop.class);
		// Loop loop = new Loop();
		loop.setId(0L);
		loop.setActive(true);
		loop.setLoopState(LoopState.STOPPED);
		this.createLoop(loop);

		try {
			this.mappings.addAll(Lists.newArrayList(
					new Gson().fromJson(
							new FileReader(DataStore.class.getResource("/data/mappings.json").getFile()),
							Mapping[].class)));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			log.error("Unable to load mapping", e);
			throw new RuntimeException("Unable to load mapping", e);
		}
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

	public Loop getLoop(Long id) {
		return FluentIterable.from(this.loops).firstMatch(new Predicate<Loop>() {
			@Override
			public boolean apply(Loop input) {
				return id == input.getId();
			}
		}).orNull();
	}

	public void createLoop(Loop loop) {
		this.loops.add(loop);
		this.eventManager.createLoop(loop);
	}

	public void updateLoop(Loop loop) {
		this.loops.remove(getLoop(loop.getId()));
		this.loops.add(loop);
		this.eventManager.updateLoop(loop);
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

	public void createChannel(Channel channel) {
		this.channels.add(channel);
		this.eventManager.createChannel(channel);
	}

	public void updateChannel(Channel channel) {
		this.channels.remove(getChannel(channel.getId()));
		this.channels.add(channel);
		this.eventManager.updateChannel(channel);
	}

	public List<Mapping> getMappings() {
		return this.mappings;
	}

}
