package org.tu.java.service;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlowableService {

    private final ProcessEngine processEngine;

    @Inject
    public FlowableService(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public String startProcess(String appName, Path filePath) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("appName", appName);
        variables.put("filePath", filePath);

        return processEngine.getRuntimeService()
                            .startProcessInstanceByKey("bg-upload", variables)
                            .getProcessInstanceId();
    }

    public void resumeProcess(String processId) {
        List<Execution> allExecutions =  processEngine.getRuntimeService()
                     .createExecutionQuery()
                     .rootProcessInstanceId(processId)
                     .list();

        List<Execution> executionsAtReceiveTask = allExecutions.stream()
            .filter(e -> e.getActivityId() != null)
            .filter(e -> !findCurrentActivitiesAtReceiveTask(e).isEmpty())
            .collect(Collectors.toList());

        for (Execution execution : executionsAtReceiveTask) {
            processEngine.getRuntimeService()
                         .trigger(execution.getId());
        }
    }

    private List<HistoricActivityInstance> findCurrentActivitiesAtReceiveTask(Execution execution) {
        return processEngine.getHistoryService()
                            .createHistoricActivityInstanceQuery()
                            .activityId(execution.getActivityId())
                            .executionId(execution.getId())
                            .activityType("receiveTask")
                            .list();
    }
}
