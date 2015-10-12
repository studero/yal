package ch.sulco.yal.dsp.audio.onboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

	public List<Channel> getChannels() {
		try {
			for (Mixer.Info thisMixerInfo : AudioSystem.getMixerInfo()) {
				System.out.println("Mixer: " + thisMixerInfo.getDescription() + " [" + thisMixerInfo.getName() + "]");
				Mixer thisMixer = AudioSystem.getMixer(thisMixerInfo);
				for (Line.Info thisLineInfo : thisMixer.getSourceLineInfo()) {
					if (thisLineInfo.getLineClass().getName().equals("javax.sound.sampled.Port")) {
						Line thisLine = thisMixer.getLine(thisLineInfo);
						thisLine.open();
						System.out.println("  Source Port: " + thisLineInfo.toString());
						for (Control thisControl : thisLine.getControls()) {
							System.out.println(this.AnalyzeControl(thisControl));
						}
						thisLine.close();
					}
				}
				for (Line.Info thisLineInfo : thisMixer.getTargetLineInfo()) {
					if (thisLineInfo.getLineClass().getName().equals("javax.sound.sampled.Port")) {
						Line thisLine = thisMixer.getLine(thisLineInfo);
						thisLine.open();
						System.out.println("  Target Port: " + thisLineInfo.toString());
						for (Control thisControl : thisLine.getControls()) {
							System.out.println(this.AnalyzeControl(thisControl));
						}
						thisLine.close();
					}
				}
			}
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		long id = 0;
		List<Channel> channels = new ArrayList<>();
		Mixer mixer = AudioSystem.getMixer(null);
		for (Line.Info lineInfo : mixer.getTargetLineInfo(this.getTargetLineInfo())) {
			InputChannel channel = new InputChannel(id);
			channel.setLineInfo(lineInfo);
			channels.add(channel);
			log.info("New Input Channel [" + id + "]");
			log.info(" LineInfo [" + lineInfo + "]");
			id++;
		}
		for (Line.Info lineInfo : mixer.getSourceLineInfo(this.getSourceLineInfo())) {
			OutputChannel channel = new OutputChannel(id);
			channel.setLineInfo(lineInfo);
			channels.add(channel);
			log.info("New Output Channel [" + id + "]");
			log.info(" LineInfo [" + lineInfo + "]");
			id++;
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