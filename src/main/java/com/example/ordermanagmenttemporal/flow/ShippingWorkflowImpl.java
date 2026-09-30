package com.example.ordermanagmenttemporal.flow;

import com.example.ordermanagmenttemporal.config.TaskQueues;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

@WorkflowImpl(taskQueues = TaskQueues.ORDER_TASK_QUEUE)
public class ShippingWorkflowImpl implements ShippingWorkflow {

    private static final Logger log = Workflow.getLogger(ShippingWorkflowImpl.class);

    @Override
    public String arrangeShipping(String orderId) {
        log.info("Child Workflow: Courier has been notified, shipping has started. Order: {}", orderId);
        Workflow.sleep(Duration.ofSeconds(3));
        log.info("Child Workflow: Delivery completed successfully! Order: {}", orderId);
        return "Shipping Completed";
    }
}
