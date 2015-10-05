package ch.sulco.yal.event;

public class ChannelMonitorValueChanged extends Event {
	private int id;
	private double value;

	public ChannelMonitorValueChanged(int id, double value) {
		super();
		this.id = id;
		this.setValue(value);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
