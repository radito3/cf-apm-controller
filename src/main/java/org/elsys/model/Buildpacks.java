package org.elsys.model;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An Enum with the available language buildpack urls.
 *
 * @author Rangel Ivanov
 */
public enum Buildpacks {

    JAVA("https://github.com/cloudfoundry/java-buildpack.git"),

    RUBY("https://github.com/cloudfoundry/ruby-buildpack.git"),

    PYTHON("https://github.com/cloudfoundry/python-buildpack.git"),

    NODEJS("https://github.com/cloudfoundry/nodejs-buildpack.git"),

    GO("https://github.com/cloudfoundry/go-buildpack.git"),

    /**
     * Hosted Web Core applications
     * Windows applications
     */
    HWC("https://github.com/cloudfoundry/hwc-buildpack.git"),

    /**
     * .NET Core applications
     */
    DOTNET("https://github.com/cloudfoundry/dotnet-core-buildpack.git"),

    PHP("https://github.com/cloudfoundry/php-buildpack.git"),
    
    BINARY("https://github.com/cloudfoundry/binary-buildpack.git"),

    STATICFILE("https://github.com/cloudfoundry/staticfile-buildpack.git");

    private final String url;

    Buildpacks(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    private static final Map<String, String> values = Stream.of(values())
        .collect(Collectors.toMap(e -> e.toString().toLowerCase(), Buildpacks::getUrl));

    /**
     * Get the buildpack url
     *
     * @param appLang The application language
     * @return The url
     */
    static public String getBuildpackUrl(String appLang) {
        if (!values.containsKey(appLang)) {
            throw new IllegalArgumentException("Unsupported language");
        }
        return values.get(appLang);
    }
}
