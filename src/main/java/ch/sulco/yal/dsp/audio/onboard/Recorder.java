package ch.sulco.yal.dsp.audio.onboard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import ch.sulco.yal.dsp.audio.RecordingState;
import ch.sulco.yal.event.ChannelCreated;
import ch.sulco.yal.event.ChannelStateChanged;
import ch.sulco.yal.event.EventManager;

public class Recorder implements LoopListener {
	private final static Logger log = Logger.getLogger(Recorder.class.getName());

	@Inject
	private AudioSystemProvider audioSystemProvider;

	@Inject
	private Player player;

	@Inject
	private LoopStore loopStore;

	@Inject
	private EventManager eventManager;

	private boolean overdubbing = false;

	private int id;

	private RecordingState recordingState;
	private byte[] recordedSample;
	private ByteArrayOutputStream recordingSample;

	private Info lineInfo = null;
	private TargetDataLine line;

	public void setLineInfo(Info lineInfo) {
		this.lineInfo = lineInfo;
	}

	public void setId(int id) {
		this.id = id;
	}

	@PostConstruct
	public void setup() {
		this.setRecordingState(RecordingState.STOPPED);
		// checkState(AudioSystem.isLineSupported(info),
		// "Line not supported");
		try {
			this.line = (TargetDataLine) this.audioSystemProvider.getLine(this.lineInfo);
			this.line.open();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.eventManager.addEvent(new ChannelCreated(this.id));
	}

	private TargetDataLine getLine() {
		return this.line;
	}

	public RecordingState getRecordingState() {
		return this.recordingState;
	}

	public void startRecord() {
		if (this.recordingState == RecordingState.STOPPED) {
			this.setRecordingState(RecordingState.WAITING);
			this.player.addLoopListerner(this);
		}
	}

	public void stopRecord() {
		this.setRecordingState(RecordingState.STOPPED);
		this.player.removeLoopListerner(this);
		if (!this.overdubbing) {
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
			this.overdubbing = !firstLoop;
			this.setRecordingState(RecordingState.RECORDING);
			this.recordedSample = null;
			this.recordingSample = new ByteArrayOutputStream();
			Thread recordThread = new Thread() {
				@Override
				public void run() {
					try {
						Recorder.this.getLine().start();
						log.info("Start capturing...");
						AudioInputStream ais = new AudioInputStream(Recorder.this.getLine());
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

	private void setRecordingState(RecordingState recordingState) {
		this.recordingState = recordingState;
		this.eventManager.addEvent(new ChannelStateChanged(this.id, recordingState));
	}
}
