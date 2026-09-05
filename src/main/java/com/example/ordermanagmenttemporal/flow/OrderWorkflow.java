package com.example.ordermanagmenttemporal.flow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderWorkflow {
    @WorkflowMethod
    String processOrder(String orderId, double amount);

    @SignalMethod
    void approveOrder();

    @SignalMethod
    void rejectOrder();

    @QueryMethod
    String getOrderStatus();
}