package com.imbank.smartgrid.electricityproviderservice.dto.request;

import com.imbank.smartgrid.electricityproviderservice.entity.ProviderName;
import com.imbank.smartgrid.electricityproviderservice.entity.ReadingType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MeterReadingRequest {
    @NotBlank(message = "meter Id is required")
    @Size(max = 20,message = "meter id must not exceed 20 characters")
    private String meterId;

    @NotBlank(message = "citizen Id is required")
    @Size(max = 20, message = "citizen id must not exceed 20 characters")
    private String citizenId;

    @NotNull(message = "provider name is required")
    private ProviderName providerName;

    @NotNull(message = "Consumption is required")
    @DecimalMin(value = "0.01", message = "Consumption must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid consumption format")
    private BigDecimal consumptionKwh;

    @NotNull(message = "Reading date is required")
    private LocalDateTime readingDate;

    @NotNull(message = "Reading type is required")
    private ReadingType readingType;

    private BigDecimal currentReading;

}
