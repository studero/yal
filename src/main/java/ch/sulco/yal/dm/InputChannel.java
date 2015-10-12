package ch.sulco.yal.dm;

public class InputChannel extends Channel {

	private Float gain;
	private RecordingState recordingState;
	private Float level;
	private boolean overdubbing;

	public InputChannel(Long id) {
		super(ChannelDirection.IN);
		this.setId(id);
	}

	public Float getGain() {
		return this.gain;
	}

	public void setGain(Float gain) {
		this.gain = gain;
	}

	public RecordingState getRecordingState() {
		return this.recordingState;
	}

	public void setRecordingState(RecordingState recordingState) {
		this.recordingState = recordingState;
	}

	public Float getLevel() {
		return this.level;
	}

	public void setLevel(Float level) {
		this.level = level;
	}

	public boolean isOverdubbing() {
		return this.overdubbing;
	}

	public void setOverdubbing(boolean overdubbing) {
		this.overdubbing = overdubbing;
	}

}
