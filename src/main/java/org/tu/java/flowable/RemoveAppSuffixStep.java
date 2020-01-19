package org.tu.java.flowable;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RenameApplicationRequest;
import org.flowable.engine.delegate.DelegateExecution;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;

@Named("removeAppSuffixStep")
public class RemoveAppSuffixStep extends ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;

    @Override
    protected ExecutionStatus executeStep(DelegateExecution execution) {
        String appName = (String) execution.getVariable("appName");

        messageService.addMessage(execution.getRootProcessInstanceId(), "Renaming new application...");

        cfOperations.applications()
                    .rename(RenameApplicationRequest.builder()
                                                    .name(appName + "-new")
                                                    .newName(appName)
                                                    .build())
                    .block();

        execution.setVariable("appsToDelete", Collections.singletonList(appName + "-old"));

        return ExecutionStatus.SUCCESS;
    }
}
