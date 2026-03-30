package com.imbank.smartgrid.electricityproviderservice.client;

import com.imbank.smartgrid.electricityproviderservice.client.request.CallbackRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "citizen-service")
public interface CitizenServiceClient {
    @PostMapping("/api/v1/callbacks/reading-confirmation")
    void sendCallback(
            @RequestHeader("X-Callback-Secret")String secret,
            @RequestBody CallbackRequest request);
}
