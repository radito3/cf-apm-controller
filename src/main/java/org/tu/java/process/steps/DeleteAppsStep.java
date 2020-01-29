package org.tu.java.process.steps;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.DeleteApplicationRequest;
import org.tu.java.process.ExecutionStep;
import org.tu.java.process.StepContext;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named("deleteAppsStep")
public class DeleteAppsStep implements ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;

    @Override
    public void execute(StepContext context) {
        List<String> appsToDelete = (List<String>) context.getVariable("appsToDelete");

        for (String appName : appsToDelete) {
            messageService.addMessage(context.getProcessId(), "Deleting application " + appName + "...");

            cfOperations.applications()
                        .delete(DeleteApplicationRequest.builder()
                                                        .name(appName)
                                                        .build())
                        .block();
        }
    }
}
