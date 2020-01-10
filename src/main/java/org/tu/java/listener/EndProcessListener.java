package org.tu.java.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.tu.java.service.MessageService;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class EndProcessListener implements ExecutionListener {

    @Inject
    private MessageService messageService;

    @Override
    public void notify(DelegateExecution execution) {
        messageService.removeOperation(execution.getRootProcessInstanceId());
    }
}
