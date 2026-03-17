package com.imbank.smartgrid.electricityproviderservice.mapper;

import com.imbank.smartgrid.electricityproviderservice.dto.request.MeterReadingRequest;
import com.imbank.smartgrid.electricityproviderservice.dto.response.MeterReadingResponse;
import com.imbank.smartgrid.electricityproviderservice.entity.MeterReading;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MeterReadingMapper {
    public MeterReading toEntity(MeterReadingRequest request) {
        MeterReading entity = new MeterReading();
        entity.setMeterId(request.getMeterId());
        entity.setCitizenId(request.getCitizenId());
        entity.setProviderName(request.getProviderName());
        entity.setReadingType(request.getReadingType());
        entity.setConsumptionKwh(request.getConsumptionKwh());
        entity.setReadingDate(request.getReadingDate());
        return entity;
    }
    public MeterReadingResponse toResponse(MeterReading entity){
        MeterReadingResponse response = new MeterReadingResponse();
        response.setReadingId(entity.getReadingId());
        response.setMeterId(entity.getMeterId());
        response.setCitizenId(entity.getCitizenId());
        response.setProviderName(entity.getProviderName());
        response.setReadingType(entity.getReadingType());
        response.setConsumptionKwh(entity.getConsumptionKwh());
        response.setReadingDate(entity.getReadingDate());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
    public List<MeterReadingResponse> toResponseList(List<MeterReading> entities) {
        if (entities == null) {
            return null;
        }

        List<MeterReadingResponse> responseList = new ArrayList<>();

        for (MeterReading entity : entities) {
            responseList.add(toResponse(entity));
        }

        return responseList;
    }


}
