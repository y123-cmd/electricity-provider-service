package com.imbank.smartgrid.electricityproviderservice.service.impl;

import com.imbank.smartgrid.electricityproviderservice.dto.request.MeterReadingRequest;
import com.imbank.smartgrid.electricityproviderservice.dto.response.MeterReadingResponse;
import com.imbank.smartgrid.electricityproviderservice.entity.MeterReading;
import com.imbank.smartgrid.electricityproviderservice.entity.ProviderName;
import com.imbank.smartgrid.electricityproviderservice.exception.InvalidReadingException;
import com.imbank.smartgrid.electricityproviderservice.exception.ReadingProgressionException;
import com.imbank.smartgrid.electricityproviderservice.exception.ResourceNotFoundException;
import com.imbank.smartgrid.electricityproviderservice.mapper.MeterReadingMapper;
import com.imbank.smartgrid.electricityproviderservice.repository.MeterReadingRepository;
import com.imbank.smartgrid.electricityproviderservice.service.MeterReadingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository repository;
    private final MeterReadingMapper mapper;

    @Override
    public MeterReadingResponse saveReading(MeterReadingRequest request) {
        log.info("processing reading submission for meter: {}", request.getMeterId());

        validateReadingRequest(request);
        validateReadingProgression(request);

        MeterReading entity = mapper.toEntity(request);
        MeterReading saved = repository.save(entity);

        log.info("Successfully saved reading ID: {} for meter: {} with consumption: {} kWh",
                saved.getReadingId(), saved.getMeterId(), saved.getConsumptionKwh());

        return mapper.toResponse(saved);
    }

    private void validateReadingRequest(MeterReadingRequest request) {
        if (request.getConsumptionKwh().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidReadingException(
                    "consumptionKwh",
                    request.getConsumptionKwh(),
                    "consumption must be greater than 0"
            );
        }
        if (request.getReadingDate().isAfter(LocalDateTime.now())) {
            throw new InvalidReadingException(
                    "reading date",
                    request.getReadingDate(),
                    "reading date cannot be in future"
            );
        }
        if (!isValidMeterIdFormat(request.getMeterId())) {
            throw new InvalidReadingException(
                    "meterId",
                    request.getMeterId(),
                    "Meter ID must follow format: PROVIDER-TYPE-XXXXX (e.g., KPLC-SM-00001)"
            );
        }
        if (!isValidCitizenIdFormat(request.getCitizenId())) {
            throw new InvalidReadingException(
                    "citizenId",
                    request.getCitizenId(),
                    "Citizen ID must follow format: CIT-PROVIDER-XXXXX (e.g., CIT-KPLC-00001)"
            );
        }
    }

    private boolean isValidMeterIdFormat(String meterId) {
        String pattern = "^(KPLC|TANESCO|UMEME)-(SM|MN)-\\d{5}$";
        return meterId != null && meterId.matches(pattern);
    }

    private boolean isValidCitizenIdFormat(String citizenId) {
        String pattern = "^CIT-(KPLC|TANESCO|UMEME)-\\d{5}$";
        return citizenId != null && citizenId.matches(pattern);
    }

    private void validateReadingProgression(MeterReadingRequest request) {
        List<MeterReading> previousReadings = repository.findByMeterId(request.getMeterId());
        if (!previousReadings.isEmpty()) {
            previousReadings.sort((a, b) -> b.getReadingDate().compareTo(a.getReadingDate()));
            MeterReading lastReading = previousReadings.get(0);

            if (request.getConsumptionKwh().compareTo(lastReading.getConsumptionKwh()) <= 0) {
                throw new ReadingProgressionException(
                        request.getMeterId(),
                        request.getConsumptionKwh(),
                        lastReading.getConsumptionKwh()
                );
            }
        }
    }

    @Override
    public MeterReadingResponse getReadingById(Long readingId) {
        log.info("Fetching reading by ID: {}", readingId);
        MeterReading entity = repository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("meterReading", "readingId", readingId));
        return mapper.toResponse(entity);
    }

@Override
    public Page<MeterReadingResponse> getAllReadings(Pageable pageable){
        log.info("fetching all readings - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize()
        );
        Page<MeterReading> entityPage = repository.findAll(pageable);
       return entityPage.map(mapper::toResponse);

}
@Override
    public Page<MeterReadingResponse>getReadingsByMeterId(String meterId, Pageable pageable){
        log.info("Fetching readings for meter: {}", meterId);
    Page<MeterReading> entityPage = repository.findByMeterId(meterId, pageable);
    return entityPage.map(mapper::toResponse);

}
@Override
    public Page<MeterReadingResponse>getReadingsByProvider(ProviderName providerName, Pageable pageable){
        log.info("Fetching readings for provider: {}", providerName);
        Page<MeterReading> entityPage = repository.findByProviderName(providerName,pageable);
        return entityPage.map(mapper::toResponse);

}
@Override
    public Page<MeterReadingResponse>getReadingsByCitizenId(String citizenId, Pageable pageable){
        log.info("Fetching readings for citizen: {}", citizenId);
        Page<MeterReading> entityPage = repository.findByCitizenId(citizenId,pageable);
        return entityPage.map(mapper::toResponse);
}
@Override
    public List<MeterReadingResponse>getReadingsByDateRange(LocalDateTime startDate, LocalDateTime endDate){
        log.info("Fetching readings between {} and {}", startDate,endDate);
        List<MeterReading> entities = repository.findByDateRange(startDate,endDate);
        return mapper.toResponseList(entities);
}
@Override
    public Long countReadingsByProvider(ProviderName providerName){
        log.info("Counting readings for provider: {}", providerName);
        return repository.countByProviderName(providerName);
}

}

