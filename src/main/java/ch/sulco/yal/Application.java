package ch.sulco.yal;

import java.util.logging.Logger;

import ch.sulco.yal.controller.MidiControl;
import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;
import ch.sulco.yal.dsp.audio.onboard.LoopStore;
import ch.sulco.yal.dsp.audio.onboard.OnboardProcessor;
import ch.sulco.yal.dsp.audio.onboard.Player;
import ch.sulco.yal.dsp.audio.onboard.Recorder;
import ch.sulco.yal.web.Server;

public class Application {

	private final static Logger log = Logger.getLogger(Application.class
			.getName());

	private final AppConfig appConfig;
	private final Server server;
	private final MidiControl midiControl;
	private final Processor audioProcessor;

	public Application() {
		log.info("Initialize Application");
		
		this.appConfig = new AppConfig();
		Player player = new Player();
		LoopStore loopStore = new LoopStore(appConfig,
				new AudioSystemProvider());
		Recorder recorder = new Recorder(appConfig, player, loopStore);
		Recorder recorder2 = new Recorder(appConfig, player, loopStore);
		
		this.audioProcessor = new OnboardProcessor(player, loopStore, recorder, recorder2);

		server = new Server(this.getAudioProcessor());

		midiControl = new MidiControl(this.getAudioProcessor());
		
		log.info("Application started");

	}

	public Processor getAudioProcessor() {
		return audioProcessor;
	}
}