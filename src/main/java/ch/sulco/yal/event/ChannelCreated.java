package ch.sulco.yal.event;


public class ChannelCreated extends Event {
	private int id;

	public ChannelCreated(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
