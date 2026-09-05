package com.example.ordermanagmenttemporal.controller;

import com.example.ordermanagmenttemporal.flow.OrderWorkflow;
import com.example.ordermanagmenttemporal.flow.ReportWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final WorkflowClient workflowClient;

    public OrderController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> createOrder(@PathVariable String id, @RequestParam double amount) {
        OrderWorkflow workflow = workflowClient.newWorkflowStub(
                OrderWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("Order-" + id)
                        .setTaskQueue("ORDER_TASK_QUEUE")
                        .build()
        );

        String result = workflow.processOrder(id, amount);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/async")
    public ResponseEntity<String> createOrderAsync(@PathVariable String id, @RequestParam double amount) {
        OrderWorkflow workflow = workflowClient.newWorkflowStub(
                OrderWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("Order-" + id)
                        .setTaskQueue("ORDER_TASK_QUEUE")
                        .build()
        );

        WorkflowClient.start(workflow::processOrder, id, amount);

        return ResponseEntity.accepted()
                .body("Order accepted and started in the background: Order-" + id);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveOrder(@PathVariable String id) {
        OrderWorkflow workflow = workflowClient.newWorkflowStub(
                OrderWorkflow.class,
                "Order-" + id
        );

        workflow.approveOrder();

        return ResponseEntity.ok("Approval signal sent!");
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectOrder(@PathVariable String id) {
        OrderWorkflow workflow = workflowClient.newWorkflowStub(
                OrderWorkflow.class,
                "Order-" + id
        );

        workflow.rejectOrder();

        return ResponseEntity.ok("Rejection signal sent!");
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getOrderStatus(@PathVariable String id) {
        OrderWorkflow workflow = workflowClient.newWorkflowStub(
                OrderWorkflow.class,
                "Order-" + id
        );

        String status = workflow.getOrderStatus();

        return ResponseEntity.ok("Current Order Status: " + status);
    }

    @PostMapping("/schedule")
    public ResponseEntity<String> startCronWorkflow() {
        ReportWorkflow workflow = workflowClient.newWorkflowStub(
                ReportWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("Daily-Report-Cron-Job")
                        .setTaskQueue("REPORT_TASK_QUEUE")
                        .setCronSchedule("0 0 * * *")
                        .build()
        );

        WorkflowClient.start(workflow::generateDailyReport);

        return ResponseEntity.ok("Cron Workflow scheduled successfully!");
    }
}