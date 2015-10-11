package ch.sulco.yal;

import ch.sulco.yal.controller.MidiControl;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.TestGui;
import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;
import ch.sulco.yal.dsp.audio.onboard.LoopStore;
import ch.sulco.yal.dsp.audio.onboard.OnboardProcessor;
import ch.sulco.yal.dsp.audio.onboard.Player;
import ch.sulco.yal.dsp.audio.onboard.Recorder;
import ch.sulco.yal.dsp.audio.onboard.Synchronizer;
import ch.sulco.yal.event.EventManager;
import ch.sulco.yal.web.Server;

import com.google.inject.AbstractModule;

public class YalModule extends AbstractModule {

	@Override
	protected void configure() {
		this.bind(AppConfig.class);
		this.bind(Application.class);
		this.bind(AudioSystemProvider.class);
		this.bind(DataStore.class);
		this.bind(EventManager.class);
		this.bind(LoopStore.class);
		this.bind(MidiControl.class);
		this.bind(Processor.class).to(OnboardProcessor.class);
		this.bind(Player.class);
		this.bind(Recorder.class);
		this.bind(Synchronizer.class);
		this.bind(Server.class);
		this.bind(TestGui.class);
	}
}
