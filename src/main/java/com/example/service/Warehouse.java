package com.example.service;

import com.example.model.Order;
import com.example.model.Product;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Warehouse {
    private final ConcurrentHashMap<Product, Integer> inventory = new ConcurrentHashMap<>();

    public Warehouse() {
        List<Product> products = List.of(
                new Product("Laptop", 999.99, 50),
                new Product("Mouse", 29.99, 100),
                new Product("Keyboard", 79.99, 75),
                new Product("Monitor", 299.99, 40),
                new Product("Headphones", 149.99, 60)
        );

        for (Product product : products) {
            this.inventory.put(product, product.getQuantity());
        }
    }

    public synchronized boolean processOrder(Order order) {
        for (Map.Entry<Product, Integer> entry : order.getOrderItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            Integer available = inventory.get(product);

            if (available == null || available < quantity) {
                System.out.println("Insufficient stock for product: " + product.getName());
                return false;
            }
        }

        double orderPrice = 0;
        for (Map.Entry<Product, Integer> entry : order.getOrderItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            inventory.compute(product, (k, v) -> v - quantity);
            product.decreaseQuantity(quantity);
            orderPrice += product.getPrice() * quantity;
        }

        order.setOrderPrice(orderPrice);
        System.out.println("Order processed: " + order.getOrderID() + ", Total: $" + orderPrice);
        return true;
    }

    public int getAvailableQuantity(Product product) {
        return inventory.getOrDefault(product, 0);
    }

    public Map<Product, Integer> getInventory() {
        return new ConcurrentHashMap<>(inventory);
    }

    public Product findProductByName(String name) {
        return inventory.keySet().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}

