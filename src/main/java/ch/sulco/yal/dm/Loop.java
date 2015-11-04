package ch.sulco.yal.dm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class Loop {

	private Long id;
	private String name;
	private List<Sample> samples = new ArrayList<>();
	private Long timeLength = 0L;
	private Long position = 0L;
	private int dataLength = 0;
	private boolean active;

	private transient Sample clickTrack;
	private Integer bars = 1;
	private Integer beats = 4;
	private boolean clickTrackMuted;

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

	public void updateSample(Sample sample) {
		if (samples.stream().anyMatch(s -> Objects.equals(s.getId(), sample.getId()))) {
			samples.removeIf(s -> Objects.equals(s.getId(), sample.getId()));
			samples.add(sample);
		}
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

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
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

	public Sample getClickTrack() {
		return clickTrack;
	}

	public void setClickTrack(Sample clickTrack) {
		this.clickTrack = clickTrack;
	}

	public Integer getBars() {
		return bars;
	}

	public void setBars(Integer bars) {
		this.bars = bars;
	}

	public Integer getBeats() {
		return beats;
	}

	public void setBeats(Integer beats) {
		this.beats = beats;
	}

	public boolean isClickTrackMuted() {
		return clickTrackMuted;
	}

	public void setClickTrackMuted(boolean clickTrackMuted) {
		this.clickTrackMuted = clickTrackMuted;
	}
}
