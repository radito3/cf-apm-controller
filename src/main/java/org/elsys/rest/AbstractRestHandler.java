package org.elsys.rest;

import com.google.gson.Gson;
import org.elsys.CloudClient;
import org.elsys.CloudClientFactory;

/**
 * Superclass to all REST handlers.
 * Contains the Json template for the responses.
 *
 * @author Rangel Ivanov
 */
abstract class AbstractRestHandler {

    //TODO create a client provider, so that the client is only created once
    protected CloudClient client;

    final String template = "{\"error\":\"%s\",\"result\":\"%s\",\"apps\":%s}";

    /**
     * Creates the {@link CloudClient} object that the REST handlers use
     *
     * @param factory A {@link CloudClientFactory} object from which the Client is constructed
     * @param request The Json request
     * @param authType The authentication type
     */
    void createClient(CloudClientFactory factory, String request, String authType) {
        HttpRequest httpRequest = new Gson().fromJson(request, HttpRequest.class);
        if (authType.equals("token")) {
            client = factory.newCloudClient(httpRequest.token);
        } else {
            client = factory.newCloudClient(httpRequest.user, httpRequest.pass);
        }
    }

    /**
     * Builds the Json for error messages
     *
     * @param message The error message
     * @return The Json
     */
    String errorMessage(String message) {
        return String.format(template, message, "", "[]");
    }

    /**
     * Builds the Json for success messages
     *
     * @param message The success message
     * @return The Json
     */
    String successMessage(String message) {
        return String.format(template, "", message, "[]");
    }

    private static class HttpRequest {
        String token;
        String user;
        String pass;
    }
}
