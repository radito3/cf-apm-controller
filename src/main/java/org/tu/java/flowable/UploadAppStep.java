package org.tu.java.flowable;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.flowable.engine.delegate.DelegateExecution;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.URI;
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
        URI filePath = (URI) execution.getVariable("filePath");
        Path file = Path.of(filePath);
        String processId = execution.getRootProcessInstanceId();

        messageService.addMessage(processId, "Starting upload for app " + appName + "-new...");

        cfOperations.applications()
                    .push(PushApplicationRequest.builder()
                                                .name(appName + "-new")
                                                .path(file)
                                                .host(appName + "-idle")
                                                .buildpack("staticfile_buildpack")
                                                .memory(64)
                                                .diskQuota(512)
                                                .instances(1)
                                                .build())
                    .block();

        messageService.addMessage(processId, "App " + appName + "-new uploaded");

        try {
            Files.delete(file);
        } catch (IOException e) {
            messageService.addMessage(processId, "Error: " + e.getMessage());
            execution.setVariable("stepError", e.getMessage());
            return ExecutionStatus.ERROR;
        }

        messageService.addMessage(processId, "Enter validation phase");

        return ExecutionStatus.SUCCESS;
    }
}
