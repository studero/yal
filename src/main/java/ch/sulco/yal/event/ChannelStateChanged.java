package ch.sulco.yal.event;

import ch.sulco.yal.dm.RecordingState;

public class ChannelStateChanged extends Event {
	private int id;
	private RecordingState recordingState;
	private boolean monitoring;

	public ChannelStateChanged(int id, RecordingState recordingState, boolean monitoring) {
		super();
		this.id = id;
		this.recordingState = recordingState;
		this.setMonitoring(monitoring);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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
}
