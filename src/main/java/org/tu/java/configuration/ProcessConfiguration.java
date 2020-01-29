package org.tu.java.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tu.java.listener.EndProcessListener;
import org.tu.java.process.ExecutionStep;
import org.tu.java.process.ProcessEngine;
import org.tu.java.process.steps.*;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.Queue;

@Configuration
public class ProcessConfiguration {
    //configure the async executor
    //set the id generator

    @Inject
    private MessageService messageService;
    @Inject
    private ApplicationContext context;

    @Bean
    public ProcessEngine processEngine() {
        ProcessEngine processEngine = new ProcessEngine();
        processEngine.addListener(new EndProcessListener(messageService));
        processEngine.setSteps(getSteps());
        return processEngine;
    }

    private Queue<ExecutionStep> getSteps() {
        Queue<ExecutionStep> steps = new LinkedList<>();
        steps.add(context.getBean(DetectActionsToExecuteStep.class));
        steps.add(context.getBean(DeleteAppsStep.class));
        steps.add(context.getBean(RenameAppsStep.class));
        steps.add(context.getBean(UploadAppStep.class));
        steps.add(context.getBean(SwitchRoutesStep.class));
        steps.add(context.getBean(RemoveAppSuffixStep.class));
        steps.add(context.getBean(DeleteAppsStep.class));
        return steps;
    }

}
