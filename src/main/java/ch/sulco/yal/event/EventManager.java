package ch.sulco.yal.event;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.Sample;

@Singleton
public class EventManager {
	private final static Logger log = LoggerFactory.getLogger(EventManager.class);

	private final List<Event> events = new ArrayList<>();

	private final List<EventListener> listeners = new ArrayList<>();

	public void addListener(EventListener listener) {
		this.listeners.add(listener);
	}

	public void createChannel(Channel channel) {
		addEvent(new ChannelCreated(channel));
	}

	public void updateChannel(Channel channel) {
		addEvent(new ChannelUpdated(channel));
	}

	public void changeLoopLength(Long loopLength) {
		addEvent(new LoopLengthChanged(loopLength));
	}

	public void createSample(Sample sample) {
		addEvent(new SampleCreated(sample));
	}

	public void updateSample(Sample sample) {
		addEvent(new SampleUpdated(sample));
	}

	private void addEvent(Event event) {
		this.events.add(event);
		for (EventListener listener : this.listeners) {
			listener.onEvent(event);
		}
	}
}
