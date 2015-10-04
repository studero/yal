package ch.sulco.yal.dsp.audio.onboard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import ch.sulco.yal.dsp.audio.RecordingState;

public class Recorder implements LoopListener {
	private final static Logger log = Logger.getLogger(Recorder.class.getName());


	@Inject
	private AudioSystemProvider audioSystemProvider;
	@Inject
	private Player player;

	@Inject
	private LoopStore loopStore;

	private RecordingState recordingState = RecordingState.STOPPED;
	private boolean overdubbing = false;
	private byte[] recordedSample;
	private ByteArrayOutputStream recordingSample;

	private TargetDataLine line;

	public Recorder() {

	}

	@PostConstruct
	public void setup() {
		// checkState(AudioSystem.isLineSupported(info),
		// "Line not supported");
		try {
			this.line = (TargetDataLine) audioSystemProvider.getLine(null);
			this.line.open();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private TargetDataLine getLine(){
		return line;
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
		if(!overdubbing){
			this.recordedSample = this.recordingSample.toByteArray();
		}
		if (this.recordedSample != null) {
			this.loopStore.addSample(this.recordedSample);
			this.recordedSample = null;
			this.recordingSample = null;
		}
	}

	@Override
	public void loopStarted(boolean firstLoop) {
		if (this.recordingState == RecordingState.WAITING) {
			this.recordingState = RecordingState.RECORDING;
			this.overdubbing = !firstLoop;
			this.recordedSample = null;
			this.recordingSample = new ByteArrayOutputStream();
			Thread recordThread = new Thread() {
				@Override
				public void run() {
					try {
						getLine().start();
						log.info("Start capturing...");
						AudioInputStream ais = new AudioInputStream(getLine());
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
