package org.elsys.model;

import org.elsys.ApplicationUploader;
import org.elsys.CloudClient;
import org.elsys.CloudClientFactory;
import org.elsys.CloudClientImpl;
import org.elsys.descriptor.Descriptor;
import org.elsys.repository.RepositoryURLBuilder;
import org.elsys.rest.DeleteApp;
import org.elsys.rest.InstallApp;
import org.elsys.rest.ListApps;
import org.elsys.rest.UpdateApp;

import javax.ws.rs.ApplicationPath;
import java.util.Set;

/**
 * Sets the base path for the Application.
 * Configures the classes used in the Application.
 *
 * @author Rangel Ivanov
 */
@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(ListApps.class, CloudClientImpl.class, Buildpacks.class,
                InstallApp.class, DeleteApp.class, UpdateApp.class, Descriptor.class,
                ApplicationUploader.class, CloudClientFactory.class,
                CloudApp.class, RepositoryURLBuilder.class, CloudClient.class);
    }
}
