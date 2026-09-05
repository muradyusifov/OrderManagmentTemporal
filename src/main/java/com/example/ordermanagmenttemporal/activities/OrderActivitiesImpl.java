package com.example.ordermanagmenttemporal.activities;

import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(taskQueues = "ORDER_TASK_QUEUE")
public class OrderActivitiesImpl implements OrderActivities {

    @Override
    public void chargePayment(String orderId, double amount) {
        System.out.println("Charging $" + amount + " for order " + orderId);
    }

    @Override
    public void reserveInventory(String orderId) {
        System.out.println("Reserving inventory items for order " + orderId);
    }

    @Override
    public void compensatePayment(String orderId, double amount) {
        System.out.println("Refunding $" + amount + " for order " + orderId);
    }
}