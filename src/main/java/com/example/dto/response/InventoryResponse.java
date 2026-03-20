package com.example.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class InventoryResponse {
    private Map<String, ProductInfo> inventory;
    private int totalProducts;
    private int totalQuantity;

    @Data
    public static class ProductInfo {
        private String name;
        private double price;
        private int availableQuantity;
    }
}
