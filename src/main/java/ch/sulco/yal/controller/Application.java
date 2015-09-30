package ch.sulco.yal.controller;

import static spark.Spark.get;

import java.util.logging.Logger;

import spark.Spark;
import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;
import ch.sulco.yal.dsp.audio.onboard.LoopStore;
import ch.sulco.yal.dsp.audio.onboard.OnboardProcessor;
import ch.sulco.yal.dsp.audio.onboard.Player;
import ch.sulco.yal.dsp.audio.onboard.Recorder;
import ch.sulco.yal.dsp.cmd.SocketCommandReceiver;

import com.google.gson.Gson;

public class Application {
	
	private final static Logger log = Logger.getLogger(Application.class.getName());
	
	private final ch.sulco.yal.dsp.Application dspApplication;
	
	private final Gson gson = new Gson();

	public Application() {
		log.info("Initialize Application");
		AppConfig appConfig = new AppConfig();
        Player player = new Player();
		LoopStore loopStore = new LoopStore(appConfig, new AudioSystemProvider());
		Recorder recorder = new Recorder(appConfig, player, loopStore);
		dspApplication = new ch.sulco.yal.dsp.Application(appConfig,
				new SocketCommandReceiver(appConfig), new OnboardProcessor(
						player, recorder, loopStore));
		
        Spark.staticFileLocation("/public");
        
        get("/play", (req, res) -> play());
        get("/loop", (req, res) -> loop());
        get("/samples", (req, res) -> getSamples());
        get("/channels", (req, res) -> getChannels());
        
        log.info("Application started");
        
	}
	
	private String getSamples(){
		return gson.toJson(dspApplication.getAudioProcessor().getSampleIds());
	}
	
	private String getChannels(){
		return gson.toJson(dspApplication.getAudioProcessor().getChannelIds());
	}
	
	private String play(){
		dspApplication.getAudioProcessor().play();
		return "Success";
	}
	
	private String loop(){
		dspApplication.getAudioProcessor().loop();
		return "Success";
	}
}