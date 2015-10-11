package ch.sulco.yal.dm;

public class Sample {
	private Long id;
	private String description;
	private Long length;
	private Float gain;
	private boolean mute;
	private Long channelId;
	private byte[] data;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getLength() {
		return this.length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public Float getGain() {
		return this.gain;
	}

	public void setGain(Float gain) {
		this.gain = gain;
	}

	public boolean isMute() {
		return this.mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}

	public Long getChannelId() {
		return this.channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
