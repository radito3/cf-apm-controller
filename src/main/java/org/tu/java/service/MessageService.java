package org.tu.java.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class MessageService {

    private Map<String, List<String>> messagesPerOperation = new ConcurrentHashMap<>();

    public void addMessage(String operationId, String message) {
        messagesPerOperation.computeIfAbsent(operationId, k -> new ArrayList<>())
                            .add(message);
    }

    public List<String> getMessages(String operationId) {
        List<String> msgs = messagesPerOperation.get(operationId);
        if (msgs == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>(msgs);
        msgs.clear();
        return result;
    }

    public void removeOperation(String operationId) {
        messagesPerOperation.remove(operationId);
    }

}
