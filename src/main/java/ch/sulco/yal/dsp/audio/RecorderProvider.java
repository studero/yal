package ch.sulco.yal.dsp.audio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.sulco.yal.Application;
import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;
import ch.sulco.yal.dsp.audio.onboard.Recorder;

@Singleton
public class RecorderProvider {

	@Inject
	private AudioSystemProvider audioSystemProvider;

	public List<Recorder> getRecorders() {
		List<Recorder> recorders = new ArrayList<>();
		Map<Integer, ChannelInfo> targetLines = this.audioSystemProvider.getTargetLines();
		for (int id : targetLines.keySet()) {
			Recorder recorder = Application.injector.getInstance(Recorder.class);
			recorder.setLineInfo(targetLines.get(id).getLineInfo());
			recorder.setId(id);
			recorders.add(recorder);

		}
		return recorders;
	}
}
