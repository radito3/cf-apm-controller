package org.tu.java.flowable;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.flowable.engine.delegate.DelegateExecution;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Named("uploadAppStep")
public class UploadAppStep extends ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;

    @Override
    protected ExecutionStatus executeStep(DelegateExecution execution) {
        String appName = (String) execution.getVariable("appName");
        Path filePath = (Path) execution.getVariable("filePath");
        String processId = execution.getRootProcessInstanceId();

        messageService.addMessage(processId, "Starting upload for app " + appName + "-new...");

        cfOperations.applications()
                    .push(PushApplicationRequest.builder()
                                                .name(appName + "-new")
                                                .path(filePath)
                                                .host(appName + "-idle")
                                                .buildpack("staticfile_buildpack")
                                                .memory(64)
                                                .diskQuota(512)
                                                .instances(1)
                                                .build())
                    .block();

        messageService.addMessage(processId, "App " + appName + "-new uploaded");

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            execution.setVariable("stepError", e.getMessage());
            return ExecutionStatus.ERROR;
        }

        return ExecutionStatus.SUCCESS;
    }
}
