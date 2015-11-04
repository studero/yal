package ch.sulco.yal.dm;

import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.google.gson.Gson;
import com.jayway.jsonassert.JsonAssert;

public class LoopTest {

	@Test
	public void shouldBeSerializable() {
		Loop loop = new Loop();
		loop.setActive(true);
		loop.setBars(12);
		loop.setBeats(4);
		loop.setClickTrack(new Sample());
		loop.setClickTrackMuted(true);
		loop.setDataLength(120000);
		loop.setId(121212L);
		loop.setName("main");
		loop.setTimeLength(40000L);

		String json = new Gson().toJson(loop);

		JsonAssert.with(json)
				.assertThat("active", is(true))
				.assertThat("bars", is(12))
				.assertThat("beats", is(4))
				.assertThat("clickTrackMuted", is(true))
				.assertThat("dataLength", is(120000))
				.assertThat("id", is(121212))
				.assertThat("name", is("main"))
				.assertThat("timeLength", is(40000));
	}
}
