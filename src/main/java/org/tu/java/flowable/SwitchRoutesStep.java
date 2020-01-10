package org.tu.java.flowable;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RestageApplicationRequest;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Value;
import org.tu.java.service.MessageService;
import org.tu.java.util.UrlUtil;

import javax.inject.Inject;
import javax.inject.Named;

@Named("switchRoutesStep")
public class SwitchRoutesStep extends ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Inject
    private MessageService messageService;
    @Value("${API_HOST}")
    private String apiHost;

    @Override
    protected ExecutionStatus executeStep(DelegateExecution execution) {
        String appName = (String) execution.getVariable("appName");

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

        return ExecutionStatus.SUCCESS;
    }
}
