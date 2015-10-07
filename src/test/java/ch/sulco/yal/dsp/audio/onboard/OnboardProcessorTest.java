package ch.sulco.yal.dsp.audio.onboard;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnboardProcessorTest {

	@InjectMocks
	private OnboardProcessor onboardProcessor;

	@Mock
	private LoopStore loopStore;

	@Ignore
	@Test
	public void testPutData() {
		when(this.loopStore.addSample("test".getBytes())).thenReturn(10101L);

		Long sample1 = this.onboardProcessor.putData("test".getBytes());

		verify(this.loopStore).addSample("test".getBytes());
		assertThat(sample1, is(10101));
	}

	@Ignore
	@Test
	public void testGetSampleIds() {
		when(this.loopStore.getSampleIds()).thenReturn(Sets.newSet(10101L, 20202L));

		Set<Long> sampleIds = this.onboardProcessor.getSampleIds();

		assertThat(sampleIds, containsInAnyOrder(10101L, 20202L));
	}
}
