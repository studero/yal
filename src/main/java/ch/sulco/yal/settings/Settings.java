package ch.sulco.yal.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Settings {

	private final static Logger log = LoggerFactory.getLogger(Settings.class);

	private static final String AUDIO_SETTINGS_FILENAME = "audio.json";

	private AudioSettings audioSettings = new AudioSettings();

	public void load(String basePath) {
		try {
			this.audioSettings = new Gson().fromJson(Files.newBufferedReader(Paths.get(basePath, AUDIO_SETTINGS_FILENAME)),
					AudioSettings.class);
		} catch (Exception e) {
			log.error("Unable to load settings: " + e.getMessage());
		}
	}

	public void save(String basePath) {
		try {
			Files.write(Paths.get(basePath, AUDIO_SETTINGS_FILENAME), new Gson().toJson(audioSettings).getBytes(),
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			log.error("Unable to save settings", e);
		}
	}

	public AudioSettings getAudioSettings() {
		return audioSettings;
	}

}
