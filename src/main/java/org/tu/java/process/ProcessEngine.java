package org.tu.java.process;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class ProcessEngine {

    private ExecutorService asyncExecutor;
    private Queue<ExecutionStep> steps;
    private Set<ExecutionListener> listeners;

    private static final Supplier<String> PROCESS_ID_GENERATOR = () -> UUID.randomUUID().toString();

    public ProcessEngine() {
        asyncExecutor = new ThreadPoolExecutor(3, 10, 10, TimeUnit.SECONDS,
                                               new LinkedBlockingQueue<>(), new ThreadPoolExecutor.DiscardOldestPolicy());
        steps = new LinkedList<>();
        listeners = new HashSet<>();
    }

    public void setSteps(Queue<ExecutionStep> steps) {
        this.steps = steps;
    }

    public void addListener(ExecutionListener listener) {
        listeners.add(listener);
    }

    public String startProcess(Map<String, Object> variables) {
        Map<String, Object> stepsCache = new ConcurrentHashMap<>(variables);

        StepContext context = new StepContext(stepsCache, PROCESS_ID_GENERATOR.get());

        asyncExecutor.execute(() -> runProcess(context));

        return context.getProcessId();
    }

    private void runProcess(StepContext context) {
        for (ExecutionStep step : steps) {
            step.execute(context);
        }
        //should be dispatched per event, not just at the end
        listeners.forEach(listener -> listener.notify(context));
    }
}
