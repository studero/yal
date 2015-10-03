package ch.sulco.yal.dsp.audio.onboard;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.dm.Sample;

public class LoopStoreTest {
	@Ignore
	@Test
	public void testAddSample() {
		AudioSystemProvider audioSystemProvider = mock(AudioSystemProvider.class);
		LoopStore loopStore = new LoopStore(new AppConfig(), audioSystemProvider);

		int id1 = loopStore.addSample("test".getBytes());
		int id2 = loopStore.addSample("test".getBytes());

		assertThat(id1, is(0));
		assertThat(id2, is(1));
	}

	@Ignore
	@Test
	public void testGetSamples() {
		AudioSystemProvider audioSystemProvider = mock(AudioSystemProvider.class);
		LoopStore loopStore = new LoopStore(new AppConfig(), audioSystemProvider);
		loopStore.addSample("test".getBytes());
		loopStore.addSample("test".getBytes());

		Collection<Sample> samples = loopStore.getSamples();

		assertThat(samples, hasSize(2));
	}

	@Ignore
	@Test
	public void testGetSampleIds() {
		AudioSystemProvider audioSystemProvider = mock(AudioSystemProvider.class);
		LoopStore loopStore = new LoopStore(new AppConfig(), audioSystemProvider);
		int id1 = loopStore.addSample("test".getBytes());
		int id2 = loopStore.addSample("test".getBytes());

		Set<Integer> sampleIds = loopStore.getSampleIds();

		assertThat(sampleIds, containsInAnyOrder(id1, id2));
	}

}
