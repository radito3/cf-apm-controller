package org.tu.java.rest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tu.java.service.FlowableService;
import org.tu.java.service.MessageService;
import org.tu.java.service.UploadService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/v1/")
public class UploadController {

    @Inject
    private UploadService uploadService;
    @Inject
    private FlowableService flowableService;
    @Inject
    private MessageService messageService;

    @GetMapping(path = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> helloWorld() {
        //for testing purposes
        return ResponseEntity.ok("hello");
    }

    @GetMapping(path = "/messages/{operationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<String>> getMessages(@PathVariable String operationId) {
        Set<String> result = messageService.getMessages(operationId);

        //check if operation is complete
        // -> return 201
        //else return list of processing messages

        return ResponseEntity.status(HttpStatus.PROCESSING)
                             .body(result);
    }

    @PutMapping(path = "/resume/{operationId}")
    public ResponseEntity<Void> continueUpload(@PathVariable String operationId) {
        flowableService.resumeProcess(operationId);

        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping(path = "/upload/{appName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadApp(HttpServletRequest request, @PathVariable String appName) {
        String processId = flowableService.startProcess(appName);

        ServletFileUpload fileUpload = new ServletFileUpload();
        FileItemIterator itemIterator;
        File file = null;

        try {
            file = Files.createTempFile("temp", ".tmp").toFile();
            itemIterator = fileUpload.getItemIterator(request);

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
            }
        } catch (FileUploadException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .build();
        } finally {
            if (file != null) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    System.err.println("failed to delete temp file");
                }
            }
        }

        uploadService.uploadFile(appName, file.toPath());

        return ResponseEntity.ok()
                             .header("Location", processId)
                             .build();
    }
}
