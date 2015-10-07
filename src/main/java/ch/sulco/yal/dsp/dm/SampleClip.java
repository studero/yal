package ch.sulco.yal.dsp.dm;

import javax.sound.sampled.Clip;

public class SampleClip {
	private final Long id;
	private final Clip clip;

	public SampleClip(Long id, Clip clip) {
		this.id = id;
		this.clip = clip;
	}

	public Long getId() {
		return this.id;
	}

	public Clip getClip() {
		return this.clip;
	}
}
