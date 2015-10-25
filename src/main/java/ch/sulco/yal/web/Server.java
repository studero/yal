package ch.sulco.yal.web;

import static spark.Spark.get;
import static spark.Spark.put;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.LoopActivator;
import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.event.Event;
import ch.sulco.yal.event.EventListener;
import spark.Spark;

@Singleton
public class Server implements EventListener {
	private final static Logger log = LoggerFactory.getLogger(Server.class);

	@Inject
	private Processor audioProcessor;

	@Inject
	private DataStore dataStore;

	@Inject
	private LoopActivator loopActivator;

	private final Gson gson = new Gson();

	public Server() {

		Spark.staticFileLocation("/public");

		Spark.webSocket("/updates", UpdatesWebSocket.class);

		get("/play", (req, res) -> this.play());
		get("/pause", (req, res) -> this.pause());
		get("/stop", (req, res) -> this.stop());
		get("/loop", (req, res) -> this.loop());
		get("/length", (req, res) -> this.getLoopLength());
		get("/channels", (req, res) -> this.getChannels());
		get("/loops", (req, res) -> this.getLoops());

		put("/activateLoop/:loopId", (req, res) -> this.activateLoop(Long.valueOf(req.params(":loopId"))));

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
		this.dataStore.addListener(this);
	}

	private String activateLoop(Long loopId) {
		this.loopActivator.setCurrentLoopId(loopId);
		return "Success";
	}

	private String getLoops() {
		return this.gson.toJson(this.dataStore.getLoops());
	}

	private String playSample(Long sampleId) {
		this.audioProcessor.setSampleMute(sampleId, false);
		return "Success";
	}

	private String getLoopLength() {
		return this.dataStore.getCurrentLoop().getTimeLength().toString();
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
		this.dataStore.getCurrentLoopSample(sampleId).setGain(volume);
		return "Success";
	}

	private String getChannels() {
		return this.gson.toJson(this.dataStore.getChannels());
	}

	private String play() {
		this.audioProcessor.play();
		return "Success";
	}

	private String pause() {
		this.audioProcessor.pause();
		return "Success";
	}

	private String stop() {
		this.audioProcessor.stop();
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
}
