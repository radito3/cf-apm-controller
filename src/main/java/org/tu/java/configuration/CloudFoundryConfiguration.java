package org.tu.java.configuration;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.UaaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tu.java.util.UrlUtil;

import java.time.Duration;

@Configuration
public class CloudFoundryConfiguration {

    @Value("${API_HOST}")
    private String apiHost;
    @Value("${P_USER}")
    private String user;
    @Value("${P_PASS}")
    private String password;

    @Bean
    public CloudFoundryOperations cfOperations(@Value("${ORG}") String org, @Value("${SPACE}") String space) {
        return DefaultCloudFoundryOperations.builder()
                                            .cloudFoundryClient(cfClient())
                                            .uaaClient(uaaClient())
                                            .dopplerClient(dopplerClient())
                                            .organization(org)
                                            .space(space)
                                            .build();
    }

    private ConnectionContext connectionContext() {
        return DefaultConnectionContext.builder()
                                       .apiHost(UrlUtil.getDomain(apiHost))
                                       .connectTimeout(Duration.ofMinutes(30))
                                       .connectionPoolSize(30)
                                       .threadPoolSize(30)
                                       .skipSslValidation(true)
                                       .build();
    }

    private TokenProvider tokenProvider() {
        return PasswordGrantTokenProvider.builder()
                                         .username(user)
                                         .password(password)
                                         .build();
    }

    private CloudFoundryClient cfClient() {
        return ReactorCloudFoundryClient.builder()
                                        .connectionContext(connectionContext())
                                        .tokenProvider(tokenProvider())
                                        .build();
    }

    private UaaClient uaaClient() {
        return ReactorUaaClient.builder()
                               .connectionContext(connectionContext())
                               .tokenProvider(tokenProvider())
                               .build();
    }

    private DopplerClient dopplerClient() {
        return ReactorDopplerClient.builder()
                                   .connectionContext(connectionContext())
                                   .tokenProvider(tokenProvider())
                                   .build();
    }

}
