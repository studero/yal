package ch.sulco.yal.settings;

import java.util.Map;

public class AudioSettings {
	private String soundCardId;
	private Map<Integer, String> inputChannels;
	private Map<Integer, String> outputChannels;

	public String getSoundCardId() {
		return soundCardId;
	}

	public void setSoundCardId(String soundCardId) {
		this.soundCardId = soundCardId;
	}

	public Map<Integer, String> getInputChannels() {
		return inputChannels;
	}

	public void setInputChannels(Map<Integer, String> inputChannels) {
		this.inputChannels = inputChannels;
	}

	public Map<Integer, String> getOutputChannels() {
		return outputChannels;
	}

	public void setOutputChannels(Map<Integer, String> outputChannels) {
		this.outputChannels = outputChannels;
	}
}