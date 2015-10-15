package ch.sulco.yal.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sulco.yal.dm.Mapping;
import ch.sulco.yal.dm.MappingMethodArgument;
import ch.sulco.yal.dsp.DataStore;
import ch.sulco.yal.dsp.audio.Processor;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

@Singleton
public class ControlHandler {

	private static final Logger log = LoggerFactory.getLogger(ControlHandler.class);

	@Inject
	private DataStore dataStore;

	@Inject
	private Processor processor;

	private final Evaluator evaluator = new Evaluator();

	public void handleMessage(int command, int channel, int data1, int data2) {
		for (Mapping mapping : this.dataStore.getMappings()) {
			if (Objects.equals(mapping.getTriggerValueMap().get("command"), command)
					&& Objects.equals(mapping.getTriggerValueMap().get("channel"), channel)
					&& Objects.equals(mapping.getTriggerValueMap().get("data1"), data1)
					&& Objects.equals(mapping.getTriggerValueMap().get("data2"), data2)) {
				log.info("Trigger Mapping [" + mapping + "]");
				try {
					if (mapping.getProcessorMethodArguments() != null) {
						List<Class<?>> argumentClasses = FluentIterable.from(mapping.getProcessorMethodArguments())
								.transform(new Function<MappingMethodArgument, Class<?>>() {
									@Override
									public Class<?> apply(MappingMethodArgument input) {
										try {
											return Class.forName(input.getType());
										} catch (ClassNotFoundException e) {
											return null;
										}
									}
								}).toList();
						List<Object> argumentValues = FluentIterable.from(mapping.getProcessorMethodArguments())
								.transform(new Function<MappingMethodArgument, Object>() {
									@Override
									public Object apply(MappingMethodArgument input) {
										String expression = mapping.getValueExpressionMap().get(input.getName());
										for (String key : mapping.getTriggerValueMap().keySet()) {
											ControlHandler.this.evaluator
													.putVariable(key, mapping.getTriggerValueMap().get(key).toString());
										}
										try {
											String response = ControlHandler.this.evaluator.evaluate(expression);
											if (input.getType() == Long.class.getName())
												return Double.valueOf(response).longValue();
											if (input.getType() == Boolean.class.getName())
												return Double.valueOf(response) > 0;
											return response;
										} catch (EvaluationException e) {
											log.error("Unable to evaluate expression [" + expression + "]", e);
											return null;
										}
									}
								}).toList();
						Processor.class.getMethod(mapping.getProcessorMethod(), argumentClasses.toArray(new Class<?>[0])).invoke(
								this.processor,
								argumentValues.toArray());
					} else {
						Processor.class.getMethod(mapping.getProcessorMethod()).invoke(this.processor);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
						| SecurityException e) {
					log.error("Unable to trigger processor method [" + mapping.getProcessorMethod() + "]", e);
				}
			}
		}
	}
}
