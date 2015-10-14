package ch.sulco.yal.dsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.Loop;
import ch.sulco.yal.dm.LoopState;
import ch.sulco.yal.dm.Sample;
import ch.sulco.yal.event.EventManager;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Singleton
public class DataStore {
	private final static Logger log = LoggerFactory.getLogger(DataStore.class);

	@Inject
	private EventManager eventManager;

	private List<Loop> loops = new ArrayList<>();
	private List<Channel> channels = new ArrayList<>();

	public void setup() {
		log.info("Setup");
		Loop loop = new Loop();
		loop.setId(0L);
		loop.setActive(true);
		loop.setLoopState(LoopState.STOPPED);
		this.addLoop(loop);
	}

	public List<Loop> getLoops() {
		return this.loops;
	}

	public Loop getCurrentLoop() {
		return FluentIterable.from(this.loops).firstMatch(new Predicate<Loop>() {
			@Override
			public boolean apply(Loop input) {
				return input.isActive();
			}
		}).orNull();
	}

	public Sample getCurrentLoopSample(long sampleId) {
		return FluentIterable.from(this.getCurrentLoop().getSamples()).firstMatch(new Predicate<Sample>() {
			@Override
			public boolean apply(Sample input) {
				return input.getId() == sampleId;
			}
		}).orNull();
	}

	public Loop getLoop(final Long id) {
		return FluentIterable.from(this.loops).firstMatch(new Predicate<Loop>() {
			@Override
			public boolean apply(Loop input) {
				return id == input.getId();
			}
		}).orNull();
	}

	public void addLoop(Loop loop) {
		this.loops.add(loop);
	}

	public void addSample(Long loopId, Sample sample) {
		this.getLoop(loopId).getSamples().add(sample);
	}

	public List<Channel> getChannels() {
		return this.channels;
	}

	public Channel getChannel(final Long id) {
		return FluentIterable.from(this.channels).firstMatch(new Predicate<Channel>() {
			@Override
			public boolean apply(Channel input) {
				return Objects.equals(id, input.getId());
			}
		}).orNull();
	}

	public void addChannel(Channel channel) {
		this.channels.add(channel);
	}

	public void setCurrentLoopId(Long currentLoopId) {
		Loop before = this.getCurrentLoop();
		Loop after = this.getLoop(currentLoopId);
		if (before != null)
			before.setActive(false);
		after.setActive(true);
		if (before != null)
			this.eventManager.updateLoop(before);
		this.eventManager.updateLoop(after);
	}

}
