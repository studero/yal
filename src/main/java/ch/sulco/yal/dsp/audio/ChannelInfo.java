package ch.sulco.yal.dsp.audio;

import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.Mixer;

public class ChannelInfo {
	private int id;
	private Mixer.Info mixerInfo;
	private Line.Info lineInfo;

	public ChannelInfo(int id, Mixer.Info mixerInfo, Line.Info lineInfo){
		this.id = id;
		this.mixerInfo = mixerInfo;
		this.lineInfo = lineInfo;
	}
	
	public int getId(){
		return id;
	}

	public String getDescription(){
		return mixerInfo.toString()+" - "+lineInfo.toString();
	}

	public Info getLineInfo() {
		return lineInfo;
	}
}
