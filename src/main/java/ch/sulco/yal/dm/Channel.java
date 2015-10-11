package ch.sulco.yal.dm;

import javax.sound.sampled.Line;

public class Channel {

	private final ChannelDirection direction;
	private Long id;
	private String name;
	private transient Line.Info lineInfo;
	private boolean monitoring;

	public Channel(ChannelDirection direction) {
		this.direction = direction;
	}

	public ChannelDirection getDirection() {
		return this.direction;
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

	public Line.Info getLineInfo() {
		return this.lineInfo;
	}

	public void setLineInfo(Line.Info lineInfo) {
		this.lineInfo = lineInfo;
	}

	public boolean isMonitoring() {
		return this.monitoring;
	}

	public void setMonitoring(boolean monitoring) {
		this.monitoring = monitoring;
	}
}
