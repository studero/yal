package ch.sulco.yal.event;

import ch.sulco.yal.dm.Channel;

public class ChannelCreated extends Event {
	private final Channel channel;

	public ChannelCreated(Channel channel) {
		super();
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}
}
