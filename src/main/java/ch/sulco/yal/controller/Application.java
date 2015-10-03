package ch.sulco.yal.controller;

import java.util.List;
import java.util.logging.Logger;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

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

		log.info("Application started");

		this.setupMidi();

	}

	private void setupMidi() {
		MidiDevice device;
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			try {
				device = MidiSystem.getMidiDevice(infos[i]);
				log.info("Device: " + infos[i]);
				List<Transmitter> transmitters = device.getTransmitters();
				for (int j = 0; j < transmitters.size(); j++) {
					transmitters.get(j).setReceiver(new Receiver() {
						@Override
						public void send(MidiMessage message, long timeStamp) {
							Application.this.handleMidiMessage(message);
						}

						@Override
						public void close() {
						}
					});
				}

				Transmitter trans = device.getTransmitter();
				trans.setReceiver(new Receiver() {
					@Override
					public void send(MidiMessage message, long timeStamp) {
						Application.this.handleMidiMessage(message);
					}

					@Override
					public void close() {
					}
				});

				device.open();
				log.info(device.getDeviceInfo() + " Was Opened");

			} catch (MidiUnavailableException e) {
			}
		}
	}

	private void handleMidiMessage(MidiMessage message) {
		if (message instanceof ShortMessage) {
			ShortMessage m = (ShortMessage) message;
			if (m.getData1() == 41)
				this.dspApplication.getAudioProcessor().play();
			if (m.getData1() == 42)
				this.dspApplication.getAudioProcessor().loop();
		}
	}
}