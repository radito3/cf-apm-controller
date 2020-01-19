package org.tu.java.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public abstract class ExecutionStep implements JavaDelegate {

    /**
     *
     * x detect actions needed
     * x delete apps
     * x rename apps
     * x upload app-new with -idle route (and delete the temp file)
     *
     * x switch to live route
     * x remove suffix from new app
     * x delete old app
     *
     */

    @Override
    public void execute(DelegateExecution execution) {
        try {
            ExecutionStatus status = executeStep(execution);
            if (status == ExecutionStatus.ERROR) {
                System.err.println((String) execution.getVariable("stepError"));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    protected abstract ExecutionStatus executeStep(DelegateExecution execution);
}
