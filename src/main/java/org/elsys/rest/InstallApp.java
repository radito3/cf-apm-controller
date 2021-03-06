package org.elsys.rest;

import org.elsys.ApplicationUploader;
import org.elsys.CloudClientFactory;
import org.elsys.descriptor.Descriptor;
import org.elsys.descriptor.DescriptorFactory;
import org.elsys.model.CloudApp;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Class for handling the REST calls for installing applications.
 *
 * @author Rangel Ivanov
 */
@Path("/{org}/{space}/install")
public class InstallApp extends AbstractRestHandler {

    /**
     * Installs an application and returns the result of the operation
     *
     * @param authType The authentication type
     * @param appName The name of the application to be installed
     * @param orgName The organisation name
     * @param spaceName The space name
     * @param memory The memory with which to install
     * @param disc The disc space with which to install
     * @param request The Json containing the authentication information
     * @return A Json containing the result of the operation
     */
    @POST
    @Path("/{appName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstallResult(@HeaderParam("auth-type") String authType,
                                     @PathParam("appName") String appName,
                                     @PathParam("org") String orgName,
                                     @PathParam("space") String spaceName,
                                     @DefaultValue("1000") @QueryParam("mem") int memory,
                                     @DefaultValue("1000") @QueryParam("disc") int disc,
                                     String request) {
        CloudClientFactory factory = new CloudClientFactory(orgName, spaceName);
        createClient(factory, request, authType);

        client.login(); //FIXME logging in and out should be done at the end points of the session, not on every call

        try {
            Descriptor descriptor = DescriptorFactory.getDescriptor();
            descriptor.checkForApp(appName);

            if (client.checkForExistingApp(appName)) {
                return Response.status(400).entity(errorMessage("App already exists")).build();
            }

            CloudApp app = descriptor.getApp(appName);

            (new ApplicationUploader(client)).install(app, memory, disc);

        } catch (NoSuchElementException  e) {
            return Response.status(404).entity(errorMessage(e.getMessage())).build();

        } catch (IllegalArgumentException e) {
            return Response.status(415).entity(errorMessage(e.getMessage())).build();

        } catch (IOException e) {
            return Response.status(500).entity(errorMessage(e.getMessage())).build();

        } finally {
            client.logout();
        }

        return Response.status(201).entity(successMessage("App installed successfully")).build();
    }
}
