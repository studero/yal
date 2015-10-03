package ch.sulco.yal.dsp;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import ch.sulco.yal.dsp.audio.Processor;
import ch.sulco.yal.dsp.cmd.CommandReceiver;
import ch.sulco.yal.dsp.cmd.LoadSample;

public class ApplicationTest {
	@Test
	public void shouldInitializeApplication() {
		AppConfig appConfig = new AppConfig();
		CommandReceiver commandReceiver = mock(CommandReceiver.class);
		Processor processor = mock(Processor.class);
		Application application = new Application(appConfig, commandReceiver, processor);

		org.junit.Assert.assertThat(application, is(not(nullValue())));
	}

	@Test
	public void shouldHandleLoadSampleCommand() {
		AppConfig appConfig = new AppConfig();
		CommandReceiver commandReceiver = mock(CommandReceiver.class);
		Processor processor = mock(Processor.class);
		Application application = new Application(appConfig, commandReceiver, processor);
		LoadSample command = new LoadSample();
		command.setData("test".getBytes());

		application.onCommand(command);

		verify(processor).putData("test".getBytes());
	}
}