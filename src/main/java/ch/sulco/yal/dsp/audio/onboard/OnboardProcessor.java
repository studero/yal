package ch.sulco.yal.dsp.audio.onboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;

import ch.sulco.yal.Application;
import ch.sulco.yal.dm.Channel;
import ch.sulco.yal.dm.InputChannel;
import ch.sulco.yal.dm.OutputChannel;
import ch.sulco.yal.dm.RecordingState;
import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.dsp.dm.SampleClip;
import ch.sulco.yal.event.ChannelCreated;
import ch.sulco.yal.event.Event;
import ch.sulco.yal.event.EventListener;
import ch.sulco.yal.event.EventManager;
import ch.sulco.yal.event.LoopLengthChanged;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

@Singleton
public class OnboardProcessor implements Processor, EventListener {

	private final static Logger log = Logger.getLogger(OnboardProcessor.class.getName());

	@Inject
	private LoopStore loopStore;

	@Inject
	private EventManager eventManager;

	private final Map<Long, Recorder> recorders = new HashMap<>();
	private final Map<Long, Player> players = new HashMap<>();

	@PostConstruct
	public void setup() {
		log.info("Setup");
		this.eventManager.addListener(this);
	}

	public LoopStore getLoopStore() {
		return this.loopStore;
	}

	@Override
	public Set<Long> getSampleIds() {
		return this.loopStore.getSampleIds();
	}

	@Override
	public byte[] getData(int sampleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void play() {
		log.info("Play");
		for (Recorder recorder : this.recorders.values()) {
			recorder.startRecord();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loop() {
		log.info("Loop");
		for (Recorder recorder : this.recorders.values()) {
			recorder.stopRecord();
		}

		log.info("Start Sample");
		log.info("Player " + this.players.get(0));
		Optional<Player> firstPlayer = FluentIterable.from(this.players.values()).first();
		if (firstPlayer.isPresent())
			firstPlayer.get().startSample(this.loopStore.getSample(0L));

		this.eventManager.addEvent(new LoopLengthChanged(this.loopStore.getLoopLength()));
	}

	@Override
	public void setChannelRecording(Long channelId, boolean recording) {
		if (recording) {
			this.recorders.get(channelId).startRecord();
		} else {
			this.recorders.get(channelId).stopRecord();
		}
	}

	@Override
	public void setSampleMute(Long sampleId, boolean mute) {
		// TODO get player by playerId
		long playerId = 0;
		Player player = this.players.get(playerId);
		SampleClip sample = this.loopStore.getSample(sampleId);
		if (sample != null) {
			if (mute) {
				player.stopSample(sample);
			} else {
				player.startSample(sample);
			}
		}

	}

	@Override
	public void setSampleVolume(Long sampleId, float volume) {
		FloatControl control = (FloatControl) this.loopStore.getSample(sampleId).getClip().getControl(Type.MASTER_GAIN);
		control.setValue(volume);
	}

	@Override
	public void removeSample(Long sampleId) {
		// TODO Auto-generated method stub

	}

	@Override
	public Long putData(byte[] data) {
		return this.loopStore.addSample(data);
	}

	@Override
	public RecordingState getChannelRecordingState(Long channelId) {
		return this.recorders.get(channelId).getRecordingState();
	}

	@Override
	public boolean isSampleMute(Long sampleId) {
		BooleanControl control = (BooleanControl) this.loopStore.getSample(sampleId).getClip().getControl(BooleanControl.Type.MUTE);
		return control.getValue();
	}

	@Override
	public float getSampleVolume(Long sampleId) {
		FloatControl control = (FloatControl) this.loopStore.getSample(sampleId).getClip().getControl(Type.MASTER_GAIN);
		return control.getValue();
	}

	@Override
	public Long getLoopLength() {
		return this.loopStore.getSampleIds().isEmpty() ? null : this.loopStore.getSample(0L).getClip().getMicrosecondLength();
	}

	@Override
	public boolean getChannelMonitoring(Long channelId) {
		return this.recorders.get(channelId).isMonitoring();
	}

	@Override
	public void setChannelMonitoring(Long channelId, boolean monitoring) {
		this.recorders.get(channelId).setMonitoring(monitoring);
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof ChannelCreated) {
			ChannelCreated channelCreated = (ChannelCreated) event;
			Channel channel = channelCreated.getChannel();
			if (channel instanceof InputChannel) {
				InputChannel inputChannel = (InputChannel) channel;
				if (!this.recorders.containsKey(channel.getId())) {
					Recorder recorder = Application.injector.getInstance(Recorder.class);
					recorder.setInputChannel(inputChannel);
					recorder.initialize();
					this.recorders.put(inputChannel.getId(), recorder);
				}
			} else if (channel instanceof OutputChannel) {
				OutputChannel outputChannel = (OutputChannel) channel;
				if (!this.players.containsKey(outputChannel.getId())) {
					Player player = Application.injector.getInstance(Player.class);
					this.players.put(outputChannel.getId(), player);
				}
			}
		}
	}

}
