package ch.sulco.yal.dsp.audio.onboard;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import ch.sulco.yal.dsp.AppConfig;

public class AudioSystemProvider {
	private final AppConfig appConfig;
	
	public AudioSystemProvider(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	public Clip getClip(AudioFormat format, byte[] data, int offset, int bufferSize) throws LineUnavailableException {
		if(format == null){
			format = appConfig.getAudioFormat();
		}
		Clip clip = AudioSystem.getClip();
		clip.open(format, data, offset, bufferSize);
		return clip;
	}

	public AudioInputStream getAudioInputStream(File file)
			throws UnsupportedAudioFileException, IOException {
		return AudioSystem.getAudioInputStream(file);
	}

	public Line getLine(Info info) throws LineUnavailableException {
		if(info == null){
			info = getTargetLineInfo();
		}
		return AudioSystem.getLine(info);
	}
	
	private Info getTargetLineInfo(){
		return new DataLine.Info(TargetDataLine.class, appConfig.getAudioFormat());
	}
}