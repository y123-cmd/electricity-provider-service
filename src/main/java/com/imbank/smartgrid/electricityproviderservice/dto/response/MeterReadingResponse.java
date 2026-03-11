package com.imbank.smartgrid.electricityproviderservice.dto.response;

import com.imbank.smartgrid.electricityproviderservice.entity.ProviderName;
import com.imbank.smartgrid.electricityproviderservice.entity.ReadingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MeterReadingResponse {
    private Long readingId;
    private String meterId;
    private String citizenId;
    private ProviderName providerName;
    private BigDecimal consumptionKwh;
    private LocalDateTime readingDate;
    private ReadingType readingType;
    private LocalDateTime createdAt;
}
