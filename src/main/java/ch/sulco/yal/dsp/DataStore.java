package ch.sulco.yal.dsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Singleton;

import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.Loop;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Singleton
public class DataStore {

	private List<Loop> loops = new ArrayList<>();
	private List<Channel> channels = new ArrayList<>();

	public List<Loop> getLoops() {
		return this.loops;
	}

	public Loop getLoop(final Long id) {
		return FluentIterable.from(this.loops).firstMatch(new Predicate<Loop>() {
			@Override
			public boolean apply(Loop input) {
				return Objects.equals(id, input.getId());
			}
		}).orNull();
	}

	public void addLoop(Loop loop) {
		this.loops.add(loop);
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

}
