package com.example.aspect;

import com.example.dto.OrderRequest;
import com.example.dto.OrderResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
@Slf4j
public class OrderAnalyticsAspect {
    @Getter
    private final AtomicLong totalOrders = new AtomicLong(0);
    @Getter
    private final AtomicLong successfulOrders = new AtomicLong(0);
    private final AtomicLong failedOrders = new AtomicLong(0);
    @Getter
    private final ConcurrentHashMap<String, Integer> productOrderCount = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> revenueByProduct = new ConcurrentHashMap<>();
    private final AtomicLong totalRevenue = new AtomicLong(0);

    public Object trackOrderCreation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        totalOrders.incrementAndGet();

        OrderRequest request = (OrderRequest) joinPoint.getArgs()[0];

        log.info("Order Analytics");
        log.info("Timestamp: {}", LocalDateTime.now());
        log.info("Customer ID: {}", request.getCustomerID());
        log.info("Requested Items: {}", request.getItems());

        OrderResponse response = null;
        try {
            response = (OrderResponse) joinPoint.proceed();

            if (response.isSuccess()) {
                successfulOrders.incrementAndGet();

                productOrderCount.putAll(request.getItems());

                long revenue = (long) (response.getTotalPrice()*100);
                totalRevenue.addAndGet(revenue);

                log.info("Status: SUCCESS");
                log.info("Order ID: {}", response.getOrderId());
                log.info("Total Price: ${}", response.getTotalPrice());
            } else  {
                failedOrders.incrementAndGet();
                log.info("Status: FAILED");
                log.info("Reason: {}", response.getMessage());
            }
            return  response;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("Duration: {} ms", duration);
            log.info("Total Orders: {}", totalOrders.get());
            log.info("Successful: {}", successfulOrders.get());
            log.info("Failed: {}", failedOrders.get());
            log.info("Success Rate: {}%",
                    totalOrders.get() > 0
                            ? (successfulOrders.get() * 100.0 / totalOrders.get())
                            : 0);
            log.info("Total Revenue: ${}", totalRevenue.get() / 100.0);
            log.info("Product Order Counts: {}", productOrderCount);
        }
    }

}
