package ch.sulco.yal.dm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Loop {
	private Long id;
	private String name;
	private List<Sample> samples = new ArrayList<>();
	private Long length = 0L;
	private boolean active;
	private LoopState loopState;

	public Loop() {

	}

	public Loop(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getNumSamples() {
		return Long.valueOf(this.samples.size());
	}

	public List<Sample> getSamples() {
		return this.samples;
	}

	public Long getLength() {
		return this.length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LoopState getLoopState() {
		return this.loopState;
	}

	public void setLoopState(LoopState loopState) {
		this.loopState = loopState;
	}
	
	public void createClickTrack(int bars, int beats) {
		byte[] clickBytes = new byte[this.length.intValue()];
		Arrays.fill(clickBytes, (byte) 0);
		int bytesPerBeat = this.length.intValue()/(bars*beats);
		for(int beat=0; beat<bars*beats; beat++){
			if(beat%beats == 0){
				Arrays.fill(clickBytes, beat*bytesPerBeat, beat*bytesPerBeat+100, (byte) 80);
			}else{
				Arrays.fill(clickBytes, beat*bytesPerBeat, beat*bytesPerBeat+100, (byte) 20);
			}
		}
	}
}
