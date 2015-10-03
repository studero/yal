package ch.sulco.yal.dsp;

import ch.sulco.yal.dsp.audio.onboard.AudioSystemProvider;
import ch.sulco.yal.dsp.audio.onboard.LoopStore;
import ch.sulco.yal.dsp.audio.onboard.OnboardProcessor;
import ch.sulco.yal.dsp.audio.onboard.Player;
import ch.sulco.yal.dsp.audio.onboard.Recorder;
import ch.sulco.yal.dsp.cmd.SocketCommandReceiver;

public class TestLooper {
	public static void main(String[] args) throws Exception {
		AppConfig appConfig = new AppConfig();
		Player player = new Player();
		LoopStore loopStore = new LoopStore(appConfig, new AudioSystemProvider());
		Recorder recorder = new Recorder(appConfig, player, loopStore);
		Recorder recorder2 = new Recorder(appConfig, player, loopStore);
		Application application = new Application(appConfig, new SocketCommandReceiver(appConfig), new OnboardProcessor(player, loopStore,
				recorder, 
				recorder2));
		System.out.println(application.getAudioProcessor().getLoopLength());
		application.getAudioProcessor().play();
		Thread.sleep(10000);
		application.getAudioProcessor().loop();
		
		System.out.println(application.getAudioProcessor().getSampleVolume(0));
		System.out.println(application.getAudioProcessor().isSampleMute(0));
		System.out.println(application.getAudioProcessor().getLoopLength());
	}
}
