package org.elsys.descriptor;

import java.io.IOException;

public class DescriptorFactory {
    private static Descriptor descriptor;

    public static Descriptor getDescriptor() throws IOException {
        if (descriptor == null) {
            descriptor = new Descriptor();
        }
        return descriptor;
    }
}
