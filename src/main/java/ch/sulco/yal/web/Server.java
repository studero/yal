package ch.sulco.yal.web;

import static spark.Spark.get;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import spark.Spark;
import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.web.dm.Channel;
import ch.sulco.yal.web.dm.Sample;

import com.google.gson.Gson;

public class Server {
	private final static Logger log = Logger.getLogger(Server.class.getName());

	private final Processor audioProcessor;

	private final Gson gson = new Gson();

	public Server(Processor audioProcessor) {
		this.audioProcessor = audioProcessor;

		Spark.staticFileLocation("/public");

		get("/play", (req, res) -> this.play());
		get("/loop", (req, res) -> this.loop());
		get("/length", (req, res) -> this.getLoopLength());
		get("/samples", (req, res) -> this.getSamples());
		get("/channels", (req, res) -> this.getChannels());

		get("/record/:channelId/:enabled",
				(req, res) -> this.setRecord(
						Integer.parseInt(req.params(":channelId")),
						Boolean.parseBoolean(req.params(":enabled"))));

		get("/volume/:sampleId/:volume",
				(req, res) -> this.setVolume(
						Integer.parseInt(req.params(":sampleId")),
						Float.parseFloat(req.params(":volume"))));

		get("/sample/play/:sampleId", (req, res) -> this.playSample(Integer
				.parseInt(req.params(":sampleId"))));
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
		log.info("Record [channelId=" + channelId + "][enabled=" + enabled
				+ "]");
		this.audioProcessor.setChannelRecording(channelId, enabled);
		return "Success";
	}

	private String setVolume(int sampleId, float volume) {
		this.audioProcessor.setSampleVolume(sampleId, volume);
		return "Success";
	}

	private String getSamples() {
		Set<Integer> sampleIds = this.audioProcessor.getSampleIds();
		List<Sample> samples = new ArrayList<>();
		for (int id : sampleIds) {
			Sample sample = new Sample();
			sample.setId(id);
			sample.setMute(this.audioProcessor.isSampleMute(id));
			sample.setVolume(this.audioProcessor.getSampleVolume(id));
			samples.add(sample);
		}
		return this.gson.toJson(samples);
	}

	private String getChannels() {
		Set<Integer> channelIds = this.audioProcessor				.getChannelIds();
		List<Channel> channels = new ArrayList<>();
		for (int id : channelIds) {
			Channel ch = new Channel();
			ch.setId(id);
			ch.setRecordingState(this.audioProcessor					.getChannelRecordingState(id));
			ch.setName("Channel " + id);
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
}
