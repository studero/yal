package ch.sulco.yal.dm;

import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import com.google.gson.Gson;
import com.jayway.jsonassert.JsonAssert;

public class MappingTest {

	@Test
	public void shouldBeSerializable() {
		Mapping mapping = new Mapping();
		mapping.setProcessorMethod("setChannelRecording");
		mapping.setProcessorMethodArguments(new LinkedList<>());
		mapping.getProcessorMethodArguments().add(new MappingMethodArgument("channelId", Long.class.getName()));
		mapping.getProcessorMethodArguments().add(new MappingMethodArgument("recording", Boolean.class.getName()));
		mapping.setTriggerValueMap(new HashMap<>());
		mapping.getTriggerValueMap().put("command", 1);
		mapping.getTriggerValueMap().put("channel", 2);
		mapping.getTriggerValueMap().put("data1", 3);
		mapping.getTriggerValueMap().put("data2", 4);
		mapping.setValueExpressionMap(new HashMap<>());
		mapping.getValueExpressionMap().put("channelId", "#{data1} * 10");
		mapping.getValueExpressionMap().put("recording", "#{data2} == 4");

		String json = new Gson().toJson(mapping);

		JsonAssert.with(json)
				.assertThat("processorMethod", is("setChannelRecording"))
				.assertThat("processorMethodArguments[0].name", is("channelId"))
				.assertThat("processorMethodArguments[0].type", is("java.lang.Long"))
				.assertThat("processorMethodArguments[1].name", is("recording"))
				.assertThat("processorMethodArguments[1].type", is("java.lang.Boolean"))
				.assertThat("triggerValueMap.command", is(1))
				.assertThat("triggerValueMap.channel", is(2))
				.assertThat("triggerValueMap.data1", is(3))
				.assertThat("triggerValueMap.data2", is(4))
				.assertThat("valueExpressionMap.channelId", is("#{data1} * 10"))
				.assertThat("valueExpressionMap.recording", is("#{data2} == 4"));
	}
}
