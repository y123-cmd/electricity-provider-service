package com.imbank.smartgrid.electricityproviderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meter_reading")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class MeterReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long readingId;

    @Column(nullable = false,length = 20)
    private String meterId;

    @Column(nullable = false,length = 20)
    private String citizenId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private ProviderName providerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private ReadingType readingType;

}
