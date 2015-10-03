package ch.sulco.yal.dsp;

import ch.sulco.yal.Application;

public class TestLooper {
	public static void main(String[] args) throws Exception {
		Application application = new Application();
		System.out.println(application.getAudioProcessor().getLoopLength());
		application.getAudioProcessor().play();
		Thread.sleep(10000);
		application.getAudioProcessor().loop();
		
		System.out.println(application.getAudioProcessor().getSampleVolume(0));
		System.out.println(application.getAudioProcessor().isSampleMute(0));
		System.out.println(application.getAudioProcessor().getLoopLength());
	}
}
