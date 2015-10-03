package ch.sulco.yal.dsp.dm;

import javax.sound.sampled.Clip;

public class Sample {
	private int id;
	private Clip clip;
	
	public Sample(int id, Clip clip){
		this.id = id;
		this.clip = clip;
	}
	
	public int getId() {
		return id;
	}
	
	public Clip getClip() {
		return clip;
	}
}
