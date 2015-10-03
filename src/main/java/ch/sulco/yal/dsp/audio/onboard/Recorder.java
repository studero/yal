package ch.sulco.yal.dsp.audio.onboard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.audio.RecordingState;

public class Recorder implements LoopListener {
	private final static Logger log = Logger.getLogger(Recorder.class.getName());

	private final AppConfig appConfig;
	private Player player;
	private LoopStore loopStore;
	private RecordingState recordingState = RecordingState.STOPPED;
	private byte[] recordedSample;
	private ByteArrayOutputStream recordingSample;
	
	private TargetDataLine line;

	public Recorder(AppConfig appConfig, Player player, LoopStore loopStore) {
		this.appConfig = appConfig;
		this.player = player;
		this.loopStore = loopStore;
		
		DataLine.Info info = new DataLine.Info(TargetDataLine.class,
				Recorder.this.appConfig.getAudioFormat());
		// checkState(AudioSystem.isLineSupported(info),
		// "Line not supported");
		TargetDataLine line;
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RecordingState getRecordingState() {
		return this.recordingState;
	}

	public void startRecord() {
		if (this.recordingState == RecordingState.STOPPED) {
			this.recordingState = RecordingState.WAITING;
			this.player.addLoopListerner(this);
		}
	}

	public void stopRecord() {
		this.recordingState = RecordingState.STOPPED;
		this.player.removeLoopListerner(this);
		if (this.recordingSample != null) {
			this.recordedSample = this.recordingSample.toByteArray();
			this.recordingSample = null;
		}
		if (this.recordedSample != null) {
			this.loopStore.addSample(this.recordedSample);
			this.recordedSample = null;
			this.recordingSample = null;
		}
	}

	@Override
	public void loopStarted() {
		if (this.recordingState == RecordingState.WAITING) {
			this.recordingState = RecordingState.RECORDING;
			this.recordedSample = null;
			this.recordingSample = new ByteArrayOutputStream();
			Thread recordThread = new Thread() {
				@Override
				public void run() {
					try {
						line.start();
						log.info("Start capturing...");
						AudioInputStream ais = new AudioInputStream(line);
						log.info("Start recording...");
						while (Recorder.this.recordingState == RecordingState.RECORDING) {
							byte[] buffer = new byte[2];
							int bytesRead = ais.read(buffer);
							Recorder.this.recordingSample.write(buffer, 0, bytesRead);
						}
						log.info("Stop recording...");

					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			};
			recordThread.start();
		} else if (this.recordingState == RecordingState.RECORDING) {
			this.recordedSample = this.recordingSample.toByteArray();
			this.recordingSample = new ByteArrayOutputStream();
		} else {
			this.player.removeLoopListerner(this);
		}
	}
}
