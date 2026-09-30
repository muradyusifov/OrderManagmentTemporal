package com.example.ordermanagmenttemporal.flow;

import com.example.ordermanagmenttemporal.config.TaskQueues;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Instant;

@WorkflowImpl(taskQueues = TaskQueues.REPORT_TASK_QUEUE)
public class ReportWorkflowImpl implements ReportWorkflow {

    private static final Logger log = Workflow.getLogger(ReportWorkflowImpl.class);

    @Override
    public void generateDailyReport() {
        // Workflow.currentTimeMillis() is deterministic (replay-safe), unlike LocalDateTime.now()
        log.info("Daily report processor started: {}", Instant.ofEpochMilli(Workflow.currentTimeMillis()));
    }
}
