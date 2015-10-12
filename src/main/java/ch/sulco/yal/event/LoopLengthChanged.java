package ch.sulco.yal.event;

public class LoopLengthChanged extends Event {
	private Long loopLength;

	LoopLengthChanged(Long loopLength) {
		super();
		this.setLoopLength(loopLength);
	}

	public Long getLoopLength() {
		return this.loopLength;
	}

	public void setLoopLength(Long loopLength) {
		this.loopLength = loopLength;
	}
}
