package org.tu.java.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public abstract class ExecutionStep implements JavaDelegate {

    /**
     *
     * - get apps starting with the desired app name
     * - detect actions needed
     * - delete apps if needed
     * - upload app-new with -idle route
     *
     * - switch to live route
     * - remove suffix from new app
     * - delete old app
     *
     */

    @Override
    public void execute(DelegateExecution execution) {
        ExecutionStatus status = executeStep(execution);

    }

    protected abstract ExecutionStatus executeStep(DelegateExecution execution);
}
