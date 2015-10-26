package ch.sulco.yal.settings;

import java.util.List;

public class AudioSettings {
	private String soundCardId;
	private List<String> inputChannelIds;
	private List<String> outputChannelIds;

	public String getSoundCardId() {
		return soundCardId;
	}

	public void setSoundCardId(String soundCardId) {
		this.soundCardId = soundCardId;
	}

	public List<String> getInputChannelIds() {
		return inputChannelIds;
	}

	public void setInputChannelIds(List<String> inputChannelIds) {
		this.inputChannelIds = inputChannelIds;
	}

	public List<String> getOutputChannelIds() {
		return outputChannelIds;
	}

	public void setOutputChannelIds(List<String> outputChannelIds) {
		this.outputChannelIds = outputChannelIds;
	}
}