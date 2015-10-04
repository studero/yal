package ch.sulco.yal;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.sulco.yal.controller.MidiControl;
import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.web.Server;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Singleton
public class Application {

	private final static Logger log = Logger.getLogger(Application.class.getName());

	@Inject
	private AppConfig appConfig;

	@Inject
	private Server server;

	@Inject
	private Processor audioProcessor;

	@Inject
	private MidiControl midiControl;

	public Application() {
		log.info("Initialize Application");

		log.info("AppConfig " + this.appConfig);

		log.info("Application started");

	}

	public Processor getAudioProcessor() {
		return this.audioProcessor;
	}

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new YalModule(), new PostConstructModule());
		Application application = injector.getInstance(Application.class);
	}
}
