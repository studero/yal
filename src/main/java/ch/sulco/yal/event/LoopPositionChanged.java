package ch.sulco.yal.event;

public class LoopPositionChanged extends Event {
	private Integer loopPosition;

	public LoopPositionChanged(Integer loopPosition) {
		super();
		this.setLoopPosition(loopPosition);
	}

	public Integer getLoopPosition() {
		return this.loopPosition;
	}

	public void setLoopPosition(Integer loopPosition) {
		this.loopPosition = loopPosition;
	}
}
