package ch.sulco.yal.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Singleton;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Singleton
public class EventManager {
	private final static Logger log = Logger.getLogger(EventManager.class.getName());

	private final List<Event> events = new ArrayList<>();

	private final List<EventListener> listeners = new ArrayList<>();

	public void addListener(EventListener listener) {
		this.listeners.add(listener);
	}

	public void addEvent(Event event) {
		this.events.add(event);
		for (EventListener listener : this.listeners) {
			listener.onEvent(event);
		}
	}

	public List<Event> getEvents(Date since) {
		return FluentIterable.from(this.events).filter(new Predicate<Event>() {
			@Override
			public boolean apply(Event input) {
				return input.getCreationDate().after(since);
			}
		}).toList();
	}
}
