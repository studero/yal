package ch.sulco.yal.dsp.audio;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.RecordingState;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.audio.onboard.LoopListener;
import ch.sulco.yal.dsp.audio.onboard.Synchronizer;

public abstract class AudioSource implements LoopListener, AudioDataListener {

	private final static Logger log = LoggerFactory.getLogger(AudioSource.class);

	@Inject
	private Synchronizer synchronizer;

	@Inject
	private DataStore dataStore;

	private InputChannel inputChannel;

	private byte[] recordedSample;
	private ByteArrayOutputStream recordingSample;
	// private ByteArrayOutputStream monitoringSample;

	private List<AudioDataListener> audioDataListeners = new ArrayList<>();

	public void addAudioDataListener(AudioDataListener audioDataListener) {
		this.audioDataListeners.add(audioDataListener);
	}

	protected void triggerNewAudioData(byte[] data) {
		for (AudioDataListener audioDataListener : this.audioDataListeners) {
			audioDataListener.newAudioData(data);
		}
	}

	protected InputChannel getInputChannel() {
		return this.inputChannel;
	}

	protected byte[] getRecordedSample() {
		return this.recordedSample;
	}

	public void initialize() {
		this.addAudioDataListener(this);
		this.setRecordingState(RecordingState.STOPPED);

	}

	public void setMonitoring(boolean monitoring) {
		this.inputChannel.setMonitoring(monitoring);
		this.dataStore.updateChannel(this.inputChannel);
	}

	public void setInputChannel(InputChannel inputChannel) {
		this.inputChannel = inputChannel;
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
			Loop currentLoop = this.dataStore.getCurrentLoop();
			Sample sample = new Sample();
			Long nextSampleId = dataStore.getNextSampleId();
			log.info("new sample id " + nextSampleId);
			sample.setId(nextSampleId);
			sample.setChannelId(this.inputChannel.getId());
			if (currentLoop.getSamples().isEmpty()) {
				long sampleLength = this.getSampleLength();
				this.synchronizer.setLength(sampleLength);
				currentLoop.setTimeLength(sampleLength);
				currentLoop.setDataLength(this.recordedSample.length);
				sample.setData(this.recordedSample);
			} else {
				byte[] adaptedData = Arrays.copyOf(this.recordedSample, currentLoop.getDataLength());
				sample.setData(adaptedData);
			}
			dataStore.updateLoop(currentLoop);
			dataStore.createSample(currentLoop.getId(), sample);
			this.recordedSample = null;
			this.recordingSample = null;
		}
	}

	protected abstract long getSampleLength();

	public boolean isMonitoring() {
		return this.inputChannel.isMonitoring();
	}

	protected abstract Thread getRecordThread();

	@Override
	public void loopStarted(boolean firstLoop) {
		log.info("Loop Started [firstLoop=" + firstLoop + "]");
		if (this.inputChannel.getRecordingState() == RecordingState.WAITING) {
			this.inputChannel.setOverdubbing(!firstLoop);
			this.setRecordingState(RecordingState.RECORDING);
			this.recordedSample = null;
			this.recordingSample = new ByteArrayOutputStream();
			this.getRecordThread().start();
		} else if (this.inputChannel.getRecordingState() == RecordingState.RECORDING) {
			this.recordedSample = this.recordingSample.toByteArray();
			this.recordingSample = new ByteArrayOutputStream();
			log.info("recorded sample " + recordedSample.length);
			stopRecord();
		} else {
			this.synchronizer.removeLoopListerner(this);
		}
	}

	@Override
	public void loopStopped() {
		log.info("Loop Stopped");
		stopRecord();
	}

	@Override
	public void newAudioData(byte[] data) {
		if (this.getInputChannel().getRecordingState() == RecordingState.RECORDING) {
			if (this.recordingSample == null) {
				this.recordingSample = new ByteArrayOutputStream();
			}
			this.recordingSample.write(data, 0, data.length);
		}
	}

	private void setRecordingState(RecordingState recordingState) {
		log.info("Change RecordingState [" + this.inputChannel.getId() + "][" + recordingState + "]");
		this.inputChannel.setRecordingState(recordingState);
		this.dataStore.updateChannel(this.inputChannel);
	}

	// private void updateMonitoring(int monitoringCount, byte[] buffer, int
	// bytesRead) {
	// if (Recorder.this.inputChannel.isMonitoring()) {
	// if (Recorder.this.monitoringSample == null)
	// Recorder.this.monitoringSample = new ByteArrayOutputStream();
	// Recorder.this.monitoringSample.write(buffer, 0, bytesRead);
	// monitoringCount++;
	// int aggregation = 60000; // 2000;
	// if (monitoringCount % aggregation == 0) {
	// byte[] byteArray = Recorder.this.monitoringSample.toByteArray();
	// float[] samples = new float[byteArray.length / 2];
	// for (int i = 0; i < byteArray.length; i += 2) {
	// byte b1 = byteArray[i];
	// byte b2 = byteArray[i + 1];
	// if (Recorder.this.appConfig.getAudioFormat().isBigEndian()) {
	// samples[i / 2] = (b1 << 8 | b2 & 0xFF) / 32768f;
	// } else {
	// samples[i / 2] = (b2 << 8 | b1 & 0xFF) / 32768f;
	// }
	// }
	// double value = 0;
	// for (float sample : samples) {
	// value += sample * sample;
	// }
	// float rms = (float) Math.sqrt(value / (samples.length));
	// Recorder.this.inputChannel.setLevel(rms);
	// Recorder.this.eventManager.addEvent(new
	// ChannelUpdated(Recorder.this.inputChannel));
	// monitoringCount = 0;
	// Recorder.this.monitoringSample = null;
	// }
	// } else {
	// Recorder.this.monitoringSample = null;
	// }
	// }
}
