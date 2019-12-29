package org.tu.java.service;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.DeleteApplicationRequest;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.RenameApplicationRequest;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tu.java.util.UrlUtil;

import javax.inject.Inject;
import java.nio.file.Path;
import java.time.Duration;

@Service
public class UploadService {

    @Inject
    private CloudFoundryOperations cfOperations;
    @Value("${API_HOST}")
    private String apiHost;

    public void uploadFile(String appName, Path filePath) {
        cfOperations.applications()
                    .push(PushApplicationRequest.builder()
                                                .name(appName)
                                                .path(filePath)
                                                .host(appName + "-idle")
                                                .buildpack("staticfile_buildpack")
                                                .memory(64)
                                                .diskQuota(100)
                                                .instances(1)
                                                .build())
                    .block();

        cfOperations.routes()
                    .map(MapRouteRequest.builder()
                                        .applicationName("app-new")
                                        .domain(UrlUtil.removeSchema(apiHost))
                                        .host("app")
                                        .build())
                    .block();

        cfOperations.routes()
                    .unmap(UnmapRouteRequest.builder()
                                            .applicationName("app-old")
                                            .domain(UrlUtil.removeSchema(apiHost))
                                            .host("app-idle")
                                            .build())
                    .block();

        cfOperations.applications()
                    .rename(RenameApplicationRequest.builder()
                                                    .name("app-new")
                                                    .newName("app")
                                                    .build())
                    .block();

        cfOperations.applications()
                    .delete(DeleteApplicationRequest.builder()
                                                    .name("app-old")
                                                    .deleteRoutes(true)
                                                    .completionTimeout(Duration.ofMinutes(5))
                                                    .build())
                    .block();
    }
}
