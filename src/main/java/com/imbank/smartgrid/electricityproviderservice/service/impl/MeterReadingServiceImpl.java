package com.imbank.smartgrid.electricityproviderservice.service.impl;

import com.imbank.smartgrid.electricityproviderservice.client.CitizenServiceClient;
import com.imbank.smartgrid.electricityproviderservice.client.request.CallbackRequest;
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
import org.springframework.beans.factory.annotation.Value;
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

    private static final String METER_ID_PATTERN = "^(KPLC|TANESCO|UMEME)-(SM|MN)-\\d{5}$";
    private static final String CITIZEN_ID_PATTERN = "^CIT-(KPLC|TANESCO|UMEME)-\\d{5}$";

    private final MeterReadingRepository repository;
    private final MeterReadingMapper mapper;
    private final CitizenServiceClient citizenServiceClient;

    @Value("${callback.secret}")
    private String callbackSecret;


    @Override
    public MeterReadingResponse saveReading(MeterReadingRequest request) {
        log.info("Processing meter reading | meterId={} | citizenId={} | provider={} | consumption={}kWh",
                request.getMeterId(),
                request.getCitizenId(),
                request.getProviderName(),
                request.getConsumptionKwh());

        validateReadingRequest(request);
        validateReadingProgression(request);

        MeterReading entity = mapper.toEntity(request);
        MeterReading saved = repository.save(entity);

        try{
            CallbackRequest callbackRequest = new CallbackRequest(
                    saved.getCitizenId(),
                    saved.getMeterId(),
                    saved.getProviderName().name(),
                    "SUCCESS",
                    "Reading Saved Successfully"
            );
            citizenServiceClient.sendCallback(callbackSecret, callbackRequest);
            log.info("callback sent successfully for citizenId: {}", saved.getCitizenId());
        }catch (Exception e){
            log.warn("failed to send callback for citizenId: {} - {}",saved.getCitizenId(),e.getMessage());
        }

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
        validateProviderConsistency(request);
    }

    private boolean isValidMeterIdFormat(String meterId) {
        return meterId != null && meterId.matches(METER_ID_PATTERN);
    }

    private boolean isValidCitizenIdFormat(String citizenId) {
        return citizenId != null && citizenId.matches(CITIZEN_ID_PATTERN);
    }

    private void validateProviderConsistency(MeterReadingRequest request) {
        if (!request.getMeterId().startsWith(request.getProviderName().name())) {
            throw new InvalidReadingException(
                    "providerMismatch",
                    request.getMeterId(),
                    "Meter ID does not match provider name"
            );
        }
    }

    private void validateReadingProgression(MeterReadingRequest request) {
        repository.findTopByMeterIdOrderByReadingDateDesc(request.getMeterId())
                .ifPresent(lastReading -> {
                    if (request.getConsumptionKwh().compareTo(lastReading.getConsumptionKwh()) <= 0) {
                        throw new ReadingProgressionException(
                                request.getMeterId(),
                                request.getConsumptionKwh(),
                                lastReading.getConsumptionKwh()
                        );
                    }
                });
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

        if(startDate.isAfter(endDate)){
            throw new InvalidReadingException(
                    "dataRange",
                    startDate,
                    "start date cannot be after end date"
            );
        }

        List<MeterReading> entities = repository.findByDateRange(startDate,endDate);
        return mapper.toResponseList(entities);
}
@Override
    public Long countReadingsByProvider(ProviderName providerName){
        log.info("Counting readings for provider: {}", providerName);
        return repository.countByProviderName(providerName);
}
    @Override
    public BigDecimal getAverageConsumption(ProviderName providerName) {
        log.info("Calculating average consumption for provider: {}", providerName);
        BigDecimal average = repository.averageConsumptionByProvider(providerName);
        return average != null ? average : BigDecimal.ZERO;

    }
    @Override
    public List<MeterReadingResponse> saveReadingsBatch(List<MeterReadingRequest> requests) {
        log.info("Processing batch meter readings | total readings={}", requests.size());

        for (MeterReadingRequest request : requests) {
            validateReadingRequest(request);
            validateReadingProgression(request);
        }


        List<MeterReading> entities = requests.stream()
                .map(mapper::toEntity)
                .toList();


        List<MeterReading> savedEntities = repository.saveAll(entities);

        log.info("Successfully saved {} meter readings in batch", savedEntities.size());


        return mapper.toResponseList(savedEntities);
    }


}

