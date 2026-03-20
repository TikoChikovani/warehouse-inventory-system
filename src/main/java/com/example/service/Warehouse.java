package com.example.service;

import com.example.model.Order;
import com.example.model.Product;
import com.example.model.Reservation;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Warehouse {
    private final ConcurrentHashMap<Product, Integer> inventory = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Map<Product, Integer>> reservations = new ConcurrentHashMap<>();

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

    public synchronized boolean createReservation(Reservation reservation) {
        Map<Product, Integer> items = reservation.getReservedItems();
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);

        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            Product product = entry.getKey();
            int requestedQty = entry.getValue();
            Integer availableQty = inventory.get(product);

            if (availableQty == null || availableQty < requestedQty) {
                System.out.println("Reservation for product " + product.getName() + " failed - insufficient stock");
                return false;
            }
        }

        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            inventory.compute(product, (k, v) -> v - quantity);
            product.decreaseQuantity(quantity);
        }

        reservations.put(reservation.getReservationId(), new ConcurrentHashMap<>(items));
        System.out.println("Reservation created " + reservation.getReservationId() +
                " for customer " + reservation.getCustomerId());
        return true;
    }

    public synchronized boolean cancelReservation(String reservationId) {
        Map<Product, Integer> reservedItems = reservations.remove(reservationId);

        if (reservedItems == null) {
            System.out.println("Reservation not found " + reservationId);
            return false;
        }

        for (Map.Entry<Product, Integer> entry : reservedItems.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            inventory.compute(product, (k, v) -> v + quantity);
            product.increaseQuantity(quantity);
        }

        System.out.println("Reservation cancelled " + reservationId);
        return true;
    }

    public synchronized boolean convertReservationToOrder(String reservationId, Order order) {
        Map<Product, Integer> reservedItems = reservations.get(reservationId);

        if (reservedItems == null) {
            System.out.println("Reservation not found: " + reservationId);
            return false;
        }

        double orderPrice = 0;
        for (Map.Entry<Product, Integer> entry : reservedItems.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            orderPrice += product.getPrice() * quantity;
        }

        order.setOrderPrice(orderPrice);
        reservations.remove(reservationId);
        System.out.println("Reservation converted to order: " + reservationId);
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

    public int getTotalReservedQuantity(Product product) {
        return reservations.values().stream()
                .mapToInt(reservation -> reservation.getOrDefault(product, 0))
                .sum();
    }

    public Map<String, Map<Product, Integer>> getActiveReservations() {
        return new ConcurrentHashMap<>(reservations);
    }
}

