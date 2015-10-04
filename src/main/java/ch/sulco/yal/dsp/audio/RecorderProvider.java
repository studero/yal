package ch.sulco.yal.dsp.audio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.sulco.yal.PostConstructModule;
import ch.sulco.yal.YalModule;
import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;
import ch.sulco.yal.dsp.audio.onboard.Recorder;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Singleton
public class RecorderProvider {

	@Inject
	private AudioSystemProvider audioSystemProvider;

	public List<Recorder> getRecorders() {
		Injector injector = Guice.createInjector(new YalModule(), new PostConstructModule());
		List<Recorder> recorders = new ArrayList<>();
		Map<Integer, ChannelInfo> targetLines = this.audioSystemProvider.getTargetLines();
		for (int id : targetLines.keySet()) {
			Recorder recorder = injector.getInstance(Recorder.class);
			recorder.setLineInfo(targetLines.get(id).getLineInfo());
			recorder.setId(id);
			recorders.add(recorder);

		}
		return recorders;
	}
}
