package org.tu.java.process.steps;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RestageApplicationRequest;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.springframework.beans.factory.annotation.Value;
import org.tu.java.process.ExecutionStep;
import org.tu.java.process.StepContext;
import org.tu.java.service.MessageService;
import org.tu.java.util.UrlUtil;

import javax.inject.Inject;
import javax.inject.Named;

@Named("switchRoutesStep")
public class SwitchRoutesStep implements ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;
    @Value("${API_HOST}")
    private String apiHost;

    @Override
    public void execute(StepContext context) {
        String appName = (String) context.getVariable("appName");

        messageService.addMessage(context.getProcessId(), "Switching to live route on new application...");

        cfOperations.routes()
                    .map(MapRouteRequest.builder()
                                        .applicationName(appName + "-new")
                                        .domain(UrlUtil.getAppDomain(apiHost))
                                        .host(appName)
                                        .build())
                    .block();

        cfOperations.routes()
                    .unmap(UnmapRouteRequest.builder()
                                            .applicationName(appName + "-new")
                                            .domain(UrlUtil.getAppDomain(apiHost))
                                            .host(appName + "-idle")
                                            .build())
                    .block();

        cfOperations.applications()
                    .restage(RestageApplicationRequest.builder()
                                                      .name(appName + "-new")
                                                      .build())
                    .block();
    }
}