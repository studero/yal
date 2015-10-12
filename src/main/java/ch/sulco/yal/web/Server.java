package ch.sulco.yal.web;

import static spark.Spark.get;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;
import ch.sulco.yal.PostConstructModule;
import ch.sulco.yal.SimulationYalModule;
import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.event.Event;
import ch.sulco.yal.event.EventListener;
import ch.sulco.yal.event.EventManager;

import com.google.common.collect.FluentIterable;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Singleton
public class Server implements EventListener {
	private final static Logger log = LoggerFactory.getLogger(Server.class);

	@Inject
	private Processor audioProcessor;

	@Inject
	private EventManager eventManager;

	@Inject
	private DataStore dataStore;

	private final Gson gson = new Gson();

	public Server() {

		Spark.staticFileLocation("/public");

		Spark.webSocket("/updates", UpdatesWebSocket.class);

		get("/play", (req, res) -> this.play());
		get("/loop", (req, res) -> this.loop());
		get("/length", (req, res) -> this.getLoopLength());
		get("/channels", (req, res) -> this.getChannels());
		get("/loops", (req, res) -> this.getLoops());

		get("/record/:channelId/:enabled",
				(req, res) -> this.setRecord(Long.valueOf(req.params(":channelId")), Boolean.parseBoolean(req.params(":enabled"))));

		get("/monitor/:channelId/:enabled",
				(req, res) -> this.setMonitoring(Long.parseLong(req.params(":channelId")), Boolean.parseBoolean(req.params(":enabled"))));

		get("/volume/:sampleId/:volume",
				(req, res) -> this.setVolume(Long.parseLong(req.params(":sampleId")), Float.parseFloat(req.params(":volume"))));

		get("/sample/play/:sampleId", (req, res) -> this.playSample(Long.parseLong(req.params(":sampleId"))));

		get("/sample/:sampleId/:mute",
				(req, res) -> this.setSampleMute(Long.parseLong(req.params(":sampleId")), Boolean.parseBoolean(req.params(":mute"))));
	}

	@PostConstruct
	public void setup() {
		this.eventManager.addListener(this);
	}

	private String getLoops() {
		return this.gson.toJson(this.dataStore.getLoops());
	}

	private String playSample(Long sampleId) {
		this.audioProcessor.setSampleMute(sampleId, false);
		return "Success";
	}

	private String getLoopLength() {
		return this.dataStore.getCurrentLoop().getLength().toString();
	}

	private String setRecord(Long channelId, boolean enabled) {
		log.info("Record [channelId=" + channelId + "][enabled=" + enabled + "]");
		this.audioProcessor.setChannelRecording(channelId, enabled);
		return "Success";
	}

	private String setSampleMute(Long sampleId, boolean mute) {
		log.info("SampleMute [sampleId=" + sampleId + "][mute=" + mute + "]");
		this.audioProcessor.setSampleMute(sampleId, mute);
		return "Success";
	}

	private String setMonitoring(Long channelId, boolean enabled) {
		log.info("Monitoring [channelId=" + channelId + "][enabled=" + enabled + "]");
		this.audioProcessor.setChannelMonitoring(channelId, enabled);
		return "Success";
	}

	private String setVolume(Long sampleId, float volume) {
		this.audioProcessor.setSampleVolume(sampleId, volume);
		return "Success";
	}

	private String getChannels() {
		FluentIterable<Channel> channels = FluentIterable.from(this.dataStore.getChannels());
		channels.forEach(new Consumer<Channel>() {
			@Override
			public void accept(Channel t) {
				t.setName("Channel " + t.getId());
			}
		});
		return this.gson.toJson(channels.toList());
	}

	private String play() {
		this.audioProcessor.play();
		return "Success";
	}

	private String loop() {
		this.audioProcessor.loop();
		return "Success";
	}

	@Override
	public void onEvent(Event event) {
		UpdatesWebSocket.getInstance().send(this.gson.toJson(event));
	}

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new SimulationYalModule(), new PostConstructModule());
		Server server = injector.getInstance(Server.class);
	}
}
