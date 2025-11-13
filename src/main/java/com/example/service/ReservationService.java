package com.example.service;

import com.example.dto.response.ReservationAnalyticsResponse;
import com.example.dto.request.ReservationRequest;
import com.example.dto.response.ReservationResponse;
import com.example.model.Product;
import com.example.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final Warehouse warehouse;

    public ReservationResponse createReservation(ReservationRequest request) {
        Reservation reservation = new Reservation(request.getCustomerId());

        for (Map.Entry<String, Integer> entry: request.getItems().entrySet()) {
            Product product = warehouse.findProductByName(entry.getKey());
            if (product == null) {
                return ReservationResponse.failure("Product not found " + entry.getKey());
            }
        }

        if (warehouse.createReservation(reservation)) {
            return ReservationResponse.success(reservation);
        }else  {
            return ReservationResponse.failure("Failed to create reservation");
        }
    }

    public ReservationResponse cancelReservation(String reservationId) {
        if (warehouse.cancelReservation(reservationId)) {
            ReservationResponse response = new ReservationResponse();
            response.setSuccess(true);
            response.setReservationId(reservationId);
            response.setMessage("Reservation cancelled");
            return response;
        } else  {
            return ReservationResponse.failure("Reservation not found " + reservationId);
        }
    }

    public ReservationAnalyticsResponse getReservationAnalytics() {
        ReservationAnalyticsResponse response = new ReservationAnalyticsResponse();
        Map<String, ReservationAnalyticsResponse.ProductReservationAnalytics> productAnalysis = new HashMap<>();

        Map<Product, Integer> inventory = warehouse.getInventory();

        for (Map.Entry<Product, Integer> entry: inventory.entrySet()) {
            Product product = entry.getKey();

            ReservationAnalyticsResponse.ProductReservationAnalytics analytics = new ReservationAnalyticsResponse.ProductReservationAnalytics();

            analytics.setProductName(product.getName());
            analytics.setTotalQuantity(product.getQuantity());
            analytics.setAvailableQuantity(warehouse.getAvailableQuantity(product));
            analytics.setReservedQuantity(warehouse.getTotalReservedQuantity(product));

            if (product.getQuantity() > 0) {
                double percentage = (double) analytics.getReservedQuantity() / product.getQuantity() * 100;
                analytics.setReservedPercentage(Math.round(percentage * 100.0) /  100.0);
            } else {
                analytics.setReservedPercentage(0.0);
            }

            analytics.setOrderedQuantity(0);

            productAnalysis.put(product.getName(), analytics);
        }

        response.setProductAnalytics(productAnalysis);
        response.setActiveReservations(warehouse.getActiveReservations().size());
        response.setTotalReservations(0);
        response.setCancelledReservations(0);

        return response;
    }
}
