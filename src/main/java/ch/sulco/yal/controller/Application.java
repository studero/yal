package ch.sulco.yal.controller;

import java.util.logging.Logger;

import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;
import ch.sulco.yal.dsp.audio.onboard.LoopStore;
import ch.sulco.yal.dsp.audio.onboard.OnboardProcessor;
import ch.sulco.yal.dsp.audio.onboard.Player;
import ch.sulco.yal.dsp.audio.onboard.Recorder;
import ch.sulco.yal.dsp.cmd.SocketCommandReceiver;
import ch.sulco.yal.web.Server;

public class Application {

	private final static Logger log = Logger.getLogger(Application.class
			.getName());

	private final ch.sulco.yal.dsp.Application dspApplication;

	private final Server server;
	private final MidiControl midiControl;

	public Application() {
		log.info("Initialize Application");
		AppConfig appConfig = new AppConfig();
		Player player = new Player();
		LoopStore loopStore = new LoopStore(appConfig,
				new AudioSystemProvider());
		Recorder recorder = new Recorder(appConfig, player, loopStore);
		Recorder recorder2 = new Recorder(appConfig, player, loopStore);
		this.dspApplication = new ch.sulco.yal.dsp.Application(appConfig,
				new SocketCommandReceiver(appConfig), new OnboardProcessor(
						player, loopStore, recorder, recorder2));

		server = new Server(this.dspApplication);

		midiControl = new MidiControl(this.dspApplication);
		
		log.info("Application started");

	}
}