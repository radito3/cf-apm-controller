package org.tu.java.process.steps;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.tu.java.process.ExecutionStep;
import org.tu.java.process.StepContext;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@Named("uploadAppStep")
public class UploadAppStep implements ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;

    @Override
    public void execute(StepContext context) {
        String appName = (String) context.getVariable("appName");
        URI filePath = (URI) context.getVariable("filePath");
        Path file = Path.of(filePath);
        String processId = context.getProcessId();

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
            e.printStackTrace();
        }
    }
}
