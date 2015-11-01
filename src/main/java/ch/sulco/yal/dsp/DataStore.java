package ch.sulco.yal.dsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.Application;
import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.LoopState;
import ch.sulco.yal.dm.Mapping;
import ch.sulco.yal.dm.Sample;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

@Singleton
public class DataStore {
	private final static Logger log = LoggerFactory.getLogger(DataStore.class);

	private final List<Loop> loops = new ArrayList<>();
	private final List<Sample> samples = new ArrayList<>();
	private final List<Channel> channels = new ArrayList<>();
	private final List<Mapping> mappings = new ArrayList<>();

	private final List<DataEventListener> listeners = new ArrayList<>();

	public void setup() {
		log.info("Setup");

		try {
			this.mappings.addAll(Lists.newArrayList(
					new Gson().fromJson(
							new FileReader(DataStore.class.getResource("/data/mappings.json").getFile()),
							Mapping[].class)));
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			log.error("Unable to load mapping", e);
			throw new RuntimeException("Unable to load mapping", e);
		}
		try {
			this.loops.addAll(Lists.newArrayList(
					new Gson().fromJson(
							new FileReader(new File("loops.json")),
							Loop[].class)));
			log.info("loops loaded: " + loops);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			log.error("Unable to load loop", e);
		}
		try {
			this.samples.addAll(Lists.newArrayList(
					new Gson().fromJson(
							new FileReader(new File("samples.json")),
							Sample[].class)));
			log.info("samples loaded: " + samples);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			log.error("Unable to load sample", e);
		}
		for (Sample sample : this.samples) {
			Path path = Paths.get(sample.getId() + ".sample");
			try {
				sample.setData(Files.readAllBytes(path));
				loops.stream().filter(l -> l.getSample(sample.getId()) != null)
						.forEach(l -> l.getSample(sample.getId()).setData(sample.getData()));
				sample.setLoop(loops.stream().filter(l -> l.getSample(sample.getId()) != null).findFirst().get());
				log.info("sample data [" + sample.getId() + "][" + sample.getData().length + "]");
			} catch (IOException e) {
				log.error("Unable to load data for sample [" + sample.getId() + "]");
			}
		}
		if (loops.isEmpty()) {
			Loop loop = Application.injector.getInstance(Loop.class);
			loop.setId(0L);
			loop.setActive(true);
			loop.setLoopState(LoopState.STOPPED);
			this.createLoop(loop);
		}
	}

	@SuppressWarnings("resource")
	public void persistData() {
		log.info("Persist data");
		Gson gson = new Gson();
		try {
			String loopsJson = gson.toJson(this.loops);
			log.info("persist loops: " + loopsJson);
			FileWriter fileWriter = new FileWriter(new File("loops.json"));
			fileWriter.write(loopsJson);
			fileWriter.close();
		} catch (IOException e) {
			log.error("Unable to persist loops", e);
		}
		try {
			String json = gson.toJson(this.samples);
			log.info("persist samples: " + json);
			FileWriter fileWriter = new FileWriter(new File("samples.json"));
			fileWriter.write(json);
			fileWriter.close();
		} catch (IOException e) {
			log.error("Unable to persist samples", e);
		}

		for (Sample sample : samples) {
			try {
				log.info("Store sample data [" + sample.getId() + "]");
				char[] data = new char[sample.getData().length];
				for (int i = 0; i < sample.getData().length; i++) {
					data[i] = (char) sample.getData()[i];
				}
				new FileWriter(new File(sample.getId() + ".sample")).write(data);
			} catch (IOException e) {
				log.error("Unable to persist samples [" + sample.getId() + "]", e);
			}
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

	public void addListener(DataEventListener listener) {
		this.listeners.add(listener);
	}

	private void addEvent(DataEvent event) {
		for (DataEventListener listener : this.listeners) {
			listener.onDataEvent(event);
		}
	}

	public interface DataEventListener {
		void onDataEvent(DataEvent event);
	}

	public class DataEvent {
		private final Date creationDate = new Date();

		private final String eventType = this.getClass().getSimpleName();

		public Date getCreationDate() {
			return this.creationDate;
		}

		public String getEventType() {
			return eventType;
		}
	}

	public final class ChannelCreated extends DataEvent {
		private final Channel channel;

		ChannelCreated(Channel channel) {
			super();
			this.channel = channel;
		}

		public Channel getChannel() {
			return channel;
		}
	}

	public final class ChannelUpdated extends DataEvent {
		private final Channel channel;

		ChannelUpdated(Channel channel) {
			super();
			this.channel = channel;
		}

		public Channel getChannel() {
			return channel;
		}
	}

	public final class LoopCreated extends DataEvent {
		private final Loop loop;

		LoopCreated(Loop loop) {
			super();
			this.loop = loop;
		}

		public Loop getLoop() {
			return this.loop;
		}
	}

	public final class LoopUpdated extends DataEvent {
		private final Loop loop;

		LoopUpdated(Loop loop) {
			super();
			this.loop = loop;
		}

		public Loop getLoop() {
			return loop;
		}
	}

	public final class SampleCreated extends DataEvent {
		private final Sample sample;

		SampleCreated(Sample sample) {
			super();
			this.sample = sample;
		}

		public Sample getSample() {
			return sample;
		}
	}

	public final class SampleUpdated extends DataEvent {
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
