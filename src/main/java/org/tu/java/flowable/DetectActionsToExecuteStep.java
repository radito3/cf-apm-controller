package org.tu.java.flowable;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationSummary;
import org.flowable.engine.delegate.DelegateExecution;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Named("detectActionsToExecuteStep")
public class DetectActionsToExecuteStep extends ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;

    @Override
    protected ExecutionStatus executeStep(DelegateExecution execution) {
        String appName = (String) execution.getVariable("appName");
        List<String> apps = cfOperations.applications()
                                        .list()
                                        .map(ApplicationSummary::getName)
                                        .filter(app -> app.startsWith(appName))
                                        .collectList()
                                        .block();
        if (apps == null || apps.isEmpty()) {
            messageService.addMessage(execution.getRootProcessInstanceId(), "No previous app found. This is initial deployment");

            execution.setVariable("appsToRename", Collections.emptyList());
            execution.setVariable("appsToDelete", Collections.emptyList());
            execution.setVariable("autoContinue", Boolean.TRUE);

            return ExecutionStatus.SUCCESS;
        }

        messageService.addMessage(execution.getRootProcessInstanceId(), "Determining actions to execute...");

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

        execution.setVariable("appsToRename", appsToRename);
        execution.setVariable("appsToDelete", appsToDelete);

        return ExecutionStatus.SUCCESS;
    }

    private static String removeAppNameSuffix(String appName) {
        if (appName.endsWith("-old") || appName.endsWith("-new")) {
            return appName.substring(0, appName.lastIndexOf('-'));
        }
        return appName;
    }
}
