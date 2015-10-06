package ch.sulco.yal.dm;

public class OutputChannel extends Channel {

	private double volume;

	public OutputChannel() {
		super(ChannelDirection.OUT);
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

}
