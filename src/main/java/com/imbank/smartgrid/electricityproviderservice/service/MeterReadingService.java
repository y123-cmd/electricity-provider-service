package com.imbank.smartgrid.electricityproviderservice.service;

import com.imbank.smartgrid.electricityproviderservice.dto.request.MeterReadingRequest;
import com.imbank.smartgrid.electricityproviderservice.dto.response.MeterReadingResponse;
import com.imbank.smartgrid.electricityproviderservice.entity.ProviderName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MeterReadingService {
    MeterReadingResponse saveReading(MeterReadingRequest request);
    MeterReadingResponse getReadingById(Long readingId);
    Page<MeterReadingResponse> getAllReadings(Pageable pageable);
    Page<MeterReadingResponse>getReadingsByMeterId(String MeterId, Pageable pageable);
    Page<MeterReadingResponse>getReadingsByProvider(ProviderName providerName, Pageable pageable);
    Page<MeterReadingResponse>getReadingsByCitizenId(String citizenId, Pageable pageable);
    List<MeterReadingResponse>getReadingsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    BigDecimal getAverageConsumption(ProviderName providerName);
    Long countReadingsByProvider(ProviderName providerName);
    List<MeterReadingResponse> saveReadingsBatch(List<MeterReadingRequest> requests);
}
