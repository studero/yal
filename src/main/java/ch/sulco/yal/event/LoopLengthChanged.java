package ch.sulco.yal.event;

public class LoopLengthChanged extends Event {
	private long loopLength;

	public LoopLengthChanged(long loopLength) {
		super();
		this.setLoopLength(loopLength);
	}

	public long getLoopLength() {
		return loopLength;
	}

	public void setLoopLength(long loopLength) {
		this.loopLength = loopLength;
	}
}
