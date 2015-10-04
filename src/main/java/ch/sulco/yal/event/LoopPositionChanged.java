package ch.sulco.yal.event;

public class LoopPositionChanged extends Event {
	private Long loopPosition;

	public LoopPositionChanged(Long loopPosition) {
		super();
		this.setLoopPosition(loopPosition);
	}

	public Long getLoopPosition() {
		return loopPosition;
	}

	public void setLoopPosition(Long loopPosition) {
		this.loopPosition = loopPosition;
	}
}
