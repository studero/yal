package ch.sulco.yal.event;

import ch.sulco.yal.dm.Sample;

public class SampleUpdated extends Event {
	private final Sample sample;

	SampleUpdated(Sample sample) {
		super();
		this.sample = sample;
	}

	public Sample getSample() {
		return sample;
	}
}
