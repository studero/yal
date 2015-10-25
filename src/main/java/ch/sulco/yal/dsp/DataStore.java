package ch.sulco.yal.dsp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import ch.sulco.yal.event.Event;
import ch.sulco.yal.event.EventListener;

@Singleton
public class DataStore {
	private final static Logger log = LoggerFactory.getLogger(DataStore.class);

	private final List<Loop> loops = new ArrayList<>();
	private final List<Sample> samples = new ArrayList<>();
	private final List<Channel> channels = new ArrayList<>();
	private final List<Mapping> mappings = new ArrayList<>();

	private final List<EventListener> listeners = new ArrayList<>();

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
		this.addEvent(new LoopCreated(loop));
	}

	public void updateLoop(Loop loop) {
		this.loops.remove(getLoop(loop.getId()));
		this.loops.add(loop);
		this.addEvent(new LoopUpdated(loop));
	}

	public Sample getSample(Long id) {
		return FluentIterable.from(this.samples).firstMatch(new Predicate<Sample>() {
			@Override
			public boolean apply(Sample input) {
				return id == input.getId();
			}
		}).orNull();
	}

	public void createSample(Sample sample) {
		samples.add(sample);
		this.addEvent(new SampleCreated(sample));
	}

	public void updateSample(Sample sample) {
		this.samples.remove(getSample(sample.getId()));
		this.samples.add(sample);
		this.addEvent(new SampleUpdated(sample));
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
		this.addEvent(new ChannelCreated(channel));
	}

	public void updateChannel(Channel channel) {
		this.channels.remove(getChannel(channel.getId()));
		this.channels.add(channel);
		this.addEvent(new ChannelUpdated(channel));
	}

	public List<Mapping> getMappings() {
		return this.mappings;
	}

	public void addListener(EventListener listener) {
		this.listeners.add(listener);
	}

	private void addEvent(Event event) {
		for (EventListener listener : this.listeners) {
			listener.onEvent(event);
		}
	}

	public final class ChannelCreated extends Event {
		private final Channel channel;

		ChannelCreated(Channel channel) {
			super();
			this.channel = channel;
		}

		public Channel getChannel() {
			return channel;
		}
	}

	public final class ChannelUpdated extends Event {
		private final Channel channel;

		ChannelUpdated(Channel channel) {
			super();
			this.channel = channel;
		}

		public Channel getChannel() {
			return channel;
		}
	}

	public final class LoopCreated extends Event {
		private final Loop loop;

		LoopCreated(Loop loop) {
			super();
			this.loop = loop;
		}

		public Loop getLoop() {
			return this.loop;
		}
	}

	public final class LoopUpdated extends Event {
		private final Loop loop;

		LoopUpdated(Loop loop) {
			super();
			this.loop = loop;
		}

		public Loop getLoop() {
			return loop;
		}
	}

	public final class SampleCreated extends Event {
		private final Sample sample;

		SampleCreated(Sample sample) {
			super();
			this.sample = sample;
		}

		public Sample getSample() {
			return sample;
		}
	}

	public final class SampleUpdated extends Event {
		private final Sample sample;

		SampleUpdated(Sample sample) {
			super();
			this.sample = sample;
		}

		public Sample getSample() {
			return sample;
		}
	}
}
