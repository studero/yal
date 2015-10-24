package ch.sulco.yal.dm;

import java.util.ArrayList;
import java.util.List;

public class Song {
	private Long id;
	private String name;
	private List<Loop> samples = new ArrayList<>();

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

	public List<Loop> getSamples() {
		return samples;
	}

	public void setSamples(List<Loop> samples) {
		this.samples = samples;
	}
}
