package ch.sulco.yal.dm;

public enum SpecialSample {
	CLICK(99L);
	
	private final Long id;
	
	private SpecialSample(Long id) {
		this.id = id;
	}
	
	public Long getId(){
		return this.id;
	}
}
