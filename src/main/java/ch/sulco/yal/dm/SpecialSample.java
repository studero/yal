package ch.sulco.yal.dm;

public enum SpecialSample {
	CLICK;
	
	public Long getId(){
		return Long.valueOf(ordinal());
	}
}
