package ch.sulco.yal.dsp;

import com.google.inject.Guice;

import ch.sulco.yal.Application;
import ch.sulco.yal.PostConstructModule;
import ch.sulco.yal.YalModule;

public class TestLooper {
	public static void main(String[] args) throws Exception {
		Application application = Guice.createInjector(new YalModule(), new PostConstructModule()).getInstance(Application.class);
		application.start();
		System.out.println(application.getAudioProcessor().getLoopLength());
		Thread.sleep(500);
		application.getAudioProcessor().play();
		Thread.sleep(1000);
		application.getAudioProcessor().loop();
	}
}
