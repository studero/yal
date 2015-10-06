package ch.sulco.yal.dm;

public class InputChannel extends Channel {

	private Double gain;
	private RecordingState recordingState;
	private Double level;

	public InputChannel() {
		super(ChannelDirection.IN);
	}

	public Double getGain() {
		return this.gain;
	}

	public void setGain(Double gain) {
		this.gain = gain;
	}

	public RecordingState getRecordingState() {
		return this.recordingState;
	}

	public void setRecordingState(RecordingState recordingState) {
		this.recordingState = recordingState;
	}

	public Double getLevel() {
		return level;
	}

	public void setLevel(Double level) {
		this.level = level;
	}

}
