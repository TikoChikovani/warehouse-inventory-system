package com.example.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class ReservationAnalyticsResponse {
    private Map<String, ProductReservationAnalytics> productAnalytics;
    private long totalReservations;
    private long activeReservations;
    private long cancelledReservations;

    @Data
    public static class ProductReservationAnalytics {
        private String productName;
        private int totalQuantity;
        private int availableQuantity;
        private int reservedQuantity;
        private double reservedPercentage;  // THIS IS THE KEY METRIC
        private int orderedQuantity;
    }
}
