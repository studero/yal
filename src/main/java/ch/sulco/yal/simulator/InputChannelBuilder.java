package ch.sulco.yal.simulator;

import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dm.RecordingState;

public class InputChannelBuilder {
	private final InputChannel instance;

	InputChannelBuilder() {
		this.instance = new InputChannel();
		this.instance.setRecordingState(RecordingState.STOPPED);
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
