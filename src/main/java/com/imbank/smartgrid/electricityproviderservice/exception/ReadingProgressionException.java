package com.imbank.smartgrid.electricityproviderservice.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ReadingProgressionException extends RuntimeException{
    private final String meterId;
    private final BigDecimal currentReading;
    private final BigDecimal previousReading;

    public ReadingProgressionException(String meterId, BigDecimal currentReading, BigDecimal previousReading){
        super(String.format(
                "Reading progression violation for meter %s: New reading (%.2f kWh) must be greater than previous reading (%.2f kWh)",
                meterId, currentReading, previousReading
        ));
        this.meterId = meterId;
        this.currentReading = currentReading;
        this.previousReading = previousReading;
    }
}
