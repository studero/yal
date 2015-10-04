package ch.sulco.yal.event;

import ch.sulco.yal.dsp.audio.RecordingState;

public class ChannelStateChanged extends Event {
	private int id;
	private RecordingState recordingState;

	public ChannelStateChanged(int id, RecordingState recordingState) {
		super();
		this.id = id;
		this.recordingState = recordingState;
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
}
