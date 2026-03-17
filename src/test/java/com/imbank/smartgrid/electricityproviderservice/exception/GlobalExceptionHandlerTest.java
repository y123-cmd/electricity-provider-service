package com.imbank.smartgrid.electricityproviderservice.exception;

import com.imbank.smartgrid.electricityproviderservice.controller.MeterReadingController;
import com.imbank.smartgrid.electricityproviderservice.service.MeterReadingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MeterReadingController.class)
@ActiveProfiles("test")
public class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeterReadingService meterReadingService;

    @Test
    @WithMockUser
    void shouldReturn400_WhenInvalidReadingExceptionThrown() throws Exception {
        when(meterReadingService.getReadingById(1L))
                .thenThrow(new InvalidReadingException("consumptionKwh",
                        BigDecimal.ZERO, "consumption must be greater than 0"));

        mockMvc.perform(get("/api/v1/meter-readings/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    @Test
    @WithMockUser
    void shouldReturn404_WhenResourceNotFoundThrown()throws Exception{
        when(meterReadingService.getReadingById(999L))
                .thenThrow(new ResourceNotFoundException("meterReading","readingId", 999L));
        mockMvc.perform(get("/api/v1/meter-readings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

    }
    @Test
    @WithMockUser
    void shouldReturn400_WhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/v1/meter-readings")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    }


