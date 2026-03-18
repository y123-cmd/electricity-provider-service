package com.imbank.smartgrid.electricityproviderservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void meterReadingEndpoint_ShouldBeAccessible_WithoutAuthentication()throws Exception{
        mockMvc.perform(get("/api/v1/meter-readings"))
                .andExpect(status().isOk());
    }
    @Test
    void postRequest_ShouldNotReturn403_WhenCSRFIsDisabled()throws Exception{
        mockMvc.perform(post("/api/v1/meter-readings")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

    }
    @Test
    void swaggerUi_ShouldBeAccessible_WithoutAuthentication()throws Exception{
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
