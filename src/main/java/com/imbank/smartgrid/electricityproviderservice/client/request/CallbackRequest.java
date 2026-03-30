package com.imbank.smartgrid.electricityproviderservice.client.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallbackRequest {
    private String meterId;
    private String citizenId;
    private String providerName;
    private String status;
    private String message;
}
