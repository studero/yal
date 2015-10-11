package ch.sulco.yal.dsp;

import ch.sulco.yal.Application;

public class TestLooper {
	public static void main(String[] args) throws Exception {
		Application application = Application.injector.getInstance(Application.class);
		application.start();
		Thread.sleep(1000);
		application.getAudioProcessor().play();
		Thread.sleep(2000);
		application.getAudioProcessor().loop();
	}
}
