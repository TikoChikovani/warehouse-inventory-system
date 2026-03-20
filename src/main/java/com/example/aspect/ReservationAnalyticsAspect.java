package com.example.aspect;

import com.example.dto.request.ReservationRequest;
import com.example.dto.response.ReservationResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Aspect
@Component
@Slf4j
public class ReservationAnalyticsAspect {
    private final AtomicLong totalReservations = new AtomicLong(0);
    private final AtomicLong successfulReservations = new AtomicLong(0);
    private final AtomicLong failedReservations = new AtomicLong(0);
    private final AtomicLong cancelledReservations = new AtomicLong(0);
    private final ConcurrentHashMap<String, Integer> productReservationCount = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> productReservedQuantity = new ConcurrentHashMap<>();

    @Around("execution(* com.example.service.ReservationService.createReservation(..))")
    public Object trackReservationCreation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        totalReservations.incrementAndGet();

        ReservationRequest request = (ReservationRequest) joinPoint.getArgs()[0];

        log.info("Reservation creation analysis");
        log.info("Timestamp: {}", LocalDateTime.now());
        log.info("Customer ID: {}", request.getCustomerId());
        log.info("Requested Items: {}", request.getItems());

        int totalItems = request.getItems().size();
        int totalQuantity = request.getItems().values().stream().mapToInt(Integer::intValue).sum();
        log.info("Total Item Types: {}", totalItems);
        log.info("Total Quantity: {}", totalQuantity);

        ReservationResponse response;
        try {
            response = (ReservationResponse) joinPoint.proceed();

            if (response.isSuccess()) {
                successfulReservations.incrementAndGet();

                request.getItems().forEach((productName, quantity) -> {
                    productReservationCount.merge(productName, 1, Integer::sum);
                    productReservedQuantity.merge(productName, quantity, Integer::sum);
                });

                log.info("Status: SUCCESS");
                log.info("Reservation ID: {}", response.getReservationId());
                log.info("Reserved Items: {}", response.getReservedItems());
            } else {
                failedReservations.incrementAndGet();
                log.info("Status: FAILED");
                log.info("Reason: {}", response.getMessage());
            }

            return response;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            log.info("Execution Duration: {} ms", duration);
            log.info("Cumulative statistics");
            log.info("Total Reservations Attempted: {}", totalReservations.get());
            log.info("Successful Reservations: {}", successfulReservations.get());
            log.info("Failed Reservations: {}", failedReservations.get());
            log.info("Cancelled Reservations: {}", cancelledReservations.get());

            if (totalReservations.get() > 0) {
                double successRate = (successfulReservations.get() * 100.0) / totalReservations.get();
                log.info("Success Rate: {}%", successRate);
            }

            log.info("Product reservation metrics");
            log.info("Reservation Count by Product: {}", productReservationCount);
            log.info("Total Reserved Quantity by Product: {}", productReservedQuantity);

            log.info("Reserved percentage by product");
            productReservedQuantity.forEach((product, reserved) ->
                    log.info("Product: {} - Reserved Quantity: {}", product, reserved)
            );
        }
    }

    @Around("execution(* com.example.service.ReservationService.cancelReservation(..))")
    public Object trackReservationCancellation(ProceedingJoinPoint joinPoint) throws Throwable {
        String reservationId = (String) joinPoint.getArgs()[0];

        log.info("Reservation cancellation analysis");
        log.info("Timestamp: {}", LocalDateTime.now());
        log.info("Reservation ID: {}", reservationId);

        ReservationResponse response = (ReservationResponse) joinPoint.proceed();

        if (response.isSuccess()) {
            cancelledReservations.incrementAndGet();
            log.info("Status: CANCELLED");
        } else {
            log.info("Status: NOT FOUND");
        }

        log.info("Message: {}", response.getMessage());
        log.info("Total Cancellations: {}", cancelledReservations.get());

        return response;
    }

}
