package ch.sulco.yal;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Singleton;
import javax.sound.sampled.AudioFormat;

@Singleton
public class AppConfig {

	public float getSampleRate() {
		return 44100;
	}

	public int getSampleSize() {
		return 16;
	}

	public AudioFormat getAudioFormat() {
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(this.getSampleRate(), this.getSampleSize(), channels, signed, bigEndian);
	}

	public String getSettingsPath() {
		return Paths.get(getLocalRoot().toString(), "config").toString();
	}

	public String getDataPath() {
		return Paths.get(getLocalRoot().toString(), "data").toString();
	}

	private Path getLocalRoot() {
		return Paths.get(System.getProperty("user.home"), ".yal");
	}
}
