package com.example.ordermanagmenttemporal.flow;

import io.temporal.spring.boot.WorkflowImpl;

@WorkflowImpl(taskQueues = "REPORT_TASK_QUEUE")
public class ReportWorkflowImpl implements ReportWorkflow {
    @Override
    public void generateDailyReport() {
        System.out.println("Daily report processor started: " + java.time.LocalDateTime.now());
    }
}
