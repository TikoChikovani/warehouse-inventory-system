package com.example.controller;

import com.example.dto.request.ReservationRequest;
import com.example.dto.response.ReservationAnalyticsResponse;
import com.example.dto.response.ReservationResponse;
import com.example.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable String reservationId) {
        ReservationResponse response = reservationService.cancelReservation(reservationId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/analytics")
    public ResponseEntity<ReservationAnalyticsResponse> getReservationAnalytics() {
        return ResponseEntity.ok(reservationService.getReservationAnalytics());
    }
}
