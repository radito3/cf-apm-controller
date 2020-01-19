package org.tu.java.service;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UploadService {

    private AtomicInteger counter = new AtomicInteger(0);

    public Path uploadFile(HttpServletRequest request) throws FileUploadException, IOException {
        ServletFileUpload fileUpload = new ServletFileUpload();
        List<Path> files = new ArrayList<>(5);
        Path file = Files.createTempFile("temp-" + counter.getAndIncrement(), ".tmp");
        FileItemIterator itemIterator = fileUpload.getItemIterator(request);

        while (itemIterator.hasNext()) {
            FileItemStream item = itemIterator.next();
            if (item.isFormField())
                continue;

            try (InputStream is = item.openStream(); OutputStream os = Files.newOutputStream(file)) {
                byte[] buffer = new byte[4096];
                while (is.read(buffer) != -1) {
                    os.write(buffer);
                }
            }
            files.add(file);
        }
        return files.get(0);
    }
}
