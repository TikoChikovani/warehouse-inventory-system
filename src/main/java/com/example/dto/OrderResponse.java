package com.example.dto;

import com.example.model.Order;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class OrderResponse {
    private String orderId;
    private String customerId;
    private Map<String, Integer> items;
    private double totalPrice;
    private boolean success;
    private String message;

    public static OrderResponse success(Order order, String customerId) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderID());
        response.setCustomerId(customerId);
        response.setTotalPrice(order.getOrderPrice());
        response.setSuccess(true);
        response.setMessage("Order processed successfully");

        Map<String, Integer> items = new HashMap<>();
        order.getOrderItems().forEach((product, quantity) -> {
            items.put(product.getName(), quantity);
        });
        response.setItems(items);

        return response;
    }

    public static OrderResponse failure(String message) {
        OrderResponse response = new OrderResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
