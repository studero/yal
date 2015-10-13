package ch.sulco.yal.dm;

import java.util.ArrayList;
import java.util.List;

public class Loop {
	private Long id;
	private String name;
	private List<Sample> samples = new ArrayList<>();
	private Long length;
	private boolean active;
	private LoopState loopState;

	public Loop() {

	}

	public Loop(Long id) {
		this.id = id;
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

	public List<Sample> getSamples() {
		return this.samples;
	}

	public Long getLength() {
		return this.length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LoopState getLoopState() {
		return loopState;
	}

	public void setLoopState(LoopState loopState) {
		this.loopState = loopState;
	}
}
