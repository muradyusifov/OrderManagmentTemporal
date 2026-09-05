package com.example.ordermanagmenttemporal.config;

import com.example.ordermanagmenttemporal.activities.OrderActivities;
import com.example.ordermanagmenttemporal.flow.OrderWorkflowImpl;
import com.example.ordermanagmenttemporal.flow.ReportWorkflowImpl;
import com.example.ordermanagmenttemporal.flow.ShippingWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient, OrderActivities orderActivities) {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);

        Worker worker = factory.newWorker("ORDER_TASK_QUEUE");
        worker.registerWorkflowImplementationTypes(OrderWorkflowImpl.class, ShippingWorkflowImpl.class, ReportWorkflowImpl.class);
        worker.registerActivitiesImplementations(orderActivities);

        factory.start();
        return factory;
    }
}