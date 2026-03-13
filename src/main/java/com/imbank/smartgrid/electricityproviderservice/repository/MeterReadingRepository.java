package com.imbank.smartgrid.electricityproviderservice.repository;

import com.imbank.smartgrid.electricityproviderservice.entity.MeterReading;
import com.imbank.smartgrid.electricityproviderservice.entity.ProviderName;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    List<MeterReading> findByMeterId(String meterId);
    Page<MeterReading> findByMeterId(String meterId, Pageable pageable);
    Page<MeterReading>findByProviderName(ProviderName providerName,Pageable pageable);
    Page<MeterReading>findByCitizenId(String citizenId,Pageable pageable);

    @Query("SELECT m FROM MeterReading m WHERE m.readingDate BETWEEN :startDate AND :endDate")
    List<MeterReading> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
    @Query("SELECT m FROM MeterReading m WHERE m.meterId = :meterId AND m.readingDate BETWEEN :startDate AND :endDate ORDER BY m.readingDate DESC")
    List<MeterReading> findByMeterIdAndDateRange(@Param("meterId") String meterId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);
    Long countByProviderName(ProviderName providerName);
}
