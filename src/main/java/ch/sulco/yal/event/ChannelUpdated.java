package ch.sulco.yal.event;

import ch.sulco.yal.dm.Channel;

public class ChannelUpdated extends Event {
	private final Channel channel;

	public ChannelUpdated(Channel channel) {
		super();
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}
}
