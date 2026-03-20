package com.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Map;

@Data
public class ReservationRequest {
    @NotBlank(message = "Customer ID is required")
    private String CustomerId;

    @NotEmpty(message = "At least one product must be reserved")
    private Map<String, Integer> items;
}
