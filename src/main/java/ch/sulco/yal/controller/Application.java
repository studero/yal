package ch.sulco.yal.controller;

import java.util.logging.Logger;

import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.audio.onboard.LoopStore;
import ch.sulco.yal.dsp.audio.onboard.OnboardProcessor;
import ch.sulco.yal.dsp.audio.onboard.Player;
import ch.sulco.yal.dsp.audio.onboard.Recorder;
import ch.sulco.yal.dsp.cmd.Command;
import ch.sulco.yal.dsp.cmd.Loop;
import ch.sulco.yal.dsp.cmd.Play;
import ch.sulco.yal.dsp.cmd.SocketCommandReceiver;
import spark.Spark;

public class Application {
	
	private final static Logger log = Logger.getLogger(Application.class.getName());
	
	private final ch.sulco.yal.dsp.Application dspApplication;

	public Application() {
		log.info("Initialize Application");
		AppConfig appConfig = new AppConfig();
        dspApplication = new ch.sulco.yal.dsp.Application(appConfig,
				new SocketCommandReceiver(appConfig), new OnboardProcessor(
						new Player(), new Recorder(appConfig), new LoopStore()));
		
        Spark.staticFileLocation("/public");
        
        Spark.get("/play", (req, res) -> runCommand(new Play()));
        Spark.get("/loop", (req, res) -> runCommand(new Loop()));
        
        log.info("Application started");
        
	}
	
	private String runCommand(Command command){
		dspApplication.onCommand(command);
		return "Success";
	}
}