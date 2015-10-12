package ch.sulco.yal.dm;

public class OutputChannel extends Channel {

	private double volume;

	public OutputChannel() {
		this(null);
	}

	public OutputChannel(Long id) {
		super(ChannelDirection.OUT);
		this.setId(id);
	}

	public double getVolume() {
		return this.volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

}
