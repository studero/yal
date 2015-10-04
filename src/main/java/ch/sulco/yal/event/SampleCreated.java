package ch.sulco.yal.event;


public class SampleCreated extends Event {
	private int id;

	public SampleCreated(int id) {
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
