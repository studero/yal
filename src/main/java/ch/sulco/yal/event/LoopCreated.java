package ch.sulco.yal.event;

import ch.sulco.yal.dm.Loop;

public class LoopCreated extends Event {
	private final Loop loop;

	LoopCreated(Loop loop) {
		super();
		this.loop = loop;
	}

	public Loop getLoop() {
		return this.loop;
	}
}
