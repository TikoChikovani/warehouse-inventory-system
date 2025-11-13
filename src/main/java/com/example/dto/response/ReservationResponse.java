package com.example.dto.response;

import com.example.model.Reservation;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ReservationResponse {
    private String reservationId;
    private String customerId;
    private Map<String, Integer> reservedItems;
    private LocalDateTime createdAt;
    private String status;
    private boolean success;
    private String message;

    public static ReservationResponse success(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setReservationId(reservation.getReservationId());
        response.setCustomerId(reservation.getCustomerId());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setStatus(reservation.getStatus().toString());
        response.setSuccess(true);
        response.setMessage("Reservation created successfully");

        Map<String, Integer> items = new java.util.HashMap<>();
        reservation.getReservedItems().forEach((product, qty) ->
                items.put(product.getName(), qty));
        response.setReservedItems(items);

        return response;
    }

    public static ReservationResponse failure(String message) {
        ReservationResponse response = new ReservationResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
