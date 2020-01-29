package org.tu.java.process;

import java.util.Map;

public class StepContext {
    private final Map<String, Object> cache;
    private final String processId;

    public StepContext(Map<String, Object> cache, String processId) {
        this.cache = cache;
        this.processId = processId;
    }

    public void setVariable(String key, Object value) {
        cache.put(key, value);
    }

    public Object getVariable(String key) {
        return cache.get(key);
    }

    public String getProcessId() {
        return processId;
    }
}
