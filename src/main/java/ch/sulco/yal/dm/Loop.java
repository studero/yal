package ch.sulco.yal.dm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Loop {
	private Long id;
	private String name;
	private List<Sample> samples = new ArrayList<>();
	private Long timeLength = 0L;
	private int dataLength = 0;
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

	public void addSample(Sample sample) {
		this.samples.add(sample);
	}

	public List<Sample> getSamples() {
		return this.samples;
	}

	public Long getTimeLength() {
		return this.timeLength;
	}

	public void setTimeLength(Long timeLength) {
		this.timeLength = timeLength;
	}

	public int getDataLength() {
		return this.dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
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
		byte[] clickBytes = new byte[this.timeLength.intValue()];
		Arrays.fill(clickBytes, (byte) 0);
		int bytesPerBeat = this.timeLength.intValue()/(bars*beats);
		for(int beat=0; beat<bars*beats; beat++){
			if(beat%beats == 0){
				Arrays.fill(clickBytes, beat*bytesPerBeat, beat*bytesPerBeat+100, (byte) 80);
			}else{
				Arrays.fill(clickBytes, beat*bytesPerBeat, beat*bytesPerBeat+100, (byte) 20);
			}
		}
	}
}
