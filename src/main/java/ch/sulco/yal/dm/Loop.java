package ch.sulco.yal.dm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import ch.sulco.yal.dsp.audio.AudioSink;
import ch.sulco.yal.dsp.audio.onboard.Synchronizer;

public class Loop {
	@Inject
	private Synchronizer synchronizer;
	
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
		sample.setLoop(this);
	}

	public List<Sample> getSamples() {
		return this.samples;
	}	
	
	public Sample getSample(long sampleId) {
		return FluentIterable.from(this.samples).firstMatch(new Predicate<Sample>() {
			@Override
			public boolean apply(Sample input) {
				return input.getId() == sampleId;
			}
		}).orNull();
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
		for(Sample sample : this.samples){
			for(AudioSink player : sample.getPlayers()){
				if(active){
					player.startSample(sample, true);
				}else{
					player.stopSample(sample, true);
				}
			}
		}
		if(active && !this.samples.isEmpty()){
			this.synchronizer.initialize(timeLength);
		}
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
