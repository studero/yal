package ch.sulco.yal.web;

import static spark.Spark.get;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gson.Gson;

import ch.sulco.yal.controller.Channel;
import ch.sulco.yal.controller.Sample;
import ch.sulco.yal.dsp.Application;
import spark.Spark;

public class Server {
	private final static Logger log = Logger.getLogger(Server.class.getName());

	private final Application dspApplication;

	private final Gson gson = new Gson();

	public Server(Application dspApplication) {
		this.dspApplication = dspApplication;

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
		this.dspApplication.getAudioProcessor().setSampleMute(sampleId, false);
		return "Success";
	}

	private String getLoopLength() {
		Long loopLength = this.dspApplication.getAudioProcessor()
				.getLoopLength();
		return loopLength == null ? "" : loopLength.toString();
	}

	private String setRecord(int channelId, boolean enabled) {
		log.info("Record [channelId=" + channelId + "][enabled=" + enabled
				+ "]");
		this.dspApplication.getAudioProcessor().setChannelRecording(channelId,
				enabled);
		return "Success";
	}

	private String setVolume(int sampleId, float volume) {
		this.dspApplication.getAudioProcessor().setSampleVolume(sampleId,
				volume);
		return "Success";
	}

	private String getSamples() {
		Set<Integer> sampleIds = this.dspApplication.getAudioProcessor()
				.getSampleIds();
		List<Sample> samples = new ArrayList<>();
		for (int id : sampleIds) {
			Sample sample = new Sample();
			sample.setId(id);
			sample.setMute(this.dspApplication.getAudioProcessor()
					.isSampleMute(id));
			sample.setVolume(this.dspApplication.getAudioProcessor()
					.getSampleVolume(id));
			samples.add(sample);
		}
		return this.gson.toJson(samples);
	}

	private String getChannels() {
		Set<Integer> channelIds = this.dspApplication.getAudioProcessor()
				.getChannelIds();
		List<Channel> channels = new ArrayList<>();
		for (int id : channelIds) {
			Channel ch = new Channel();
			ch.setId(id);
			ch.setRecordingState(this.dspApplication.getAudioProcessor()
					.getChannelRecordingState(id));
			ch.setName("Channel " + id);
			channels.add(ch);
		}
		return this.gson.toJson(channels);
	}

	private String play() {
		this.dspApplication.getAudioProcessor().play();
		return "Success";
	}

	private String loop() {
		this.dspApplication.getAudioProcessor().loop();
		return "Success";
	}
}
