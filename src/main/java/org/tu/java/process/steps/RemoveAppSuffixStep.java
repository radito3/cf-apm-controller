package org.tu.java.process.steps;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RenameApplicationRequest;
import org.tu.java.process.ExecutionStep;
import org.tu.java.process.StepContext;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;

@Named("removeAppSuffixStep")
public class RemoveAppSuffixStep implements ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;

    @Override
    public void execute(StepContext context) {
        String appName = (String) context.getVariable("appName");

        messageService.addMessage(context.getProcessId(), "Renaming new application...");

        cfOperations.applications()
                    .rename(RenameApplicationRequest.builder()
                                                    .name(appName + "-new")
                                                    .newName(appName)
                                                    .build())
                    .block();

        context.setVariable("appsToDelete", Collections.singletonList(appName + "-old"));
    }
}
