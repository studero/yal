package ch.sulco.yal.web.dm;

import ch.sulco.yal.dsp.audio.RecordingState;

public class Channel {
	private int id;
	private String name;
	private RecordingState recordingState;
	private boolean monitoring;
	private double meterLevel;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RecordingState getRecordingState() {
		return this.recordingState;
	}

	public void setRecordingState(RecordingState recordingState) {
		this.recordingState = recordingState;
	}

	public boolean isMonitoring() {
		return this.monitoring;
	}

	public void setMonitoring(boolean monitoring) {
		this.monitoring = monitoring;
	}

	public double getMeterLevel() {
		return this.meterLevel;
	}

	public void setMeterLevel(double meterLevel) {
		this.meterLevel = meterLevel;
	}
}
