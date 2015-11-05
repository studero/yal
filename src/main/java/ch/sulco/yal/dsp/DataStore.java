package ch.sulco.yal.dsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ch.sulco.yal.AppConfig;
import ch.sulco.yal.Application;
import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.LooperState;
import ch.sulco.yal.dm.Mapping;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.settings.AudioSettings;
import ch.sulco.yal.settings.Settings;

@Singleton
public class DataStore {
	private final static Logger log = LoggerFactory.getLogger(DataStore.class);

	private static final String AUDIO_SETTINGS_FILENAME = "audio.json";

	@Inject
	private AppConfig appConfig;

	private LooperState looperState = LooperState.STOPPED;
	private Long nextLoopId;

	private final List<Loop> loops = new ArrayList<>();
	private final List<Channel> channels = new ArrayList<>();
	private final List<Mapping> mappings = new ArrayList<>();

	private Settings settings;

	private final List<DataEventListener> listeners = new ArrayList<>();

	public void setup() {
		log.info("Setup");

		this.getSettings()
				.setAudioSettings(load(Paths.get(this.appConfig.getSettingsPath(), AUDIO_SETTINGS_FILENAME), AudioSettings.class));

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
							new FileReader(new File(appConfig.getDataPath() + "/loops.json")),
							Loop[].class)));
			log.info("loops loaded: " + loops);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			log.error("Unable to load loop", e);
		}
		for (Loop loop : this.loops) {
			for (Sample sample : loop.getSamples()) {
				Path path = Paths.get(appConfig.getDataPath() + "/" + sample.getId() + ".sample");
				try {
					sample.setData(Files.readAllBytes(path));
					loops.stream().filter(l -> l.getSample(sample.getId()) != null)
							.forEach(l -> l.getSample(sample.getId()).setData(sample.getData()));
					log.info("sample data [" + sample.getId() + "][" + sample.getData().length + "]");
				} catch (IOException e) {
					log.error("Unable to load data for sample [" + sample.getId() + "]");
				}
			}
		}
		if (loops.isEmpty()) {
			Loop loop = Application.injector.getInstance(Loop.class);
			loop.setId(0L);
			loop.setActive(true);
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
			FileWriter fileWriter = new FileWriter(new File(appConfig.getDataPath() + "/loops.json"));
			fileWriter.write(loopsJson);
			fileWriter.close();
		} catch (IOException e) {
			log.error("Unable to persist loops", e);
		}

		for (Loop loop : loops) {
			for (Sample sample : loop.getSamples()) {
				try {
					log.info("Store sample data [" + sample.getId() + "]");
					char[] data = new char[sample.getData().length];
					for (int i = 0; i < sample.getData().length; i++) {
						data[i] = (char) sample.getData()[i];
					}
					new FileWriter(new File(appConfig.getDataPath() + "/" + sample.getId() + ".sample")).write(data);
				} catch (IOException e) {
					log.error("Unable to persist samples [" + sample.getId() + "]", e);
				}
			}
		}

		save(Paths.get(this.appConfig.getSettingsPath(), AUDIO_SETTINGS_FILENAME), this.settings.getAudioSettings());
	}

	public Settings getSettings() {
		if (settings == null) {
			settings = new Settings();
		}
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	private <T> T load(Path path, Class<T> clazz) {
		try {
			return new Gson().fromJson(Files.newBufferedReader(path), clazz);
		} catch (Exception e) {
			log.error("Unable to load data: " + e.getMessage());
			return null;
		}
	}

	public <T> void save(Path path, T data) {
		try {
			Files.write(path, new Gson().toJson(data).getBytes(),
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			log.error("Unable to save data", e);
		}
	}

	public List<Loop> getLoops() {
		return this.loops;
	}

	public Loop getCurrentLoop() {
		return loops.stream().filter(l -> l.isActive()).findFirst().orElse(null);
	}

	public Sample getCurrentLoopSample(long sampleId) {
		return getCurrentLoop().getSamples().stream().filter(s -> Objects.equals(sampleId, s.getId())).findFirst().orElse(null);
	}

	public Loop getLoop(Long id) {
		return loops.stream().filter(l -> Objects.equals(id, l.getId())).findFirst().orElse(null);
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

	public Sample getSample(Long loopId, Long sampleId) {
		return loops.stream().filter(l -> Objects.equals(l.getId(), loopId)).findFirst().map(l -> l.getSample(sampleId)).get();
	}

	public void createSample(Long loopId, Sample sample) {
		Loop loop = getLoop(loopId);
		loop.addSample(sample);
		this.addEvent(new LoopUpdated(loop));
	}

	public void updateSample(Long loopId, Sample sample) {
		Loop loop = getLoop(loopId);
		loop.updateSample(sample);
		this.addEvent(new LoopUpdated(loop));
	}

	public List<Channel> getChannels() {
		return this.channels;
	}

	public Channel getChannel(final Long id) {
		return channels.stream().filter(c -> Objects.equals(id, c.getId())).findFirst().orElse(null);
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

	public LooperState getLooperState() {
		return looperState;
	}

	public void setLooperState(LooperState looperState) {
		this.looperState = looperState;
		addEvent(new LooperStateUpdated(looperState));
	}

	public Long getNextLoopId() {
		return nextLoopId;
	}

	public void setNextLoopId(Long nextLoopId) {
		this.nextLoopId = nextLoopId;
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

	public final class LooperStateUpdated extends DataEvent {
		private final LooperState looperState;

		LooperStateUpdated(LooperState looperState) {
			super();
			this.looperState = looperState;
		}

		public LooperState getLooperState() {
			return looperState;
		}
	}
}
