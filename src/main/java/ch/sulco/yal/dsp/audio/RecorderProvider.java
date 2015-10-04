package ch.sulco.yal.dsp.audio;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import ch.sulco.yal.dsp.audio.onboard.Recorder;

@Singleton
public class RecorderProvider {
	public List<Recorder> getRecorders() {
		return Arrays.asList(new Recorder(), new Recorder());
	}
}
