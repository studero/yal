package ch.sulco.yal.simulator;

import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.LoopState;
import ch.sulco.yal.dm.Sample;

public class LoopBuilder {
	private final Loop instance;

	LoopBuilder() {
		this.instance = new Loop();
		this.instance.setLoopState(LoopState.STOPPED);
	}

	public LoopBuilder id(Long id) {
		this.instance.setId(id);
		return this;
	}

	public LoopBuilder name(String name) {
		this.instance.setName(name);
		return this;
	}

	public LoopBuilder length(Long length) {
		this.instance.setLength(length);
		return this;
	}

	public LoopBuilder active(boolean active) {
		this.instance.setActive(active);
		return this;
	}

	public LoopBuilder sample(Sample sample) {
		this.instance.getSamples().add(sample);
		return this;
	}

	public Loop build() {
		return this.instance;
	}
}
