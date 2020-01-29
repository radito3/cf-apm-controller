package org.tu.java.listener;

import org.tu.java.process.ExecutionListener;
import org.tu.java.process.StepContext;
import org.tu.java.service.MessageService;

public class EndProcessListener implements ExecutionListener {

    private MessageService messageService;

    public EndProcessListener(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void notify(StepContext context) {
        messageService.addMessage(context.getProcessId(), "Operation completed");
    }
}
