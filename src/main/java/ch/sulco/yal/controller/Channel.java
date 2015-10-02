package ch.sulco.yal.controller;

import ch.sulco.yal.dsp.audio.RecordingState;

public class Channel {
	private int id;
	private String name;
	private RecordingState recordingState;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RecordingState getRecordingState() {
		return recordingState;
	}
	public void setRecordingState(RecordingState recordingState) {
		this.recordingState = recordingState;
	}
}
