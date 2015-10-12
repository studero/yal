package ch.sulco.yal.simulator;

import ch.sulco.yal.dm.InputChannel;

public class InputChannelBuilder {
	private final InputChannel instance;

	InputChannelBuilder() {
		this.instance = new InputChannel();
	}

	public InputChannelBuilder id(Long id) {
		this.instance.setId(id);
		return this;
	}

	public InputChannelBuilder name(String name) {
		this.instance.setName(name);
		return this;
	}

	public InputChannel build() {
		return this.instance;
	}
}
