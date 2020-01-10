package org.tu.java.service;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class UploadService {

    public Path uploadFile(HttpServletRequest request) throws FileUploadException, IOException {
        ServletFileUpload fileUpload = new ServletFileUpload();
        List<File> files = new ArrayList<>(5);
        File file = Files.createTempFile("temp", ".tmp").toFile();
        FileItemIterator itemIterator = fileUpload.getItemIterator(request);

        while (itemIterator.hasNext()) {
            FileItemStream item = itemIterator.next();
            if (item.isFormField())
                continue;

            try (InputStream is = item.openStream(); OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                while (is.read(buffer) != -1) {
                    os.write(buffer);
                }
            }
            files.add(file);
        }
        return files.get(0).toPath();
    }
}
