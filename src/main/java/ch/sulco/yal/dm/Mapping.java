package ch.sulco.yal.dm;

import java.util.LinkedList;
import java.util.Map;

public class Mapping {
	private String source;
	private Map<String, Object> triggerValueMap;
	private Map<String, String> valueExpressionMap;
	private String processorMethod;
	private LinkedList<MappingMethodArgument> processorMethodArguments;

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getProcessorMethod() {
		return this.processorMethod;
	}

	public void setProcessorMethod(String processorMethod) {
		this.processorMethod = processorMethod;
	}

	public Map<String, Object> getTriggerValueMap() {
		return this.triggerValueMap;
	}

	public void setTriggerValueMap(Map<String, Object> triggerValueMap) {
		this.triggerValueMap = triggerValueMap;
	}

	public Map<String, String> getValueExpressionMap() {
		return this.valueExpressionMap;
	}

	public void setValueExpressionMap(Map<String, String> valueExpressionMap) {
		this.valueExpressionMap = valueExpressionMap;
	}

	public LinkedList<MappingMethodArgument> getProcessorMethodArguments() {
		return this.processorMethodArguments;
	}

	public void setProcessorMethodArguments(LinkedList<MappingMethodArgument> processorMethodArguments) {
		this.processorMethodArguments = processorMethodArguments;
	}

}
