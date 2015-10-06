package ch.sulco.yal.web;

import static spark.Spark.get;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import spark.Spark;
import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.event.Event;
import ch.sulco.yal.event.EventListener;
import ch.sulco.yal.event.EventManager;

import com.google.gson.Gson;

@Singleton
public class Server implements EventListener {
	private final static Logger log = Logger.getLogger(Server.class.getName());

	@Inject
	private Processor audioProcessor;

	@Inject
	private EventManager eventManager;

	private final Gson gson = new Gson();

	public Server() {

		Spark.staticFileLocation("/public");

		Spark.webSocket("/updates", UpdatesWebSocket.class);

		get("/play", (req, res) -> this.play());
		get("/loop", (req, res) -> this.loop());
		get("/length", (req, res) -> this.getLoopLength());
		get("/samples", (req, res) -> this.getSamples());
		get("/channels", (req, res) -> this.getChannels());

		get("/record/:channelId/:enabled",
				(req, res) -> this.setRecord(Integer.parseInt(req.params(":channelId")), Boolean.parseBoolean(req.params(":enabled"))));

		get("/monitor/:channelId/:enabled",
				(req, res) -> this.setMonitoring(Integer.parseInt(req.params(":channelId")), Boolean.parseBoolean(req.params(":enabled"))));

		get("/volume/:sampleId/:volume",
				(req, res) -> this.setVolume(Integer.parseInt(req.params(":sampleId")), Float.parseFloat(req.params(":volume"))));

		get("/sample/play/:sampleId", (req, res) -> this.playSample(Integer.parseInt(req.params(":sampleId"))));
	}

	@PostConstruct
	public void setup() {
		this.eventManager.addListener(this);
	}

	private String playSample(int sampleId) {
		this.audioProcessor.setSampleMute(sampleId, false);
		return "Success";
	}

	private String getLoopLength() {
		Long loopLength = this.audioProcessor.getLoopLength();
		return loopLength == null ? "" : loopLength.toString();
	}

	private String setRecord(int channelId, boolean enabled) {
		log.info("Record [channelId=" + channelId + "][enabled=" + enabled + "]");
		this.audioProcessor.setChannelRecording(channelId, enabled);
		return "Success";
	}

	private String setMonitoring(int channelId, boolean enabled) {
		log.info("Monitoring [channelId=" + channelId + "][enabled=" + enabled + "]");
		this.audioProcessor.setChannelMonitoring(channelId, enabled);
		return "Success";
	}

	private String setVolume(int sampleId, float volume) {
		this.audioProcessor.setSampleVolume(sampleId, volume);
		return "Success";
	}

	private String getSamples() {
		Set<Integer> sampleIds = this.audioProcessor.getSampleIds();
		List<ch.sulco.yal.dm.Sample> samples = new ArrayList<>();
		for (int id : sampleIds) {
			Sample sample = new Sample();
			sample.setId(Long.valueOf(id));
			sample.setMute(this.audioProcessor.isSampleMute(id));
			sample.setGain(this.audioProcessor.getSampleVolume(id));
			samples.add(sample);
		}
		return this.gson.toJson(samples);
	}

	private String getChannels() {
		Set<Integer> channelIds = this.audioProcessor.getChannelIds();
		List<Channel> channels = new ArrayList<>();
		for (Integer id : channelIds) {
			InputChannel ch = new InputChannel();
			ch.setId(Long.valueOf(id));
			ch.setRecordingState(this.audioProcessor.getChannelRecordingState(id));
			ch.setName("Channel " + id);
			ch.setMonitoring(this.audioProcessor.getChannelMonitoring(id));
			ch.setLevel(0.0);
			channels.add(ch);
		}
		return this.gson.toJson(channels);
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
}
