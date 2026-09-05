package com.example.ordermanagmenttemporal.dto;

import lombok.Data;

@Data
public class OrderEventDto {
    private String orderId;
    private double amount;
}