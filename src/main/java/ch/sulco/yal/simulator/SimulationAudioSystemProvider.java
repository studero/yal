package ch.sulco.yal.simulator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;

@Singleton
public class SimulationAudioSystemProvider extends AudioSystemProvider {

	private final static Logger log = LoggerFactory.getLogger(SimulationAudioSystemProvider.class);

	@Override
	public List<Channel> getChannels() {
		List<Channel> channels = new ArrayList<>();
		channels.add(anInputChannel().id(0L).name("Drum Module").build());
		channels.add(anInputChannel().id(1L).name("Bass").build());
		channels.add(anInputChannel().id(2L).name("Guitar").build());
		channels.add(anInputChannel().id(3L).name("Piano").build());

		channels.add(anOutputChannel().id(4L).name("Main").build());
		channels.add(anOutputChannel().id(5L).name("Click").build());
		return channels;
	}

	private static InputChannelBuilder anInputChannel() {
		return new InputChannelBuilder();
	}

	private static OutputChannelBuilder anOutputChannel() {
		return new OutputChannelBuilder();
	}
}