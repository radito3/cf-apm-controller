package org.elsys;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.Staging;
import org.elsys.descriptor.Descriptor;
import org.elsys.descriptor.DescriptorFactory;
import org.elsys.model.Buildpacks;
import org.elsys.model.CloudApp;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

/**
 * Class for uploading applications and checking dependencies.
 *
 * @author Rangel Ivanov
 */
public class ApplicationUploader {

    @Deprecated(forRemoval = true)
    private static final int DEFAULT_DISC = 1000;
    @Deprecated(forRemoval = true)
    private static final int DEFAULT_MEMORY = 1000;

    private CloudClient client;

    public ApplicationUploader(CloudClient client) {
        this.client = client;
    }

    /**
     * Install a given application
     * Recursively installs its dependencies starting from the lowest level
     *
     * @param app The CloudApp object representing the application to be installed
     * @param memory The amount of operating memory with which to be installed
     * @param disc The amount of disc space with which to be installed
     * @throws IOException If there is an error with building the descriptor json
     * @throws NoSuchElementException If the application does not exist
     */
    public void install(CloudApp app, int memory, int disc)
            throws IOException, NoSuchElementException {

        create(app, memory, disc);
        upload((HttpsURLConnection) app.getFileUrl().openConnection(), app);

        if (!app.hasDependencies()) {
            return;
        }

        //TODO install dependencies asynchronously
        for (String dependency : app.getDependencies()) {
            if (client.checkForExistingApp(dependency)) {
                continue;
            }

            Descriptor descriptor = DescriptorFactory.getDescriptor();
            CloudApp dependency1 = descriptor.getApp(dependency);

            install(dependency1, DEFAULT_MEMORY, DEFAULT_DISC); //TODO use the dependency's memory & disc
        }
    }

    /**
     * Check if all dependencies of a given app are present
     *
     * @param app A CloudApp object representing the application
     * @throws MissingResourceException If there are missing dependencies
     */
    public void checkDependencies(CloudApp app) {
        for (String dependency : app.getDependencies()) {
            try {
                client.getApp(dependency);
            } catch (CloudFoundryException e) {
                throw new MissingResourceException("Missing dependencies", app.getName(), dependency);
            }
        }
    }

    private void create(CloudApp app, int memory, int disc) {
        String buildpackUrl = Buildpacks.getBuildpackUrl(app.getLanguage());

        client.createApp(app.getName(), new Staging(null, buildpackUrl), disc, memory,
                List.of("https://cf-" + app.getName().toLowerCase() + ".cfapps.io"),
                List.of());
    }

    public void upload(HttpsURLConnection con, CloudApp app) throws IOException {
        try (InputStream in = con.getInputStream()) {
            client.uploadApp(app.getName(), app.getFileName(), in);
            client.updateAppEnv(app.getName(), Map.of("pkgVersion", app.getVersion()));
        }
    }
}
