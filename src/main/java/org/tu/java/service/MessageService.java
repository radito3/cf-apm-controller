package org.tu.java.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageService {

    private Map<String, Set<String>> messagesPerOperation;

    public MessageService() {
        messagesPerOperation = new ConcurrentHashMap<>();
    }

    public void addMessage(String operationId, String message) {
        messagesPerOperation.computeIfAbsent(operationId, k -> new HashSet<>())
                            .add(message);
    }

    public Set<String> getMessages(String operationId) {
        Set<String> messages = messagesPerOperation.getOrDefault(operationId, Collections.emptySet());
        if (!messages.isEmpty()) {
            messagesPerOperation.get(operationId)
                                .clear();
        }
        return messages;
    }

    public void removeOperation(String operationId) {
        messagesPerOperation.remove(operationId);
    }
}
