package ch.sulco.yal.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedList;

import net.sourceforge.jeval.Evaluator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ch.sulco.yal.dm.Mapping;
import ch.sulco.yal.dm.MappingMethodArgument;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.audio.Processor;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class ControlHandlerTest {

	@InjectMocks
	private ControlHandler controlHandler;

	@Mock
	private DataStore dataStore;

	@Mock
	private Processor processor;

	@Test
	public void shouldTriggerProcessorMethodWithoutParameters() {
		Mapping mapping = new Mapping();
		mapping.setProcessorMethod("play");
		mapping.setTriggerValueMap(new HashMap<>());
		mapping.getTriggerValueMap().put("command", 1);
		mapping.getTriggerValueMap().put("channel", 2);
		mapping.getTriggerValueMap().put("data1", 3);
		mapping.getTriggerValueMap().put("data2", 4);
		when(this.dataStore.getMappings()).thenReturn(Lists.newArrayList(mapping));

		this.controlHandler.handleMessage(1, 2, 3, 4);

		verify(this.processor).play();
	}

	@Test
	public void shouldTriggerProcessorMethodWithParameters() {
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
		when(this.dataStore.getMappings()).thenReturn(Lists.newArrayList(mapping));

		this.controlHandler.handleMessage(1, 2, 3, 4);

		verify(this.processor).setChannelRecording(30L, true);
	}

	@Test
	public void test() throws Exception {
		Evaluator e = new Evaluator();
		e.putVariable("data1", "3");
		System.out.println(e.evaluate("#{data1}==3"));
	}
}
