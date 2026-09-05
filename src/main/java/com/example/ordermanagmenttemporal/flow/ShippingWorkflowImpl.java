package com.example.ordermanagmenttemporal.flow;

import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

import java.time.Duration;

@WorkflowImpl(taskQueues = "ORDER_TASK_QUEUE")
public class ShippingWorkflowImpl implements ShippingWorkflow {
    @Override
    public String arrangeShipping(String orderId) {
        System.out.println("Child Workflow: Courier has been notified, shipping has started. Order: " + orderId);
        Workflow.sleep(Duration.ofSeconds(3));
        System.out.println("Child Workflow: Delivery completed successfully! Order: " + orderId);
        return "Shipping Completed";
    }
}