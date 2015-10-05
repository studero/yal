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

import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.audio.RecordingState;
import ch.sulco.yal.event.ChannelCreated;
import ch.sulco.yal.event.ChannelMonitorValueChanged;
import ch.sulco.yal.event.ChannelStateChanged;
import ch.sulco.yal.event.EventManager;

public class Recorder implements LoopListener {
	private final static Logger log = Logger.getLogger(Recorder.class.getName());

	@Inject
	private AppConfig appConfig;

	@Inject
	private AudioSystemProvider audioSystemProvider;

	@Inject
	private Player player;

	@Inject
	private LoopStore loopStore;

	@Inject
	private EventManager eventManager;

	private boolean overdubbing = false;

	private boolean monitoring = false;

	private int id;

	private RecordingState recordingState;
	private byte[] recordedSample;
	private ByteArrayOutputStream recordingSample;
	private ByteArrayOutputStream monitoringSample;

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

	public boolean isMonitoring() {
		return this.monitoring;
	}

	public void setMonitoring(boolean monitoring) {
		if (monitoring)
			Recorder.this.getLine().start();
		else
			Recorder.this.getLine().stop();
		this.monitoring = monitoring;
		this.eventManager.addEvent(new ChannelStateChanged(this.id, this.recordingState, this.monitoring));
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
						int monitoringCount = 0;
						while (Recorder.this.recordingState == RecordingState.RECORDING) {
							byte[] buffer = new byte[2];
							int bytesRead = ais.read(buffer);
							if (Recorder.this.recordingState == RecordingState.RECORDING) {
								if (Recorder.this.recordingSample == null)
									Recorder.this.recordingSample = new ByteArrayOutputStream();
								Recorder.this.recordingSample.write(buffer, 0, bytesRead);
							}
							if (Recorder.this.monitoring) {
								if (Recorder.this.monitoringSample == null)
									Recorder.this.monitoringSample = new ByteArrayOutputStream();
								Recorder.this.monitoringSample.write(buffer, 0, bytesRead);
								monitoringCount++;
								int aggregation = 60000; // 2000;
								if (monitoringCount % aggregation == 0) {
									byte[] byteArray = Recorder.this.monitoringSample.toByteArray();
									float[] samples = new float[byteArray.length / 2];
									for (int i = 0; i < byteArray.length; i += 2) {
										byte b1 = byteArray[i];
										byte b2 = byteArray[i + 1];
										if (Recorder.this.appConfig.getAudioFormat().isBigEndian()) {
											samples[i / 2] = (b1 << 8 | b2 & 0xFF) / 32768f;
										} else {
											samples[i / 2] = (b2 << 8 | b1 & 0xFF) / 32768f;
										}
									}
									double value = 0;
									for (float sample : samples) {
										value += sample * sample;
									}
									float rms = (float) Math.sqrt(value / (samples.length));
									Recorder.this.eventManager.addEvent(new ChannelMonitorValueChanged(Recorder.this.id, rms));
									monitoringCount = 0;
									Recorder.this.monitoringSample = null;
								}
							} else {
								Recorder.this.monitoringSample = null;
							}
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
		log.info("Change RecordingState [" + this.id + "][" + recordingState + "]");
		this.recordingState = recordingState;
		this.eventManager.addEvent(new ChannelStateChanged(this.id, recordingState, this.monitoring));
	}
}
