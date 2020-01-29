package org.tu.java.process.steps;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationSummary;
import org.tu.java.process.ExecutionStep;
import org.tu.java.process.StepContext;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Named("detectActionsToExecuteStep")
public class DetectActionsToExecuteStep implements ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;

    @Override
    public void execute(StepContext context) {
        String appName = (String) context.getVariable("appName");

        messageService.addMessage(context.getProcessId(), "Retrieving apps...");

        List<String> apps = cfOperations.applications()
                                        .list()
                                        .map(ApplicationSummary::getName)
                                        .filter(app -> app.startsWith(appName))
                                        .collectList()
                                        .block(Duration.ofSeconds(10));

        if (apps == null || apps.isEmpty()) {
            messageService.addMessage(context.getProcessId(), "No previous app found. This is initial deployment");

            context.setVariable("appsToRename", Collections.emptyList());
            context.setVariable("appsToDelete", Collections.emptyList());

            return;
        }

        messageService.addMessage(context.getProcessId(), "Determining actions to execute...");

        List<String> appsToRename = new LinkedList<>();
        List<String> appsToDelete = new LinkedList<>();

        for (String app : apps) {
            if (app.endsWith("-new")) {
                continue;
            }
            if (!app.endsWith("-old")) {
                appsToRename.add(app);
            } else if (apps.contains(removeAppNameSuffix(app))) {
                appsToDelete.add(app);
            }
        }

        context.setVariable("appsToRename", appsToRename);
        context.setVariable("appsToDelete", appsToDelete);
    }

    private static String removeAppNameSuffix(String appName) {
        if (appName.endsWith("-old") || appName.endsWith("-new")) {
            return appName.substring(0, appName.lastIndexOf('-'));
        }
        return appName;
    }
}
