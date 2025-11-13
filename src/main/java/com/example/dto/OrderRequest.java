package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Map;

@Data
public class OrderRequest {
    @NotBlank(message = "Customer ID is required")
    private String customerID;

    @NotEmpty(message = "At least one product must be ordered")
    private Map<String, Integer> items;
}
