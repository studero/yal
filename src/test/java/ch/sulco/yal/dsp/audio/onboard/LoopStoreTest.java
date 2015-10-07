package ch.sulco.yal.dsp.audio.onboard;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.dm.SampleClip;

@RunWith(MockitoJUnitRunner.class)
public class LoopStoreTest {

	@InjectMocks
	private LoopStore loopStore;

	@Mock
	private AudioSystemProvider audioSystemProvider;

	@Mock
	private AppConfig appConfig;

	@Ignore
	@Test
	public void testAddSample() {
		Long id1 = this.loopStore.addSample("test".getBytes());
		Long id2 = this.loopStore.addSample("test".getBytes());

		assertThat(id1, is(0));
		assertThat(id2, is(1));
	}

	@Ignore
	@Test
	public void testGetSamples() {
		this.loopStore.addSample("test".getBytes());
		this.loopStore.addSample("test".getBytes());

		Collection<SampleClip> samples = this.loopStore.getSamples();

		assertThat(samples, hasSize(2));
	}

	@Ignore
	@Test
	public void testGetSampleIds() {
		Long id1 = this.loopStore.addSample("test".getBytes());
		Long id2 = this.loopStore.addSample("test".getBytes());

		Set<Long> sampleIds = this.loopStore.getSampleIds();

		assertThat(sampleIds, containsInAnyOrder(id1, id2));
	}

}
