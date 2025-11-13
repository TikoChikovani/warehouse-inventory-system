package com.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "name")
public class Product {
    private final String name;
    private final double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public synchronized int getQuantity() {
        return quantity;
    }

    public synchronized void decreaseQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public synchronized void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }

    @Override
    public String toString() {
        return name;
    }
}
