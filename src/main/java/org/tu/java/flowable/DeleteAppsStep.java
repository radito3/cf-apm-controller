package org.tu.java.flowable;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.flowable.engine.delegate.DelegateExecution;

import javax.inject.Inject;
import javax.inject.Named;

@Named("deleteAppsStep")
public class DeleteAppsStep extends ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;

    @Override
    protected ExecutionStatus executeStep(DelegateExecution execution) {

        return ExecutionStatus.SUCCESS;
    }
}
