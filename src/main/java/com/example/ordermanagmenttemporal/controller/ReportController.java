package com.example.ordermanagmenttemporal.controller;

import com.example.ordermanagmenttemporal.config.TaskQueues;
import com.example.ordermanagmenttemporal.flow.ReportWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final WorkflowClient workflowClient;

    public ReportController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @PostMapping("/schedule")
    public ResponseEntity<String> startCronWorkflow() {
        ReportWorkflow workflow = workflowClient.newWorkflowStub(
                ReportWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("Daily-Report-Cron-Job")
                        .setTaskQueue(TaskQueues.REPORT_TASK_QUEUE)
                        .setCronSchedule("0 0 * * *")
                        .build()
        );

        WorkflowClient.start(workflow::generateDailyReport);

        return ResponseEntity.ok("Cron Workflow scheduled successfully!");
    }
}
