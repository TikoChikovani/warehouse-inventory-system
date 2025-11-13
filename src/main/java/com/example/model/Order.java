package com.example.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Order {
    private final String orderID;
    private final Map<Product, Integer> orderItems;
    private double orderPrice;

    public Order(String orderID) {
        this.orderID = orderID;
        this.orderItems = new HashMap<>();
        this.orderPrice = 0;
    }

    public void addProduct(Product product, int quantity) {
        orderItems.put(product, quantity);
    }
}
