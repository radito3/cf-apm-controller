package org.tu.java.rest;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tu.java.service.MessageService;
import org.tu.java.service.ProcessService;
import org.tu.java.service.UploadService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/")
public class UploadController {

    @Inject
    private UploadService uploadService;
    @Inject
    private ProcessService processService;
    @Inject
    private MessageService messageService;

    @GetMapping(path = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("hello");
    }

    @GetMapping(path = "/messages/{operationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getMessages(@PathVariable String operationId) {
        List<String> result = messageService.getMessages(operationId);

        if (result.contains("Operation completed")) {
            if (result.size() == 1) {
                messageService.removeOperation(operationId);
                return ResponseEntity.status(HttpStatus.CREATED)
                                     .body(Collections.emptyList());
            } else {
                messageService.addMessage(operationId, "Operation completed");
                result.remove("Operation completed");
                return ResponseEntity.ok()
                                     .body(result);
            }
        }

        return ResponseEntity.ok()
                             .body(result);
    }

    @PostMapping(path = "/upload/{appName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadApp(HttpServletRequest request, @PathVariable String appName) {
        Path filePath;
        try {
            filePath = uploadService.uploadFile(request);
        } catch (FileUploadException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .build();
        }
        String processId = processService.startProcess(appName, filePath.toUri());

        return ResponseEntity.ok()
                             .header("Location", processId)
                             .build();
    }
}
