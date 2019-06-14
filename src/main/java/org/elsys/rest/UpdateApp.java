package org.elsys.rest;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.elsys.ApplicationUploader;
import org.elsys.CloudClientFactory;
import org.elsys.descriptor.Descriptor;
import org.elsys.descriptor.DescriptorFactory;
import org.elsys.model.CloudApp;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

/**
 * Class for handling the REST calls for updating applications.
 *
 * @author Rangel Ivanov
 */
@Path("/{org}/{space}/update")
public class UpdateApp extends AbstractRestHandler {

    /**
     * Updates an application and returns the result of the operation
     *
     * @param authType The authentication type
     * @param appName The name of the application to be updated
     * @param orgName The organisation name
     * @param spaceName The space name
     * @param request The Json containing the authentication information
     * @return A Json containing the result of the operation
     */
    @PUT
    @Path("/{appName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUpdateResult(@HeaderParam("auth-type") String authType,
                                    @PathParam("appName") String appName,
                                    @PathParam("org") String orgName,
                                    @PathParam("space") String spaceName,
                                    String request) {
        CloudClientFactory factory = new CloudClientFactory(orgName, spaceName);
        createClient(factory, request, authType);

        client.login();

        try {
            CloudApplication cloudApp = client.getApp(appName);
            Descriptor descriptor = DescriptorFactory.getDescriptor();
            descriptor.checkForApp(appName);

            CloudApp app = descriptor.getApp(appName);

            ApplicationUploader uploader = new ApplicationUploader(client);
            uploader.checkDependencies(app);

            if (compareVersions(app.getVersion(), cloudApp.getEnvAsMap().get("pkgVersion")) > 0) {
                uploader.upload((HttpsURLConnection) app.getFileUrl().openConnection(), app);
            } else {
                return Response.status(200).entity(successMessage("App up-to-date")).build();
            }
        } catch (CloudFoundryException e) {
            return Response.status(404).entity(errorMessage("App " + appName + " not found")).build();

        } catch (NoSuchElementException e) {
            return Response.status(410).entity(errorMessage("App " + appName + " no longer supported")).build();

        } catch (MissingResourceException e) {
            return Response.status(424).entity(errorMessage(e.getMessage())).build();

        } catch (IOException e) {
            return Response.status(500).entity(errorMessage(e.getMessage())).build();

        } finally {
            client.logout();
        }

        return Response.status(202).entity(successMessage("App updated")).build();
    }

    private static int compareVersions(String ver1, String ver2) {
        Integer[] repoVersion = Arrays.stream(ver1.split("\\."))
                .map(Integer::valueOf)
                .toArray(Integer[]::new);
        Integer[] currentVersion = Arrays.stream(ver2.split("\\."))
                .map(Integer::valueOf)
                .toArray(Integer[]::new);

        for (int i = 0; i < Math.max(repoVersion.length, currentVersion.length); i++) {
            int repoVerPart = i < repoVersion.length ? repoVersion[i] : 0;
            int currentVerPart = i < currentVersion.length ? currentVersion[i] : 0;

            int result = Integer.compare(repoVerPart, currentVerPart);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
