package com.example.ordermanagmenttemporal.flow;

import com.example.ordermanagmenttemporal.activities.OrderActivities;
import com.example.ordermanagmenttemporal.config.TaskQueues;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

@WorkflowImpl(taskQueues = TaskQueues.ORDER_TASK_QUEUE)
public class OrderWorkflowImpl implements OrderWorkflow {

    private static final Logger log = Workflow.getLogger(OrderWorkflowImpl.class);

    private boolean isApproved = false;
    private boolean isRejected = false;
    private String currentStatus = "INITIALIZED";

    private final OrderActivities activities = Workflow.newActivityStub(
            OrderActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
                    .build()
    );

    @Override
    public String processOrder(String orderId, double amount) {
        Saga saga = new Saga(
                new Saga.Options.Builder()
                        .setParallelCompensation(false)
                        .build()
        );

        try {
            currentStatus = "CHARGING_PAYMENT";
            activities.chargePayment(orderId, amount);
            saga.addCompensation(activities::compensatePayment, orderId, amount);

            currentStatus = "WAITING_FOR_APPROVAL";
            Workflow.await(() -> isApproved || isRejected);

            // If the user rejects the order, we do not throw an exception.
            // We simply trigger compensation, update the status, and complete the workflow gracefully.
            if (isRejected) {
                currentStatus = "REJECTED_AND_COMPENSATING";
                saga.compensate(); // Immediately refunds the payment
                currentStatus = "REJECTED";

                return "Order Rejected by User and Payment Refunded Successfully";
            }

            currentStatus = "RESERVING_INVENTORY";
            activities.reserveInventory(orderId);

            currentStatus = "ARRANGING_SHIPPING";
            ShippingWorkflow shippingWorkflow = Workflow.newChildWorkflowStub(
                    ShippingWorkflow.class,
                    ChildWorkflowOptions.newBuilder()
                            .setWorkflowId("Shipping-" + orderId)
                            .build()
            );

            String shippingResult = shippingWorkflow.arrangeShipping(orderId);
            log.info("Result received by the parent workflow: {}", shippingResult);

            currentStatus = "COMPLETED";
            return "Order and Shipping Completed Successfully";

        } catch (Exception e) {
            currentStatus = "FAILED_AND_COMPENSATING";
            saga.compensate();

            currentStatus = "COMPENSATED";
            throw Workflow.wrap(e);
        }
    }

    @Override
    public void approveOrder() {
        // Ignore approval if the order was already rejected
        if (!isRejected) {
            this.isApproved = true;
        }
    }

    @Override
    public void rejectOrder() {
        // Ignore rejection if the order was already approved
        if (!isApproved) {
            this.isRejected = true;
        }
    }

    @Override
    public String getOrderStatus() {
        return this.currentStatus;
    }
}
