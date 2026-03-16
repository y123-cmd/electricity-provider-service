package com.imbank.smartgrid.electricityproviderservice.controller;

import com.imbank.smartgrid.electricityproviderservice.dto.request.MeterReadingRequest;
import com.imbank.smartgrid.electricityproviderservice.dto.response.ApiResponse;
import com.imbank.smartgrid.electricityproviderservice.dto.response.MeterReadingResponse;
import com.imbank.smartgrid.electricityproviderservice.dto.response.Pagination;
import com.imbank.smartgrid.electricityproviderservice.entity.ProviderName;
import com.imbank.smartgrid.electricityproviderservice.service.MeterReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/meter-readings")
@RequiredArgsConstructor
@Tag(name = "Meter Readings", description = "APIs for managing electricity meter readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;


    @Operation(summary = "Create a new meter reading", description = "Saves a new electricity meter reading submitted by a smart meter or citizen")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Meter reading created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<MeterReadingResponse>> createReading(
            @Valid @RequestBody MeterReadingRequest request) {

        MeterReadingResponse response = meterReadingService.saveReading(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Meter reading created successfully", response));
    }


    @Operation(summary = "Create multiple meter readings", description = "Saves multiple meter readings submitted in batch")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Meter readings created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<MeterReadingResponse>>> createReadingsBatch(
            @Valid @RequestBody List<MeterReadingRequest> requests) {

        List<MeterReadingResponse> responses = meterReadingService.saveReadingsBatch(requests);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Meter readings created successfully", responses));
    }

    @Operation(summary = "Get meter reading by ID", description = "Retrieves a single meter reading by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reading found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reading not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MeterReadingResponse>> getReadingById(
            @Parameter(description = "Meter reading ID") @PathVariable Long id) {

        MeterReadingResponse response = meterReadingService.getReadingById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Meter reading retrieved successfully", response));
    }


    @Operation(summary = "Get all meter readings", description = "Retrieves all meter readings with pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Readings retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<MeterReadingResponse>>> getAllReadings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MeterReadingResponse> readingPage = meterReadingService.getAllReadings(pageable);

        Pagination pagination = new Pagination(
                readingPage.getNumber(),
                readingPage.getSize(),
                readingPage.getTotalElements(),
                readingPage.getTotalPages()
        );

        return ResponseEntity.ok(new ApiResponse<>(200, "Meter readings retrieved successfully", readingPage.getContent(), pagination));
    }

    @Operation(summary = "Get readings by meter ID", description = "Retrieves readings for a specific meter")
    @GetMapping("/meter/{meterId}")
    public ResponseEntity<ApiResponse<List<MeterReadingResponse>>> getReadingsByMeterId(
            @Parameter(description = "Meter ID") @PathVariable String meterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MeterReadingResponse> readingPage = meterReadingService.getReadingsByMeterId(meterId, pageable);

        Pagination pagination = new Pagination(
                readingPage.getNumber(),
                readingPage.getSize(),
                readingPage.getTotalElements(),
                readingPage.getTotalPages()
        );

        return ResponseEntity.ok(new ApiResponse<>(200, "Meter readings retrieved successfully", readingPage.getContent(), pagination));
    }

    // ---------------- GET READINGS BY PROVIDER ----------------
    @Operation(summary = "Get readings by provider", description = "Retrieves readings for a specific electricity provider")
    @GetMapping("/provider/{providerName}")
    public ResponseEntity<ApiResponse<List<MeterReadingResponse>>> getReadingsByProvider(
            @Parameter(description = "Provider name") @PathVariable ProviderName providerName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MeterReadingResponse> readingPage = meterReadingService.getReadingsByProvider(providerName, pageable);

        Pagination pagination = new Pagination(
                readingPage.getNumber(),
                readingPage.getSize(),
                readingPage.getTotalElements(),
                readingPage.getTotalPages()
        );

        return ResponseEntity.ok(new ApiResponse<>(200, "Meter readings retrieved successfully", readingPage.getContent(), pagination));
    }

    @Operation(summary = "Get readings by citizen ID", description = "Retrieves readings submitted by a specific citizen")
    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<ApiResponse<List<MeterReadingResponse>>> getReadingsByCitizenId(
            @Parameter(description = "Citizen ID") @PathVariable String citizenId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MeterReadingResponse> readingPage = meterReadingService.getReadingsByCitizenId(citizenId, pageable);

        Pagination pagination = new Pagination(
                readingPage.getNumber(),
                readingPage.getSize(),
                readingPage.getTotalElements(),
                readingPage.getTotalPages()
        );

        return ResponseEntity.ok(new ApiResponse<>(200, "Meter readings retrieved successfully", readingPage.getContent(), pagination));
    }


    @Operation(summary = "Get readings by date range", description = "Retrieves readings submitted within a date range")
    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<MeterReadingResponse>>> getReadingsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {

        List<MeterReadingResponse> readings = meterReadingService.getReadingsByDateRange(startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>(200, "Meter readings retrieved successfully", readings));
    }

    @Operation(summary = "Get average consumption", description = "Calculates the average consumption for a provider")
    @GetMapping("/average/{providerName}")
    public ResponseEntity<ApiResponse<BigDecimal>> getAverageConsumption(
            @PathVariable ProviderName providerName) {

        BigDecimal average = meterReadingService.getAverageConsumption(providerName);
        return ResponseEntity.ok(new ApiResponse<>(200, "Average consumption retrieved successfully", average));
    }


    @Operation(summary = "Count readings by provider", description = "Counts all readings for a specific provider")
    @GetMapping("/count/{providerName}")
    public ResponseEntity<ApiResponse<Long>> countReadingsByProvider(
            @PathVariable ProviderName providerName) {

        Long count = meterReadingService.countReadingsByProvider(providerName);
        return ResponseEntity.ok(new ApiResponse<>(200, "Reading count retrieved successfully", count));
    }

}