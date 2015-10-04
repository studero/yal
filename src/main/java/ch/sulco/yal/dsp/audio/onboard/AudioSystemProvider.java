package ch.sulco.yal.dsp.audio.onboard;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.audio.ChannelInfo;

@Singleton
public class AudioSystemProvider {

	@Inject
	private AppConfig appConfig;

	public Clip getClip(AudioFormat format, byte[] data, int offset, int bufferSize) throws LineUnavailableException {
		if (format == null) {
			format = this.appConfig.getAudioFormat();
		}
		Clip clip = AudioSystem.getClip();
		clip.open(format, data, offset, bufferSize);
		return clip;
	}

	public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {
		return AudioSystem.getAudioInputStream(file);
	}

	public Map<Integer, ChannelInfo> getTargetLines() {
		HashMap<Integer, ChannelInfo> channels = new HashMap<Integer, ChannelInfo>();
		for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
			for (Line.Info lineInfo : AudioSystem.getMixer(mixerInfo).getTargetLineInfo(this.getTargetLineInfo())) {
				ChannelInfo channel = new ChannelInfo(channels.size(), mixerInfo, lineInfo);
				channels.put(channel.getId(), channel);
			}
		}
		return channels;
	}

	public Line getLine(Line.Info info) throws LineUnavailableException {
		if (info == null) {
			info = this.getTargetLineInfo();
		}
		return AudioSystem.getLine(info);
	}

	private Info getTargetLineInfo() {
		return new DataLine.Info(TargetDataLine.class, this.appConfig.getAudioFormat());
	}
}