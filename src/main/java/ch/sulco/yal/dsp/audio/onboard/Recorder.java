package ch.sulco.yal.dsp.audio.onboard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dm.RecordingState;
import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.event.ChannelUpdated;
import ch.sulco.yal.event.EventManager;

public class Recorder implements LoopListener {
	private final static Logger log = Logger
			.getLogger(Recorder.class.getName());

	@Inject
	private AppConfig appConfig;

	@Inject
	private AudioSystemProvider audioSystemProvider;

	@Inject
	private Synchronizer synchronizer;

	@Inject
	private LoopStore loopStore;

	@Inject
	private EventManager eventManager;

	private InputChannel inputChannel;

	private byte[] recordedSample;
	private ByteArrayOutputStream recordingSample;
	private ByteArrayOutputStream monitoringSample;

	private TargetDataLine line;

	public void setInputChannel(InputChannel inputChannel) {
		this.inputChannel = inputChannel;
	}

	public void initialize() {
		this.setRecordingState(RecordingState.STOPPED);
		// checkState(AudioSystem.isLineSupported(info),
		// "Line not supported");
		try {
			this.line = (TargetDataLine) this.audioSystemProvider.getLine(
					this.inputChannel.getMixerInfo(),
					this.inputChannel.getLineInfo());
			this.line.open(appConfig.getAudioFormat());
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private TargetDataLine getLine() {
		return this.line;
	}

	public RecordingState getRecordingState() {
		return this.inputChannel.getRecordingState();
	}

	public void startRecord() {
		if (this.inputChannel.getRecordingState() == RecordingState.STOPPED) {
			this.setRecordingState(RecordingState.WAITING);
			this.synchronizer.addLoopListerner(this);
		}
	}

	public void stopRecord() {
		this.setRecordingState(RecordingState.STOPPED);
		this.synchronizer.removeLoopListerner(this);
		if (!this.inputChannel.isOverdubbing()) {
			this.recordedSample = this.recordingSample.toByteArray();
		}
		if (this.recordedSample != null) {
			this.loopStore.addSample(this.recordedSample);
			this.recordedSample = null;
			this.recordingSample = null;
		}
	}

	public boolean isMonitoring() {
		return this.inputChannel.isMonitoring();
	}

	public void setMonitoring(boolean monitoring) {
		if (monitoring)
			Recorder.this.getLine().start();
		else
			Recorder.this.getLine().stop();
		this.inputChannel.setMonitoring(monitoring);
		this.eventManager.addEvent(new ChannelUpdated(this.inputChannel));
	}

	@Override
	public void loopStarted(boolean firstLoop) {
		log.info("Loop Started [firstLoop=" + firstLoop + "]");
		if (this.inputChannel.getRecordingState() == RecordingState.WAITING) {
			this.inputChannel.setOverdubbing(!firstLoop);
			this.setRecordingState(RecordingState.RECORDING);
			this.recordedSample = null;
			this.recordingSample = new ByteArrayOutputStream();
			Thread recordThread = new Thread() {
				@Override
				public void run() {
					try {
						Recorder.this.getLine().start();
						log.info("Start capturing...");
						AudioInputStream ais = new AudioInputStream(
								Recorder.this.getLine());
						log.info("Start recording...");
						int monitoringCount = 0;
						while (Recorder.this.inputChannel.getRecordingState() == RecordingState.RECORDING) {
							byte[] buffer = new byte[4];
							int bytesRead = ais.read(buffer);
							if (Recorder.this.inputChannel.getRecordingState() == RecordingState.RECORDING) {
								if (Recorder.this.recordingSample == null) {
									Recorder.this.recordingSample = new ByteArrayOutputStream();
								}
								Recorder.this.recordingSample.write(buffer, 0,
										bytesRead);
							}
							// Recorder.this.updateMonitoring(monitoringCount,
							// buffer, bytesRead);
						}
						log.info("Stop recording...");

					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			};
			recordThread.start();
		} else if (this.inputChannel.getRecordingState() == RecordingState.RECORDING) {
			this.recordedSample = this.recordingSample.toByteArray();
			this.recordingSample = new ByteArrayOutputStream();
		} else {
			this.synchronizer.removeLoopListerner(this);
		}
	}

	private void updateMonitoring(int monitoringCount, byte[] buffer,
			int bytesRead) {
		if (Recorder.this.inputChannel.isMonitoring()) {
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
				Recorder.this.inputChannel.setLevel(rms);
				Recorder.this.eventManager.addEvent(new ChannelUpdated(
						Recorder.this.inputChannel));
				monitoringCount = 0;
				Recorder.this.monitoringSample = null;
			}
		} else {
			Recorder.this.monitoringSample = null;
		}
	}

	private void setRecordingState(RecordingState recordingState) {
		log.info("Change RecordingState [" + this.inputChannel.getId() + "]["
				+ recordingState + "]");
		this.inputChannel.setRecordingState(recordingState);
		this.eventManager.addEvent(new ChannelUpdated(this.inputChannel));
	}
}
