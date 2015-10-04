package ch.sulco.yal.event;

import java.util.Date;

public class Event {
	private final Date creationDate = new Date();

	private final String eventType = this.getClass().getSimpleName();

	public Date getCreationDate() {
		return this.creationDate;
	}

	public String getEventType() {
		return eventType;
	}
}
