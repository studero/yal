package ch.sulco.yal.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

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

import ch.sulco.yal.dm.Mapping;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.audio.Processor;

@Singleton
public class MidiControl {

	private final static Logger log = LoggerFactory.getLogger(MidiControl.class);

	@Inject
	private Processor audioProcessor;

	@Inject
	private DataStore dataStore;

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

		try {
			this.sendMidiMessage(new ShortMessage(176, 0, 64, 0));
			this.sendMidiMessage(new ShortMessage(176, 0, 64, 127));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleMidiMessage(MidiMessage message) {
		if (message instanceof ShortMessage) {
			ShortMessage m = (ShortMessage) message;
			log.info("new message channel=" + m.getChannel()
					+ ", status=" + m.getStatus()
					+ ", command=" + m.getCommand()
					+ ", data1=" + m.getData1()
					+ ", data2=" + m.getData2()
					+ ", length=" + m.getLength());
			for (Mapping mapping : this.dataStore.getMappings()) {
				if (Objects.equals(mapping.getTriggerValueMap().get("command"), m.getCommand())
						&& Objects.equals(mapping.getTriggerValueMap().get("channel"), m.getChannel())
						&& Objects.equals(mapping.getTriggerValueMap().get("data1"), m.getData1())
						&& Objects.equals(mapping.getTriggerValueMap().get("data2"), m.getData2())) {
					log.info("Trigger Mapping [" + mapping + "]");
					try {
						Processor.class.getMethod(mapping.getProcessorMethod()).invoke(this.audioProcessor);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
							| SecurityException e) {
						log.error("Unable to trigger processor method [" + mapping.getProcessorMethod() + "]", e);
					}
				}
			}
		}
	}

	private void sendMidiMessage(MidiMessage message) {
		ShortMessage m = (ShortMessage) message;
		log.info("send message channel=" + m.getChannel()
				+ ", status=" + m.getStatus()
				+ ", command=" + m.getCommand()
				+ ", data1=" + m.getData1()
				+ ", data2=" + m.getData2()
				+ ", length=" + m.getLength());
		MidiDevice device;
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			try {
				device = MidiSystem.getMidiDevice(infos[i]);
				log.info("Device: " + infos[i]);
				List<Receiver> receivers = device.getReceivers();
				for (int j = 0; j < receivers.size(); j++) {
					receivers.get(j).send(message, System.currentTimeMillis());
				}

				device.open();
				Receiver receiver = device.getReceiver();
				receiver.send(message, System.currentTimeMillis());

			} catch (MidiUnavailableException e) {
			}
		}
	}
}
