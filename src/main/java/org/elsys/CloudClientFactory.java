package org.elsys;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

/**
 * A Factory from which the CloudClient objects are constructed
 *
 * @author Rangel Ivanov
 */
public class CloudClientFactory {

    private String org;
    private String space;

    public CloudClientFactory(String org, String space) {
        this.org = org;
        this.space = space;
    }

    /**
     * Build a CloudClient with an OAuth2 token
     *
     * @param token The token
     * @return A new CloudClient instance
     */
    public CloudClient newCloudClient(String token) {
        CloudCredentials credentials = new CloudCredentials(new DefaultOAuth2AccessToken(token), false);
        return new CloudClientImpl(org, space, credentials);
    }

    /**
     * Build a CloudClient with a Username and Password
     *
     * @param user The username
     * @param pass The password
     * @return A new CloudClient instance
     */
    public CloudClient newCloudClient(String user, String pass) {
        CloudCredentials credentials = new CloudCredentials(user, pass);
        return new CloudClientImpl(org, space, credentials);
    }
}
