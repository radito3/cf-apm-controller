package org.tu.java.flowable;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RenameApplicationRequest;
import org.flowable.engine.delegate.DelegateExecution;

import javax.inject.Inject;
import javax.inject.Named;

@Named("removeAppSuffixStep")
public class RemoveAppSuffixStep extends ExecutionStep {

    @Inject
    private CloudFoundryOperations cfOperations;

    @Override
    protected ExecutionStatus executeStep(DelegateExecution execution) {
        String appName = (String) execution.getVariable("appName");

        cfOperations.applications()
                    .rename(RenameApplicationRequest.builder()
                                                    .name(appName + "-new")
                                                    .newName(appName)
                                                    .build())
                    .block();

        return ExecutionStatus.SUCCESS;
    }
}
