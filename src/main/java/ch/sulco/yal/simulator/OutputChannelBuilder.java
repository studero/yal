package ch.sulco.yal.simulator;

import ch.sulco.yal.dm.OutputChannel;

public class OutputChannelBuilder {
	private final OutputChannel instance;

	OutputChannelBuilder() {
		this.instance = new OutputChannel();
	}

	public OutputChannelBuilder id(Long id) {
		this.instance.setId(id);
		return this;
	}

	public OutputChannelBuilder name(String name) {
		this.instance.setName(name);
		return this;
	}

	public OutputChannel build() {
		return this.instance;
	}
}
