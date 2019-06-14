package org.elsys.rest;

import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.elsys.CloudClientFactory;
import org.elsys.descriptor.Descriptor;
import org.elsys.descriptor.DescriptorFactory;
import org.elsys.model.CloudApp;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Class for handling the REST calls for listing applications.
 *
 * @author Rangel Ivanov
 */
@Path("/{org}/{space}/list_apps")
public class ListApps extends AbstractRestHandler {

    /**
     * Get the repository applications
     *
     * @return A Json containing the applications names
     */
    @GET
    @Path("/repo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRepoApps() {
        Descriptor descriptor;
        try {
            descriptor = DescriptorFactory.getDescriptor();
        } catch (IOException e) {
            return Response.status(500).entity(errorMessage(e.getMessage())).build();
        }

        String output = descriptor.getApps().stream()
                .map(CloudApp::getName)
                .collect(Collectors.joining(",", "[", "]"));
        return Response.status(200).entity(String.format(template, "", "", output)).build();
    }

    /**
     * Get the currently installed applications
     *
     * @param authType The authentication type
     * @param org The organisation name
     * @param space The space name
     * @param request The Json containing the authentication information
     * @return A Json containing the applications names
     */
    @GET
    @Path("/installed")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstalledApps(@HeaderParam("auth-type") String authType,
                                     @PathParam("org") String org,
                                     @PathParam("space") String space,
                                     String request) {
        CloudClientFactory factory = new CloudClientFactory(org, space);
        createClient(factory, request, authType);

        String output = client.getApps().stream()
                .map(CloudEntity::getName)
                .collect(Collectors.joining(",", "[", "]"));
        return Response.status(200).entity(String.format(template, "", "", output)).build();
    }
}
