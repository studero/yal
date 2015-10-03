package ch.sulco.yal.dsp;

import javax.sound.sampled.AudioFormat;

public class AppConfig {

	public int getCommandSocketPort() {
		return 12000;
	}

	public float getSampleRate() {
		return 128000;
	}

	public int getSampleSize() {
		return 16;
	}

	public AudioFormat getAudioFormat() {
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(this.getSampleRate(), this.getSampleSize(), channels, signed, bigEndian);
	}
}
