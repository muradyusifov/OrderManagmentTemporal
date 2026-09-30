package com.example.ordermanagmenttemporal.controller;

import com.example.ordermanagmenttemporal.config.TaskQueues;
import com.example.ordermanagmenttemporal.flow.OrderWorkflow;
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

    /**
     * Starts the order workflow asynchronously. The workflow waits for an approve/reject signal,
     * so a synchronous call here would block the HTTP request until someone approves the order.
     */
    @PostMapping("/{id}")
    public ResponseEntity<String> createOrder(@PathVariable String id, @RequestParam double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        OrderWorkflow workflow = workflowClient.newWorkflowStub(
                OrderWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId(id))
                        .setTaskQueue(TaskQueues.ORDER_TASK_QUEUE)
                        .build()
        );

        WorkflowClient.start(workflow::processOrder, id, amount);

        return ResponseEntity.accepted()
                .body("Order accepted and started in the background: " + workflowId(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveOrder(@PathVariable String id) {
        existingWorkflow(id).approveOrder();
        return ResponseEntity.ok("Approval signal sent!");
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectOrder(@PathVariable String id) {
        existingWorkflow(id).rejectOrder();
        return ResponseEntity.ok("Rejection signal sent!");
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getOrderStatus(@PathVariable String id) {
        String status = existingWorkflow(id).getOrderStatus();
        return ResponseEntity.ok("Current Order Status: " + status);
    }

    private OrderWorkflow existingWorkflow(String id) {
        return workflowClient.newWorkflowStub(OrderWorkflow.class, workflowId(id));
    }

    private static String workflowId(String id) {
        return "Order-" + id;
    }
}
