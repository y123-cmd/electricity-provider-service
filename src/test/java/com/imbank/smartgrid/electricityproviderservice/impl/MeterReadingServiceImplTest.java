package com.imbank.smartgrid.electricityproviderservice.service.impl;

import com.imbank.smartgrid.electricityproviderservice.dto.request.MeterReadingRequest;
import com.imbank.smartgrid.electricityproviderservice.dto.response.MeterReadingResponse;
import com.imbank.smartgrid.electricityproviderservice.entity.MeterReading;
import com.imbank.smartgrid.electricityproviderservice.entity.ProviderName;
import com.imbank.smartgrid.electricityproviderservice.entity.ReadingType;
import com.imbank.smartgrid.electricityproviderservice.exception.InvalidReadingException;
import com.imbank.smartgrid.electricityproviderservice.exception.ReadingProgressionException;
import com.imbank.smartgrid.electricityproviderservice.exception.ResourceNotFoundException;
import com.imbank.smartgrid.electricityproviderservice.mapper.MeterReadingMapper;
import com.imbank.smartgrid.electricityproviderservice.repository.MeterReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeterReadingService Implementation Tests")
class MeterReadingServiceImplTest {

    @Mock
    private MeterReadingRepository repository;

    @Mock
    private MeterReadingMapper mapper;

    @InjectMocks
    private MeterReadingServiceImpl service;

    private MeterReadingRequest validRequest;
    private MeterReading validEntity;
    private MeterReadingResponse validResponse;

