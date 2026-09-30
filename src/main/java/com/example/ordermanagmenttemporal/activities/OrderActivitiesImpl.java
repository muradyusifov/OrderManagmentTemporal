package com.example.ordermanagmenttemporal.activities;

import com.example.ordermanagmenttemporal.config.TaskQueues;
import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ActivityImpl(taskQueues = TaskQueues.ORDER_TASK_QUEUE)
public class OrderActivitiesImpl implements OrderActivities {

    @Override
    public void chargePayment(String orderId, double amount) {
        log.info("Charging ${} for order {}", amount, orderId);
    }

    @Override
    public void reserveInventory(String orderId) {
        log.info("Reserving inventory items for order {}", orderId);
    }

    @Override
    public void compensatePayment(String orderId, double amount) {
        log.info("Refunding ${} for order {}", amount, orderId);
    }
}
