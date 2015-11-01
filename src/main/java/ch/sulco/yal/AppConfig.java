package ch.sulco.yal;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Singleton;
import javax.sound.sampled.AudioFormat;

import ch.sulco.yal.settings.Settings;

@Singleton
public class AppConfig {

	private Settings settings;

	public Settings getSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.load(getSettingsPath());
		}
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
		this.settings.save(getSettingsPath());
	}

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

	private Path getLocalRoot() {
		return Paths.get(System.getProperty("user.home"), ".yal");
	}
}
