package org.tu.java.process.steps;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RenameApplicationRequest;
import org.tu.java.process.ExecutionStep;
import org.tu.java.process.StepContext;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named("renameAppsStep")
public class RenameAppsStep implements ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;

    @Override
    public void execute(StepContext context) {
        List<String> appsToRename = (List<String>) context.getVariable("appsToRename");

        for (String appName : appsToRename) {
            messageService.addMessage(context.getProcessId(), "Renaming app " + appName + " to " + appName + "-old...");

            cfOperations.applications()
                        .rename(RenameApplicationRequest.builder()
                                                        .name(appName)
                                                        .newName(appName + "-old")
                                                        .build())
                        .block();
        }
    }
}
