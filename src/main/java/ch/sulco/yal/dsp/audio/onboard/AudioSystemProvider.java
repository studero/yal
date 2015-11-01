package ch.sulco.yal.dsp.audio.onboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.AppConfig;
import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dm.OutputChannel;
import ch.sulco.yal.settings.AudioSettings;

@Singleton
public class AudioSystemProvider {

	private final static Logger log = LoggerFactory.getLogger(AudioSystemProvider.class);

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

	public List<AudioSettings> getAvailableAudioSettings() {
		List<AudioSettings> audioSettings = new ArrayList<>();
		for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
			Mixer mixer = AudioSystem.getMixer(mixerInfo);
			AudioSettings audioSetting = new AudioSettings();
			audioSetting.setSoundCardId(mixerInfo.getName());
			audioSetting.setInputChannels(new HashMap<>());
			audioSetting.setOutputChannels(new HashMap<>());
			if (mixer.getClass().getName().equals("com.sun.media.sound.DirectAudioDevice")) {
				for (Line.Info lineInfo : mixer.getTargetLineInfo(this.getTargetLineInfo())) {
					try {
						audioSetting.getInputChannels().put(mixer.getLine(lineInfo).hashCode(), lineInfo.toString());
					} catch (LineUnavailableException e) {
						log.error("Unable to load channel [" + lineInfo.toString() + "]", e);
					}
				}
				for (Line.Info lineInfo : mixer.getSourceLineInfo(this.getSourceLineInfo())) {
					try {
						audioSetting.getOutputChannels().put(mixer.getLine(lineInfo).hashCode(), lineInfo.toString());
					} catch (LineUnavailableException e) {
						log.error("Unable to load channel [" + lineInfo.toString() + "]", e);
					}
				}
			}
			audioSettings.add(audioSetting);
		}
		return audioSettings;
	}

	public List<Channel> getChannels() {
		for (Mixer.Info thisMixerInfo : AudioSystem.getMixerInfo()) {
			System.out.println("Mixer: " + thisMixerInfo.getDescription() + " [" + thisMixerInfo.getName() + "]");
			Mixer thisMixer = AudioSystem.getMixer(thisMixerInfo);
			for (Line.Info thisLineInfo : thisMixer.getSourceLineInfo()) {
				try {
					Line thisLine = thisMixer.getLine(thisLineInfo);
					thisLine.open();
					System.out.println("  Source Port: " + thisLineInfo.toString());
					for (Control thisControl : thisLine.getControls()) {
						System.out.println(this.AnalyzeControl(thisControl));
					}
					thisLine.close();
				} catch (Exception e) {
				}
			}
			for (Line.Info thisLineInfo : thisMixer.getTargetLineInfo()) {
				try {
					Line thisLine = thisMixer.getLine(thisLineInfo);
					thisLine.open();
					System.out.println("  Target Port: " + thisLineInfo.toString());
					for (Control thisControl : thisLine.getControls()) {
						System.out.println(this.AnalyzeControl(thisControl));
					}
					thisLine.close();
				} catch (Exception e) {
				}
			}
		}
		long id = 0;
		List<Channel> channels = new ArrayList<>();
		for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
			Mixer mixer = AudioSystem.getMixer(mixerInfo);
			if (mixer.getClass().getName().equals("com.sun.media.sound.DirectAudioDevice")) {
				for (Line.Info lineInfo : mixer.getTargetLineInfo(this.getTargetLineInfo())) {
					InputChannel channel = new InputChannel(id);
					channel.setLineInfo(lineInfo);
					channels.add(channel);
					log.info("New Input Channel [" + id + "]");
					log.info(" MixerInfo [" + mixerInfo.getDescription() + "][" + mixerInfo.getName() + "]");
					log.info(" LineInfo [" + lineInfo + "]");
					log.info(" LineInfo LineClass [" + lineInfo.getLineClass().getName() + "]");
					try {
						log.info(" Line HashCode [" + mixer.getLine(lineInfo).hashCode() + "]");
					} catch (LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					id++;
				}
				for (Line.Info lineInfo : mixer.getSourceLineInfo(this.getSourceLineInfo())) {
					OutputChannel channel = new OutputChannel(id);
					channel.setLineInfo(lineInfo);
					channels.add(channel);
					log.info("New Output Channel [" + id + "]");
					log.info(" MixerInfo [" + mixerInfo.getDescription() + "][" + mixerInfo.getName() + "]");
					log.info(" LineInfo [" + lineInfo + "]");
					log.info(" LineInfo LineClass [" + lineInfo.getLineClass().getName() + "]");
					try {
						log.info(" Line HashCode [" + mixer.getLine(lineInfo).hashCode() + "]");
					} catch (LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					id++;
				}
			}
		}
		return channels;
	}

	public String AnalyzeControl(Control thisControl) {
		String type = thisControl.getType().toString();
		if (thisControl instanceof BooleanControl) {
			return "    Control: " + type + " (boolean)";
		}
		if (thisControl instanceof CompoundControl) {
			System.out.println("    Control: " + type + " (compound - values below)");
			String toReturn = "";
			for (Control children : ((CompoundControl) thisControl).getMemberControls()) {
				toReturn += "  " + this.AnalyzeControl(children) + "\n";
			}
			return toReturn.substring(0, toReturn.length() - 1);
		}
		if (thisControl instanceof EnumControl) {
			return "    Control:" + type + " (enum: " + thisControl.toString() + ")";
		}
		if (thisControl instanceof FloatControl) {
			return "    Control: " + type + " (float: from " + ((FloatControl) thisControl).getMinimum() + " to "
					+ ((FloatControl) thisControl).getMaximum() + ")";
		}
		return "    Control: unknown type";
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

	private Info getSourceLineInfo() {
		return new DataLine.Info(SourceDataLine.class, this.appConfig.getAudioFormat());
	}
}