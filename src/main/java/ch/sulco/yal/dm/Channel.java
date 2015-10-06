package ch.sulco.yal.dm;

import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class Channel {

	private final ChannelDirection direction;
	private Long id;
	private String name;
	private Mixer.Info mixerInfo;
	private Line.Info lineInfo;

	public Channel(ChannelDirection direction) {
		this.direction = direction;
	}

	public ChannelDirection getDirection() {
		return direction;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Mixer.Info getMixerInfo() {
		return mixerInfo;
	}

	public void setMixerInfo(Mixer.Info mixerInfo) {
		this.mixerInfo = mixerInfo;
	}

	public Line.Info getLineInfo() {
		return lineInfo;
	}

	public void setLineInfo(Line.Info lineInfo) {
		this.lineInfo = lineInfo;
	}
}
