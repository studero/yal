package ch.sulco.yal.dm;


public class InputChannel extends Channel {

	private Double gain;
	private RecordingState recordingState;

	public InputChannel() {
		super(ChannelDirection.IN);
	}

	public Double getGain() {
		return gain;
	}

	public void setGain(Double gain) {
		this.gain = gain;
	}

	public RecordingState getRecordingState() {
		return recordingState;
	}

	public void setRecordingState(RecordingState recordingState) {
		this.recordingState = recordingState;
	}

}
