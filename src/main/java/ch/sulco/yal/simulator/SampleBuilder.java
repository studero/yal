package ch.sulco.yal.simulator;

import ch.sulco.yal.dm.Sample;

public class SampleBuilder {
	private final Sample instance;

	SampleBuilder() {
		this.instance = new Sample();
	}

	public SampleBuilder id(Long id) {
		this.instance.setId(id);
		return this;
	}

	public SampleBuilder channelId(Long channelId) {
		this.instance.setChannelId(channelId);
		return this;
	}

	public SampleBuilder mute(boolean mute) {
		this.instance.setMute(mute);
		return this;
	}

	public Sample build() {
		return this.instance;
	}
}
