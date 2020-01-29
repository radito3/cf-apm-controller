package org.tu.java.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.tu.java.process.ProcessEngine;

import javax.inject.Inject;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ProcessService {

    @Inject
    private ProcessEngine processEngine;

    public String startProcess(String appName, URI filePath) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("appName", appName);
        variables.put("filePath", filePath);

        return processEngine.startProcess(variables);
    }
}
