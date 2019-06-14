package org.elsys.descriptor;

import com.google.gson.Gson;
import org.elsys.model.CloudApp;
import org.elsys.repository.RepositoryURLBuilder;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Class representing the repository descriptor file.
 * Contains information about repository files.
 * Implements the Singleton design pattern.
 *
 * @author Rangel Ivanov
 */
public class Descriptor {

    private final Map<String, CloudApp> apps;

    Descriptor() throws IOException {
        URL url = new RepositoryURLBuilder().repoRoot().repoDescriptor().build();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        CloudApp[] appsArr = parseJson(connection);
        apps = Arrays.stream(appsArr)
                .collect(Collectors.toMap(CloudApp::getName, e -> e));
    }

    /**
     * Checks if an application exists in the repository
     *
     * @param appName The name of the application to be checked
     * @throws NoSuchElementException If the application does not exist
     */
    public void checkForApp(String appName) throws NoSuchElementException {
        if (!apps.containsKey(appName)) {
            throw new NoSuchElementException("App " + appName + " not found");
        }
    }

    /**
     * Get an application
     *
     * @param appName The application name
     * @return The {@link CloudApp} object representing the app
     * @throws NoSuchElementException If the application is not found
     */
    public CloudApp getApp(String appName) throws NoSuchElementException {
        if (!apps.containsKey(appName)) {
            throw new NoSuchElementException("Missing package " + appName);
        }
        return apps.get(appName);
    }

    public Collection<CloudApp> getApps() {
        return apps.values();
    }

    private CloudApp[] parseJson(HttpsURLConnection connection) throws IOException {
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(connection.getInputStream()))) {
            String json = br.lines()
                    .collect(Collectors.joining("\n"));
            return new Gson().fromJson(json, CloudApp[].class);
        }
    }
}
