package ch.sulco.yal;

import com.google.inject.AbstractModule;

import ch.sulco.yal.controller.MidiControl;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.audio.AudioSink;
import ch.sulco.yal.dsp.audio.AudioSource;
import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;
import ch.sulco.yal.dsp.audio.onboard.Synchronizer;
import ch.sulco.yal.simulator.SimulatedAudioSink;
import ch.sulco.yal.simulator.SimulatedAudioSource;
import ch.sulco.yal.simulator.SimulationAudioSystemProvider;
import ch.sulco.yal.simulator.SimulationDataStore;
import ch.sulco.yal.web.Server;

public class SimulationYalModule extends AbstractModule {

	@Override
	protected void configure() {
		this.bind(AppConfig.class);
		this.bind(Application.class);
		this.bind(AudioSystemProvider.class).to(SimulationAudioSystemProvider.class);
		this.bind(DataStore.class).to(SimulationDataStore.class);
		this.bind(MidiControl.class);
		this.bind(Processor.class);
		this.bind(AudioSink.class).to(SimulatedAudioSink.class);
		this.bind(AudioSource.class).to(SimulatedAudioSource.class);
		this.bind(Synchronizer.class);
		this.bind(Server.class);
	}
}
