package ch.sulco.yal.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dsp.audio.Processor;

@Singleton
public class MidiControl {

	private final static Logger log = LoggerFactory.getLogger(MidiControl.class);

	@Inject
	private Processor audioProcessor;

	@PostConstruct
	public void setup() {
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
							MidiControl.this.handleMidiMessage(message);
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
						MidiControl.this.handleMidiMessage(message);
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
				this.audioProcessor.play();
			if (m.getData1() == 42)
				this.audioProcessor.loop();
		}
	}
}
