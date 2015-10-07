package ch.sulco.yal.dsp.audio.onboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dm.OutputChannel;
import ch.sulco.yal.dsp.AppConfig;

@Singleton
public class AudioSystemProvider {

	private static final Logger log = Logger
			.getLogger(AudioSystemProvider.class.getName());

	@Inject
	private AppConfig appConfig;

	public Clip getClip(AudioFormat format, byte[] data, int offset,
			int bufferSize) throws LineUnavailableException {
		if (format == null) {
			format = this.appConfig.getAudioFormat();
		}
		Clip clip = AudioSystem.getClip();
		clip.open(format, data, offset, bufferSize);
		return clip;
	}

	public AudioInputStream getAudioInputStream(File file)
			throws UnsupportedAudioFileException, IOException {
		return AudioSystem.getAudioInputStream(file);
	}

	public List<Channel> getChannels() {
		long id = 0;
		List<Channel> channels = new ArrayList<>();
		for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
			for (Line.Info lineInfo : AudioSystem.getMixer(mixerInfo)
					.getTargetLineInfo(this.getTargetLineInfo())) {
				InputChannel channel = new InputChannel();
				channel.setId(id);
				channel.setMixerInfo(mixerInfo);
				channel.setLineInfo(lineInfo);
				channels.add(channel);
				log.info("New Input Channel [" + channel + "]");
				id++;
			}
			for (Line.Info lineInfo : AudioSystem.getMixer(mixerInfo)
					.getSourceLineInfo(this.getSourceLineInfo())) {
				OutputChannel channel = new OutputChannel();
				channel.setId(id);
				channel.setMixerInfo(mixerInfo);
				channel.setLineInfo(lineInfo);
				channels.add(channel);
				log.info("New Output Channel [" + channel + "]");
				id++;
			}
		}
		return channels;
	}

	public Line getLine(Mixer.Info mixerInfo, Line.Info info)
			throws LineUnavailableException {
		if (info == null) {
			info = this.getTargetLineInfo();
		}
		return AudioSystem.getMixer(mixerInfo).getLine(info);
	}

	private Info getTargetLineInfo() {
		return new DataLine.Info(TargetDataLine.class,
				this.appConfig.getAudioFormat());
	}

	private Info getSourceLineInfo() {
		return new DataLine.Info(SourceDataLine.class,
				this.appConfig.getAudioFormat());
	}
}