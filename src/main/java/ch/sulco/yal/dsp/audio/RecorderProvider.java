package ch.sulco.yal.dsp.audio;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.sulco.yal.Application;
import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.audio.onboard.Recorder;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

@Singleton
public class RecorderProvider {

	@Inject
	private DataStore dataStore;

	public List<Recorder> getRecorders() {
		List<Recorder> recorders = new ArrayList<>();
		ImmutableList<InputChannel> inputChannels = FluentIterable.from(this.dataStore.getChannels()).filter(InputChannel.class).toList();
		for (InputChannel inputChannel : inputChannels) {
			Recorder recorder = Application.injector.getInstance(Recorder.class);
			recorder.setInputChannel(inputChannel);
			recorders.add(recorder);

		}
		return recorders;
	}
}
