package ch.sulco.yal.event;

import ch.sulco.yal.dm.Sample;

public class SampleCreated extends Event {
	private final Sample sample;

	SampleCreated(Sample sample) {
		super();
		this.sample = sample;
	}

	public Sample getSample() {
		return sample;
	}
}
