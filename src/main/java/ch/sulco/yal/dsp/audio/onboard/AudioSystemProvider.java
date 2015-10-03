package ch.sulco.yal.dsp.audio.onboard;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioSystemProvider {
	public Clip getClip(AudioFormat format, byte[] data, int offset,
			int bufferSize) throws LineUnavailableException {
		Clip clip = AudioSystem.getClip();
		clip.open(format, data, offset, bufferSize);
		return clip;
	}

	public AudioInputStream getAudioInputStream(File file)
			throws UnsupportedAudioFileException, IOException {
		return AudioSystem.getAudioInputStream(file);
	}
}