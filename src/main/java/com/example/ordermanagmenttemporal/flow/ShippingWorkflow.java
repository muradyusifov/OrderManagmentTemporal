package com.example.ordermanagmenttemporal.flow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ShippingWorkflow {
    @WorkflowMethod
    String arrangeShipping(String orderId);
}