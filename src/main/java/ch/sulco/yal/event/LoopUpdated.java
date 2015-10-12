package ch.sulco.yal.event;

import ch.sulco.yal.dm.Loop;

public class LoopUpdated extends Event {
	private final Loop loop;

	LoopUpdated(Loop loop) {
		super();
		this.loop = loop;
	}

	public Loop getLoop() {
		return loop;
	}
}
