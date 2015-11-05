package ch.sulco.yal.dsp.audio.onboard;

public class SyncAdjustment {
	private Long lowestSamplePosition;
	private Long highestSamplePosition;
	private Long averageSamplePosition;

	public SyncAdjustment(Long lowestSamplePosition, Long highestSamplePosition, Long averageSamplePosition) {
		this.setLowestSamplePosition(lowestSamplePosition);
		this.setHighestSamplePosition(highestSamplePosition);
		this.setAverageSamplePosition(averageSamplePosition);
	}

	public Long getLowestSamplePosition() {
		return lowestSamplePosition;
	}

	public Long getHighestSamplePosition() {
		return highestSamplePosition;
	}

	public Long getAverageSamplePosition() {
		return averageSamplePosition;
	}

	public void setLowestSamplePosition(Long lowestSamplePosition) {
		this.lowestSamplePosition = lowestSamplePosition;
	}

	public void setHighestSamplePosition(Long highestSamplePosition) {
		this.highestSamplePosition = highestSamplePosition;
	}

	public void setAverageSamplePosition(Long averageSamplePosition) {
		this.averageSamplePosition = averageSamplePosition;
	}

}
