package com.example.ordermanagmenttemporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface OrderActivities {
    @ActivityMethod
    void chargePayment(String orderId, double amount);

    @ActivityMethod
    void reserveInventory(String orderId);

    @ActivityMethod
    void compensatePayment(String orderId, double amount);
}