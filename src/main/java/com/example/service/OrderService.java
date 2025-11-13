package com.example.service;

import com.example.dto.response.InventoryResponse;
import com.example.dto.request.OrderRequest;
import com.example.dto.response.OrderResponse;
import com.example.model.Order;
import com.example.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final Warehouse warehouse;

    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order(request.getCustomerID() + "-" + System.currentTimeMillis());

        for (Map.Entry<String,Integer> entry : request.getItems().entrySet()) {
            Product product = warehouse.findProductByName(entry.getKey());
            if (product == null) {
                return OrderResponse.failure("Product not found " + entry.getKey());
            }
            order.addProduct(product,entry.getValue());
        }

        if (warehouse.processOrder(order)) {
            return OrderResponse.success(order, request.getCustomerID());
        } else  {
            return OrderResponse.failure("Order processing failed");
        }
    }

    public InventoryResponse getInventory(){
        InventoryResponse response = new InventoryResponse();
        Map<String, InventoryResponse.ProductInfo> inventoryMap = new HashMap<>();

        Map<Product, Integer> warehouseInventory = warehouse.getInventory();
        int totalQty = 0;

        for (Map.Entry<Product, Integer> entry : warehouseInventory.entrySet()) {
            Product product = entry.getKey();

            InventoryResponse.ProductInfo info = new InventoryResponse.ProductInfo();
            info.setName(product.getName());
            info.setPrice(product.getPrice());
            info.setAvailableQuantity(entry.getValue());

            inventoryMap.put(product.getName(),  info);
            totalQty += entry.getValue();
        }
        response.setInventory(inventoryMap);
        response.setTotalProducts(inventoryMap.size());
        response.setTotalQuantity(totalQty);

        return response;
    }
}
