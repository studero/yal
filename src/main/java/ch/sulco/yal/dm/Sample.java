package ch.sulco.yal.dm;

import java.util.LinkedList;
import java.util.List;

import ch.sulco.yal.dsp.audio.AudioSink;

public class Sample {
	private transient Loop loop;
	private Long id;
	private String description;
	private Float gain;
	private Long channelId;
	private boolean mute = true;
	private transient List<AudioSink> players = new LinkedList<>();

	private transient byte[] data;

	public Sample() {

	}

	public Sample(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Loop getLoop() {
		return this.loop;
	}

	public void setLoop(Loop loop) {
		this.loop = loop;
	}

	public Float getGain() {
		return this.gain;
	}

	public void setGain(Float gain) {
		this.gain = gain;
	}

	public boolean isMute() {
		return this.mute;
	}

	public void setMute(boolean mute, AudioSink player, boolean doSynchronization) {
		if(player != null){
			if(mute && players.contains(player)){
				players.remove(player);
				if(loop != null && loop.isActive()){
					player.stopSample(this, doSynchronization);
				}
				if(players.isEmpty()){
					this.mute = true;
				}
			}else if(!mute && !players.contains(player)){
				players.add(player);
				if(loop != null && loop.isActive()){
					player.startSample(this, doSynchronization);
				}
				this.mute = false;
			}
		}
	}
	
	public List<AudioSink> getPlayers(){
		return players;
	}

	public Long getChannelId() {
		return this.channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public byte[] getData() {
		return this.data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
