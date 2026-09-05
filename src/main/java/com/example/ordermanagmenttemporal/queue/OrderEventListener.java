package com.example.ordermanagmenttemporal.queue;

import com.example.ordermanagmenttemporal.dto.OrderEventDto;
import com.example.ordermanagmenttemporal.flow.OrderWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private final WorkflowClient workflowClient;

    public OrderEventListener(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @KafkaListener(topics = "order-created-events", groupId = "temporal-order-group")
    public void handleOrderCreatedEvent(OrderEventDto event) {
        try {
            OrderWorkflow workflow = workflowClient.newWorkflowStub(
                    OrderWorkflow.class,
                    WorkflowOptions.newBuilder()
                            .setWorkflowId("Order-" + event.getOrderId())
                            .setTaskQueue("ORDER_TASK_QUEUE")
                            .build()
            );

            WorkflowClient.start(
                    workflow::processOrder,
                    event.getOrderId(),
                    event.getAmount()
            );

            System.out.println(
                    "Kafka event received, Temporal Workflow started: "
                            + event.getOrderId()
            );

        } catch (Exception e) {
            System.err.println(
                    "An error occurred while starting the workflow: "
                            + e.getMessage()
            );

            // You can route the event to a DLQ (Dead Letter Queue)
        }
    }
}