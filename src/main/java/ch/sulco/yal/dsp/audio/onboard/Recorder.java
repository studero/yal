package ch.sulco.yal.dsp.audio.onboard;

import java.io.IOException;

import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.AppConfig;
import ch.sulco.yal.dm.RecordingState;
import ch.sulco.yal.dsp.audio.AudioSource;

public class Recorder extends AudioSource {
	private final static Logger log = LoggerFactory.getLogger(Recorder.class);

	@Inject
	private AppConfig appConfig;

	@Inject
	private AudioSystemProvider audioSystemProvider;

	private TargetDataLine line;

	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	protected Thread getRecordThread() {
		try {
			this.line = (TargetDataLine) this.audioSystemProvider.getLine(this.getInputChannel().getLineInfo());
			this.line.open(this.appConfig.getAudioFormat());
			this.line.start();
			return new RecordThread(this.line);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to open line", e);
		}
	}

	private class RecordThread extends Thread {

		private final TargetDataLine line;

		RecordThread(TargetDataLine line) {
			this.line = line;
		}

		@Override
		public void run() {
			try {
				log.info("Start capturing...");
				AudioInputStream ais = new AudioInputStream(this.line);
				log.info("Start recording...");
				while (Recorder.this.getInputChannel().getRecordingState() == RecordingState.RECORDING) {
					byte[] buffer = new byte[4];
					ais.read(buffer);
					Recorder.this.newAudioData(buffer);
				}
				log.info("Stop recording...");

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
