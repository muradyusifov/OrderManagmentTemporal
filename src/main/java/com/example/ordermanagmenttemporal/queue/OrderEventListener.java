package com.example.ordermanagmenttemporal.queue;

import com.example.ordermanagmenttemporal.config.TaskQueues;
import com.example.ordermanagmenttemporal.dto.OrderEventDto;
import com.example.ordermanagmenttemporal.flow.OrderWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.client.WorkflowOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
public class OrderEventListener {

    private final WorkflowClient workflowClient;
    private final JsonMapper jsonMapper;

    public OrderEventListener(WorkflowClient workflowClient, JsonMapper jsonMapper) {
        this.workflowClient = workflowClient;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Messages arrive as JSON strings (default StringDeserializer), e.g. {"orderId":"101","amount":250.0}
     */
    @KafkaListener(topics = "order-created-events", groupId = "temporal-order-group")
    public void handleOrderCreatedEvent(String message) {
        try {
            OrderEventDto event = jsonMapper.readValue(message, OrderEventDto.class);

            OrderWorkflow workflow = workflowClient.newWorkflowStub(
                    OrderWorkflow.class,
                    WorkflowOptions.newBuilder()
                            .setWorkflowId("Order-" + event.getOrderId())
                            .setTaskQueue(TaskQueues.ORDER_TASK_QUEUE)
                            .build()
            );

            WorkflowClient.start(workflow::processOrder, event.getOrderId(), event.getAmount());

            log.info("Kafka event received, Temporal Workflow started: {}", event.getOrderId());

        } catch (WorkflowExecutionAlreadyStarted e) {
            // Kafka is at-least-once: a redelivered event must not create a second workflow
            log.warn("Workflow already started, duplicate event ignored: {}", message);
        } catch (Exception e) {
            log.error("An error occurred while starting the workflow for message: {}", message, e);
            // You can route the event to a DLQ (Dead Letter Queue)
        }
    }
}
