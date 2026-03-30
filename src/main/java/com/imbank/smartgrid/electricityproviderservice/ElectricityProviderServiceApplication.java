package com.imbank.smartgrid.electricityproviderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients

public class ElectricityProviderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectricityProviderServiceApplication.class, args);
    }

}
