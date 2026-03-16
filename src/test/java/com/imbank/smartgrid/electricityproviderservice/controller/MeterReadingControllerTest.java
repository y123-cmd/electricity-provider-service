package com.imbank.smartgrid.electricityproviderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imbank.smartgrid.electricityproviderservice.dto.request.MeterReadingRequest;
import com.imbank.smartgrid.electricityproviderservice.dto.response.MeterReadingResponse;
import com.imbank.smartgrid.electricityproviderservice.entity.ProviderName;
import com.imbank.smartgrid.electricityproviderservice.entity.ReadingType;
import com.imbank.smartgrid.electricityproviderservice.service.MeterReadingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeterReadingController.class)
@ActiveProfiles("test")
public class MeterReadingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeterReadingService meterReadingService;

    private MeterReadingResponse buildReading() {
        return new MeterReadingResponse(
                1L,
                "KPLC-SM-00001",
                "CIT-KPLC-00001",
                ProviderName.KPLC,
                new BigDecimal("120.50"),
                LocalDateTime.now().minusDays(1),
                ReadingType.MANUAL,
                LocalDateTime.now()
        );
    }

    private MeterReadingRequest buildReadingRequest() {
        MeterReadingRequest request = new MeterReadingRequest();
        request.setMeterId("KPLC-SM-00001");
        request.setCitizenId("CIT-KPLC-00001");
        request.setProviderName(ProviderName.KPLC);
        request.setConsumptionKwh(new BigDecimal("120.50"));
        request.setReadingType(ReadingType.MANUAL);
        request.setReadingDate(LocalDateTime.now().minusDays(1));
        return request;
    }

    @Test
    @WithMockUser
    void createReading_ShouldReturn201() throws Exception {
        MeterReadingRequest request = buildReadingRequest();
        when(meterReadingService.saveReading(any(MeterReadingRequest.class)))
                .thenReturn(buildReading());

        mockMvc.perform(post("/api/v1/meter-readings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.meterId").value("KPLC-SM-00001"))
                .andExpect(jsonPath("$.data.consumptionKwh").value(120.50));
    }

    @Test
    @WithMockUser
    void getReadingById_ShouldReturn200() throws Exception {
        when(meterReadingService.getReadingById(1L)).thenReturn(buildReading());

        mockMvc.perform(get("/api/v1/meter-readings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meterId").value("KPLC-SM-00001"));
    }

    @Test
    @WithMockUser
    void getAllReadings_ShouldReturnPaginatedReadings() throws Exception {
        List<MeterReadingResponse> readings = List.of(buildReading());
        Page<MeterReadingResponse> page = new PageImpl<>(readings, PageRequest.of(0, 10), readings.size());

        when(meterReadingService.getAllReadings(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/meter-readings")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].meterId").value("KPLC-SM-00001"))
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.size").value(10))
                .andExpect(jsonPath("$.pagination.totalRecords").value(1));
    }

    @Test
    @WithMockUser
    void getReadingsByMeterId_ShouldReturnPaginatedReadings() throws Exception {
        List<MeterReadingResponse> readings = List.of(buildReading());
        Page<MeterReadingResponse> page = new PageImpl<>(readings, PageRequest.of(0, 10), readings.size());

        when(meterReadingService.getReadingsByMeterId(eq("KPLC-SM-00001"), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/meter-readings/meter/KPLC-SM-00001")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].meterId").value("KPLC-SM-00001"))
                .andExpect(jsonPath("$.pagination.totalRecords").value(1));
    }

    @Test
    @WithMockUser
    void getReadingsByProvider_ShouldReturnPaginatedReadings() throws Exception {
        List<MeterReadingResponse> readings = List.of(buildReading());
        Page<MeterReadingResponse> page = new PageImpl<>(readings, PageRequest.of(0, 10), readings.size());

        when(meterReadingService.getReadingsByProvider(eq(ProviderName.KPLC), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/meter-readings/provider/KPLC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].meterId").value("KPLC-SM-00001"))
                .andExpect(jsonPath("$.pagination.totalRecords").value(1));
    }

    @Test
    @WithMockUser
    void getReadingsByCitizenId_ShouldReturnPaginatedReadings() throws Exception {
        List<MeterReadingResponse> readings = List.of(buildReading());
        Page<MeterReadingResponse> page = new PageImpl<>(readings, PageRequest.of(0, 10), readings.size());

        when(meterReadingService.getReadingsByCitizenId(eq("CIT-KPLC-00001"), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/meter-readings/citizen/CIT-KPLC-00001")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].meterId").value("KPLC-SM-00001"))
                .andExpect(jsonPath("$.pagination.totalRecords").value(1));
    }

    @Test
    @WithMockUser
    void getReadingsByDateRange_ShouldReturnReadings() throws Exception {
        List<MeterReadingResponse> readings = List.of(buildReading());
        when(meterReadingService.getReadingsByDateRange(any(), any())).thenReturn(readings);

        mockMvc.perform(get("/api/v1/meter-readings/range")
                        .param("startDate", LocalDateTime.now().minusDays(5).toString())
                        .param("endDate", LocalDateTime.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].meterId").value("KPLC-SM-00001"));
    }

    @Test
    @WithMockUser
    void getAverageConsumption_ShouldReturnValue() throws Exception {
        when(meterReadingService.getAverageConsumption(ProviderName.KPLC)).thenReturn(new BigDecimal("120.50"));

        mockMvc.perform(get("/api/v1/meter-readings/average/KPLC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(120.50));
    }

    @Test
    @WithMockUser
    void countReadingsByProvider_ShouldReturnValue() throws Exception {
        when(meterReadingService.countReadingsByProvider(ProviderName.KPLC)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/meter-readings/count/KPLC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(5));
    }
}