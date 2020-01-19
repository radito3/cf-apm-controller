package org.tu.java.flowable;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.DeleteApplicationRequest;
import org.flowable.engine.delegate.DelegateExecution;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named("deleteAppsStep")
public class DeleteAppsStep extends ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;

    @Override
    protected ExecutionStatus executeStep(DelegateExecution execution) {
        List<String> appsToDelete = (List<String>) execution.getVariable("appsToDelete");

        for (String appName : appsToDelete) {
            messageService.addMessage(execution.getRootProcessInstanceId(), "Deleting application " + appName + "...");

            cfOperations.applications()
                        .delete(DeleteApplicationRequest.builder()
                                                        .name(appName)
                                                        .build())
                        .block();
        }

        return ExecutionStatus.SUCCESS;
    }
}
