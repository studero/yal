package ch.sulco.yal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ch.sulco.yal.controller.MidiControl;
import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dsp.ClickTrackGenerator;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;
import ch.sulco.yal.web.Server;

@Singleton
public class Application {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

	@Inject
	private AppConfig appConfig;

	@Inject
	private Server server;

	@Inject
	private Processor audioProcessor;

	@Inject
	private MidiControl midiControl;

	@Inject
	private DataStore dataStore;

	@Inject
	private AudioSystemProvider audioSystemProvider;

	@Inject
	private ClickTrackGenerator clickTrackGenerator;

	public void start() {
		log.info("Start Application");
		this.dataStore.setup();
		for (Channel channel : this.audioSystemProvider.getChannels()) {
			this.dataStore.createChannel(channel);
		}
	}

	public Processor getAudioProcessor() {
		return this.audioProcessor;
	}

	public static Injector injector;

	static {
		boolean simulation = Boolean.getBoolean("simulation");
		if (simulation) {
			log.info("Running as simulation");
			injector = Guice.createInjector(new SimulationYalModule(), new PostConstructModule());
		} else {
			injector = Guice.createInjector(new YalModule(), new PostConstructModule());
		}
	}

	public static void main(String[] args) {
		Application application = injector.getInstance(Application.class);
		application.start();
	}
}
