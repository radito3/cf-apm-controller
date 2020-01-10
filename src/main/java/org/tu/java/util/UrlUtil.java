package org.tu.java.util;

public class UrlUtil {

    private UrlUtil() {
    }

    private static final String CFAPPS_PREFIX = "cfapps.";

    public static String getAppDomain(String url) {
        String[] origElems = url.split("\\.");
        String[] urlElements = new String[origElems.length - 2];
        System.arraycopy(origElems, 2, urlElements, 0, origElems.length - 2);
        return CFAPPS_PREFIX + String.join(".", urlElements);
    }
}
