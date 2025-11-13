package com.example.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Reservation {
    public enum ReservationStatus {
        ACTIVE,
        CANCELLED,
        CONFIRMED
    }

    private final String reservationId;
    private final String customerId;
    private final Map<Product, Integer> reservedItems;
    private final LocalDateTime createdAt;
    private ReservationStatus status;

    public Reservation(String customerId) {
        this.reservationId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.reservedItems = new ConcurrentHashMap<>();
        this.createdAt = LocalDateTime.now();
        this.status = ReservationStatus.ACTIVE;
    }

    public void addProduct(Product product, int quantity) {
        reservedItems.merge(product, quantity, Integer::sum);
    }

    public Map<Product, Integer> getReservedItems() {
        return new ConcurrentHashMap<>(reservedItems);
    }
}
