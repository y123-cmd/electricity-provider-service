package com.imbank.smartgrid.electricityproviderservice.service;

import com.imbank.smartgrid.electricityproviderservice.dto.request.MeterReadingRequest;
import com.imbank.smartgrid.electricityproviderservice.dto.response.MeterReadingResponse;
import com.imbank.smartgrid.electricityproviderservice.entity.MeterReading;
import com.imbank.smartgrid.electricityproviderservice.entity.ProviderName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    Long countReadingsByProvider(ProviderName providerName);
}
