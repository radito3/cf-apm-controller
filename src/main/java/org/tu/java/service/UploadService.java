package org.tu.java.service;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class UploadService {

    private Supplier<String> nameGenerator = () -> UUID.randomUUID().toString();

    public Path uploadFile(HttpServletRequest request) throws FileUploadException, IOException {
        ServletFileUpload fileUpload = new ServletFileUpload();
        Path file = Files.createTempFile("temp-" + nameGenerator.get(), ".tmp");
        FileItemIterator itemIterator = fileUpload.getItemIterator(request);

        FileItemStream item = getFileItem(itemIterator);

        try (InputStream is = item.openStream(); OutputStream os = Files.newOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        }
        return file;
    }

    private FileItemStream getFileItem(FileItemIterator itemIterator) throws IOException, FileUploadException {
        FileItemStream item;
        do {
            if (!itemIterator.hasNext()) {
                throw new IllegalArgumentException("Request doesn't contain proper field with file");
            }
            item = itemIterator.next();
        } while (item.isFormField() || !item.getFieldName().equals("file_field"));
        return item;
    }
}
