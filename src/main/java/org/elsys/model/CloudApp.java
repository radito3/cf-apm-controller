package org.elsys.model;

import org.elsys.repository.RepositoryURLBuilder;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class representing the model for Cloud applications.
 *
 * @author Rangel Ivanov
 */
public class CloudApp { //TODO add quota

    private String name;
    private String language;
    private String version;
    private String fileName;
    private String[] dependencies;

    public CloudApp(String name, String language, String version, String fileName, String[] dependencies) {
        this.name = name;
        this.language = language;
        this.version = version;
        this.fileName = fileName;
        this.dependencies = dependencies;
    }

    public String getVersion() {
        return version;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public boolean hasDependencies() {
        return dependencies.length != 0;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public URL getFileUrl() throws MalformedURLException {
        RepositoryURLBuilder urlBuilder = new RepositoryURLBuilder();
        return urlBuilder.repoRoot().target(fileName).build();
    }
}
