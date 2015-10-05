package ch.sulco.yal.event;

public class LoopLengthChanged extends Event {
	private Integer loopLength;

	public LoopLengthChanged(Integer loopLength) {
		super();
		this.setLoopLength(loopLength);
	}

	public Integer getLoopLength() {
		return this.loopLength;
	}

	public void setLoopLength(Integer loopLength) {
		this.loopLength = loopLength;
	}
}
