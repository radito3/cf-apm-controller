package org.tu.java.flowable;

import org.flowable.engine.delegate.DelegateExecution;

import javax.inject.Named;

@Named("detectActionsToExecuteStep")
public class DetectActionsToExecuteStep extends ExecutionStep {


    @Override
    protected ExecutionStatus executeStep(DelegateExecution execution) {

        return ExecutionStatus.SUCCESS;
    }
}
