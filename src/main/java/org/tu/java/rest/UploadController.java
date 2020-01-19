package org.tu.java.rest;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tu.java.service.FlowableService;
import org.tu.java.service.MessageService;
import org.tu.java.service.UploadService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
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

        if (result.contains("Enter validation phase")) {
            messageService.clearMessagesForOperation(operationId);
            messageService.addMessage(operationId, "Enter validation phase");
            result.remove("Enter validation phase");
            if (!result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.PROCESSING)
                                     .body(result);
            } else {
                return ResponseEntity.status(HttpStatus.MULTIPLE_CHOICES)
                                     .body(result);
            }
        }

        if (result.contains("Operation completed")) {
            if (result.size() == 1) {
                messageService.removeOperation(operationId);
                return ResponseEntity.status(HttpStatus.CREATED)
                                     .body(Collections.emptySet());
            } else {
                messageService.clearMessagesForOperation(operationId);
                messageService.addMessage(operationId, "Operation completed");
                result.remove("Operation completed");
                return ResponseEntity.status(HttpStatus.PROCESSING)
                                     .body(result);
            }
        }

        messageService.clearMessagesForOperation(operationId);

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
        Path filePath;
        try {
            filePath = uploadService.uploadFile(request);
        } catch (FileUploadException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .build();
        }
        String processId = flowableService.startProcess(appName, filePath.toUri());

        return ResponseEntity.ok()
                             .header("Location", processId)
                             .build();
    }
}
