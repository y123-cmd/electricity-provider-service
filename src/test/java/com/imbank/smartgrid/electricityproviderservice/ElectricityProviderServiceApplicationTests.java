package com.imbank.smartgrid.electricityproviderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EnableFeignClients
class ElectricityProviderServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
