package org.tu.java.flowable;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RenameApplicationRequest;
import org.flowable.engine.delegate.DelegateExecution;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named("renameAppsStep")
public class RenameAppsStep extends ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;

    @Override
    protected ExecutionStatus executeStep(DelegateExecution execution) {
        List<String> appsToRename = (List<String>) execution.getVariable("appsToRename");

        for (String appName : appsToRename) {
            messageService.addMessage(execution.getRootProcessInstanceId(), "Renaming app " + appName + " to " + appName + "-old...");

            cfOperations.applications()
                        .rename(RenameApplicationRequest.builder()
                                                        .name(appName)
                                                        .newName(appName + "-old")
                                                        .build())
                        .block();
        }

        return ExecutionStatus.SUCCESS;
    }
}