    @BeforeEach
    void setUp() {
        validRequest = new MeterReadingRequest();
        validRequest.setMeterId("KPLC-SM-00001");
        validRequest.setCitizenId("CIT-KPLC-00001");
        validRequest.setProviderName(ProviderName.KPLC);
        validRequest.setReadingType(ReadingType.AUTOMATED);
        validRequest.setConsumptionKwh(new BigDecimal("150.50"));
        validRequest.setReadingDate(LocalDateTime.now().minusHours(1));

        validEntity = new MeterReading();
        validEntity.setReadingId(1L);
        validEntity.setMeterId("KPLC-SM-00001");
        validEntity.setCitizenId("CIT-KPLC-00001");
        validEntity.setProviderName(ProviderName.KPLC);
        validEntity.setReadingType(ReadingType.AUTOMATED);
        validEntity.setConsumptionKwh(new BigDecimal("150.50"));
        validEntity.setReadingDate(LocalDateTime.now().minusHours(1));
        validEntity.setCreatedAt(LocalDateTime.now());

        validResponse = new MeterReadingResponse();
        validResponse.setReadingId(1L);
        validResponse.setMeterId("KPLC-SM-00001");
        validResponse.setCitizenId("CIT-KPLC-00001");
        validResponse.setProviderName(ProviderName.KPLC);
        validResponse.setReadingType(ReadingType.AUTOMATED);
        validResponse.setConsumptionKwh(new BigDecimal("150.50"));
        validResponse.setReadingDate(LocalDateTime.now().minusHours(1));
        validResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should successfully save reading when all validations pass")
    void saveReading_ShouldReturnResponse_WhenValidRequest() {
        when(repository.findTopByMeterIdOrderByReadingDateDesc(validRequest.getMeterId()))
                .thenReturn(Optional.empty());
        when(mapper.toEntity(validRequest))
                .thenReturn(validEntity);
        when(repository.save(validEntity))
                .thenReturn(validEntity);
        when(mapper.toResponse(validEntity))
                .thenReturn(validResponse);

        MeterReadingResponse result = service.saveReading(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getReadingId()).isEqualTo(1L);
        assertThat(result.getMeterId()).isEqualTo("KPLC-SM-00001");
        assertThat(result.getConsumptionKwh()).isEqualTo(new BigDecimal("150.50"));

        verify(repository).findTopByMeterIdOrderByReadingDateDesc(validRequest.getMeterId());
        verify(mapper).toEntity(validRequest);
        verify(repository).save(validEntity);
        verify(mapper).toResponse(validEntity);
    }

    @Test
    @DisplayName("Should throw exception when consumption is negative")
    void saveReading_ShouldThrowException_WhenConsumptionIsNegative() {
        validRequest.setConsumptionKwh(new BigDecimal("-10.50"));

        assertThatThrownBy(() -> service.saveReading(validRequest))
                .isInstanceOf(InvalidReadingException.class)
                .hasMessageContaining("consumption must be greater than 0");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when consumption is zero")
    void saveReading_ShouldThrowException_WhenConsumptionIsZero() {
        validRequest.setConsumptionKwh(BigDecimal.ZERO);

        assertThatThrownBy(() -> service.saveReading(validRequest))
                .isInstanceOf(InvalidReadingException.class)
                .hasMessageContaining("consumption must be greater than 0");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when reading date is in future")
    void saveReading_ShouldThrowException_WhenDateIsInFuture() {
        validRequest.setReadingDate(LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> service.saveReading(validRequest))
                .isInstanceOf(InvalidReadingException.class)
                .hasMessageContaining("reading date cannot be in future");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when meter ID format is invalid")
    void saveReading_ShouldThrowException_WhenMeterIdFormatInvalid() {
        validRequest.setMeterId("INVALID-FORMAT");

        assertThatThrownBy(() -> service.saveReading(validRequest))
                .isInstanceOf(InvalidReadingException.class)
                .hasMessageContaining("Meter ID must follow format");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when citizen ID format is invalid")
    void saveReading_ShouldThrowException_WhenCitizenIdFormatInvalid() {
        validRequest.setCitizenId("INVALID-ID");

        assertThatThrownBy(() -> service.saveReading(validRequest))
                .isInstanceOf(InvalidReadingException.class)
                .hasMessageContaining("Citizen ID must follow format");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when provider doesn't match meter ID")
    void saveReading_ShouldThrowException_WhenProviderMismatch() {
        validRequest.setMeterId("KPLC-SM-00001");
        validRequest.setProviderName(ProviderName.TANESCO);

        assertThatThrownBy(() -> service.saveReading(validRequest))
                .isInstanceOf(InvalidReadingException.class)
                .hasMessageContaining("Meter ID does not match provider name");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when new reading is not greater than previous")
    void saveReading_ShouldThrowException_WhenReadingNotIncreasing() {
        MeterReading previousReading = new MeterReading();
        previousReading.setConsumptionKwh(new BigDecimal("200.00"));

        validRequest.setConsumptionKwh(new BigDecimal("150.00"));

        when(repository.findTopByMeterIdOrderByReadingDateDesc(validRequest.getMeterId()))
                .thenReturn(Optional.of(previousReading));

        assertThatThrownBy(() -> service.saveReading(validRequest))
                .isInstanceOf(ReadingProgressionException.class);

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when new reading equals previous")
    void saveReading_ShouldThrowException_WhenReadingEqualsPrevi() {
        MeterReading previousReading = new MeterReading();
        previousReading.setConsumptionKwh(new BigDecimal("150.50"));

        validRequest.setConsumptionKwh(new BigDecimal("150.50"));

        when(repository.findTopByMeterIdOrderByReadingDateDesc(validRequest.getMeterId()))
                .thenReturn(Optional.of(previousReading));

        assertThatThrownBy(() -> service.saveReading(validRequest))
                .isInstanceOf(ReadingProgressionException.class);

        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("Should return reading when ID exists")
    void getReadingById_ShouldReturnResponse_WhenReadingExists() {
        when(repository.findById(1L))
                .thenReturn(Optional.of(validEntity));
        when(mapper.toResponse(validEntity))
                .thenReturn(validResponse);

        MeterReadingResponse result = service.getReadingById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getReadingId()).isEqualTo(1L);

        verify(repository).findById(1L);
        verify(mapper).toResponse(validEntity);
    }

    @Test
    @DisplayName("Should throw exception when reading not found")
    void getReadingById_ShouldThrowException_WhenReadingNotFound() {
        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReadingById(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository).findById(999L);
        verify(mapper, never()).toResponse(any());
    }



    @Test
    @DisplayName("Should return paginated readings")
    void getAllReadings_ShouldReturnPagedResponse_WhenCalled() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MeterReading> entityPage = new PageImpl<>(List.of(validEntity));

        when(repository.findAll(pageable))
                .thenReturn(entityPage);
        when(mapper.toResponse(validEntity))
                .thenReturn(validResponse);

        Page<MeterReadingResponse> result = service.getAllReadings(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(repository).findAll(pageable);
    }

    @Test
    @DisplayName("Should return readings for specific meter")
    void getReadingsByMeterId_ShouldReturnPagedResponse_WhenMeterExists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MeterReading> entityPage = new PageImpl<>(List.of(validEntity));

        when(repository.findByMeterId("KPLC-SM-00001", pageable))
                .thenReturn(entityPage);
        when(mapper.toResponse(validEntity))
                .thenReturn(validResponse);

        Page<MeterReadingResponse> result = service.getReadingsByMeterId("KPLC-SM-00001", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(repository).findByMeterId("KPLC-SM-00001", pageable);
    }

    @Test
    @DisplayName("Should return readings for specific provider")
    void getReadingsByProvider_ShouldReturnPagedResponse_WhenProviderExists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MeterReading> entityPage = new PageImpl<>(List.of(validEntity));

        when(repository.findByProviderName(ProviderName.KPLC, pageable))
                .thenReturn(entityPage);
        when(mapper.toResponse(validEntity))
                .thenReturn(validResponse);

        Page<MeterReadingResponse> result = service.getReadingsByProvider(ProviderName.KPLC, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(repository).findByProviderName(ProviderName.KPLC, pageable);
    }


    @Test
    @DisplayName("Should return readings for specific citizen")
    void getReadingsByCitizenId_ShouldReturnPagedResponse_WhenCitizenExists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MeterReading> entityPage = new PageImpl<>(List.of(validEntity));

        when(repository.findByCitizenId("CIT-KPLC-00001", pageable))
                .thenReturn(entityPage);
        when(mapper.toResponse(validEntity))
                .thenReturn(validResponse);

        Page<MeterReadingResponse> result = service.getReadingsByCitizenId("CIT-KPLC-00001", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(repository).findByCitizenId("CIT-KPLC-00001", pageable);
    }


    @Test
    @DisplayName("Should return readings within date range")
    void getReadingsByDateRange_ShouldReturnList_WhenValidDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<MeterReading> entities = List.of(validEntity);

        when(repository.findByDateRange(startDate, endDate))
                .thenReturn(entities);
        when(mapper.toResponseList(entities))
                .thenReturn(List.of(validResponse));

        List<MeterReadingResponse> result = service.getReadingsByDateRange(startDate, endDate);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(repository).findByDateRange(startDate, endDate);
        verify(mapper).toResponseList(entities);
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date")
    void getReadingsByDateRange_ShouldThrowException_WhenInvalidDateRange() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(7);

        assertThatThrownBy(() -> service.getReadingsByDateRange(startDate, endDate))
                .isInstanceOf(InvalidReadingException.class)
                .hasMessageContaining("start date cannot be after end date");

        verify(repository, never()).findByDateRange(any(), any());
    }

    @Test
    @DisplayName("Should return count for provider")
    void countReadingsByProvider_ShouldReturnCount_WhenProviderExists() {
        when(repository.countByProviderName(ProviderName.KPLC))
                .thenReturn(100L);

        Long result = service.countReadingsByProvider(ProviderName.KPLC);

        assertThat(result).isEqualTo(100L);

        verify(repository).countByProviderName(ProviderName.KPLC);
    }

    @Test
    @DisplayName("Should return average consumption when data exists")
    void getAverageConsumption_ShouldReturnAverage_WhenDataExists() {
        when(repository.averageConsumptionByProvider(ProviderName.KPLC))
                .thenReturn(new BigDecimal("125.75"));

        BigDecimal result = service.getAverageConsumption(ProviderName.KPLC);

        assertThat(result).isEqualTo(new BigDecimal("125.75"));

        verify(repository).averageConsumptionByProvider(ProviderName.KPLC);
    }

    @Test
    @DisplayName("Should return zero when no data exists")
    void getAverageConsumption_ShouldReturnZero_WhenNoDataExists() {
        when(repository.averageConsumptionByProvider(ProviderName.KPLC))
                .thenReturn(null);

        BigDecimal result = service.getAverageConsumption(ProviderName.KPLC);

        assertThat(result).isEqualTo(BigDecimal.ZERO);

        verify(repository).averageConsumptionByProvider(ProviderName.KPLC);
    }
}