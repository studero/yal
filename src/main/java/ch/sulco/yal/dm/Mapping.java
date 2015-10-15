package ch.sulco.yal.dm;

import java.util.Map;

public class Mapping {
	private String source;
	private Map<String, Object> triggerValueMap;
	private Map<String, String> valueMap;
	private String processorMethod;

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Map<String, String> getValueMap() {
		return this.valueMap;
	}

	public void setValueMap(Map<String, String> valueMap) {
		this.valueMap = valueMap;
	}

	public String getProcessorMethod() {
		return this.processorMethod;
	}

	public void setProcessorMethod(String processorMethod) {
		this.processorMethod = processorMethod;
	}

	public Map<String, Object> getTriggerValueMap() {
		return triggerValueMap;
	}

	public void setTriggerValueMap(Map<String, Object> triggerValueMap) {
		this.triggerValueMap = triggerValueMap;
	}
}
