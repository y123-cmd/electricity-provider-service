package com.imbank.smartgrid.electricityproviderservice.repository;

import com.imbank.smartgrid.electricityproviderservice.entity.MeterReading;
import com.imbank.smartgrid.electricityproviderservice.entity.ProviderName;
import com.imbank.smartgrid.electricityproviderservice.entity.ReadingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MeterReadingRepositoryTest {

    @Autowired
    private MeterReadingRepository meterReadingRepository;

    private MeterReading testReading;

    @BeforeEach
    void setUp() {
        testReading = new MeterReading();
        testReading.setMeterId("KPLC-SM-00001");
        testReading.setCitizenId("CIT-KPLC-00001");
        testReading.setProviderName(ProviderName.KPLC);
        testReading.setReadingType(ReadingType.AUTOMATED);
        testReading.setConsumptionKwh(new BigDecimal("150.50"));
        testReading.setReadingDate(LocalDateTime.now());
    }

    @Test
    void shouldSaveAndFindMeterReading() {

        MeterReading saved = meterReadingRepository.save(testReading);


        assertNotNull(saved.getReadingId());

        MeterReading found = meterReadingRepository.findById(saved.getReadingId()).orElse(null);
        assertNotNull(found);
        assertEquals("KPLC-SM-00001", found.getMeterId());
        assertEquals(new BigDecimal("150.50"), found.getConsumptionKwh());
    }

    @Test
    void shouldFindByMeterId() {
        meterReadingRepository.save(testReading);


        List<MeterReading> readings = meterReadingRepository.findByMeterId("KPLC-SM-00001");


        assertFalse(readings.isEmpty());
        assertEquals("KPLC-SM-00001", readings.get(0).getMeterId());
    }

    @Test
    void shouldCountByProviderName() {

        meterReadingRepository.save(testReading);

        Long count = meterReadingRepository.countByProviderName(ProviderName.KPLC);

        assertTrue(count > 0);
    }

    @Test
    void shouldFindByProviderNameWithPagination() {

        meterReadingRepository.save(testReading);

        MeterReading meterReading2 = new MeterReading();
        meterReading2.setMeterId("KPLC-SM-00002");
        meterReading2.setCitizenId("CIT-KPLC-00002");
        meterReading2.setProviderName(ProviderName.KPLC);
        meterReading2.setReadingType(ReadingType.MANUAL);
        meterReading2.setConsumptionKwh(new BigDecimal("200.00"));
        meterReading2.setReadingDate(LocalDateTime.now());
        meterReadingRepository.save(meterReading2);


        MeterReading meterReading3 = new MeterReading();
        meterReading3.setMeterId("KPLC-SM-00003");
        meterReading3.setCitizenId("CIT-KPLC-00003");
        meterReading3.setProviderName(ProviderName.KPLC);
        meterReading3.setReadingType(ReadingType.AUTOMATED);
        meterReading3.setConsumptionKwh(new BigDecimal("175.50"));
        meterReading3.setReadingDate(LocalDateTime.now());
        meterReadingRepository.save(meterReading3);

        PageRequest pageable = PageRequest.of(1, 2);
        Page<MeterReading> page = meterReadingRepository.findByProviderName(ProviderName.KPLC, pageable);

        assertEquals(1, page.getContent().size());
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(ProviderName.KPLC, page.getContent().get(0).getProviderName());
    }
    @Test
    void shouldFindByCitizenIdWithPagination() {

        meterReadingRepository.save(testReading);

        MeterReading reading2 = new MeterReading();
        reading2.setMeterId("KPLC-SM-00001");
        reading2.setCitizenId("CIT-KPLC-00001");
        reading2.setProviderName(ProviderName.KPLC);
        reading2.setReadingType(ReadingType.MANUAL);
        reading2.setConsumptionKwh(new BigDecimal("160.00"));
        reading2.setReadingDate(LocalDateTime.now().plusDays(1));
        meterReadingRepository.save(reading2);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<MeterReading> page = meterReadingRepository.findByCitizenId("CIT-KPLC-00001", pageable);


        assertEquals(2, page.getTotalElements());
        assertEquals(2, page.getContent().size());
        assertEquals("CIT-KPLC-00001", page.getContent().get(0).getCitizenId());
    }
    @Test
    void shouldFindByMeterIdWithPagination() {

        meterReadingRepository.save(testReading);

        MeterReading reading2 = new MeterReading();
        reading2.setMeterId("KPLC-SM-00001");
        reading2.setCitizenId("CIT-KPLC-00001");
        reading2.setProviderName(ProviderName.KPLC);
        reading2.setReadingType(ReadingType.MANUAL);
        reading2.setConsumptionKwh(new BigDecimal("165.00"));
        reading2.setReadingDate(LocalDateTime.now().plusDays(1));
        meterReadingRepository.save(reading2);

        MeterReading reading3 = new MeterReading();
        reading3.setMeterId("KPLC-SM-00001");
        reading3.setCitizenId("CIT-KPLC-00001");
        reading3.setProviderName(ProviderName.KPLC);
        reading3.setReadingType(ReadingType.AUTOMATED);
        reading3.setConsumptionKwh(new BigDecimal("180.00"));
        reading3.setReadingDate(LocalDateTime.now().plusDays(2));
        meterReadingRepository.save(reading3);

        PageRequest pageable = PageRequest.of(0, 2);
        Page<MeterReading> page = meterReadingRepository.findByMeterId("KPLC-SM-00001", pageable);

        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getContent().size());
        assertEquals(2, page.getTotalPages());
        assertEquals("KPLC-SM-00001", page.getContent().get(0).getMeterId());
    }
    @Test
    void shouldFindByDateRange(){
        LocalDateTime now = LocalDateTime.now();
        testReading.setReadingDate(now.minusDays(2));
        meterReadingRepository.save(testReading);

        MeterReading reading2 = new MeterReading();
        reading2.setMeterId("KPLC-SM-00002");
        reading2.setCitizenId("CIT-KPLC-00002");
        reading2.setProviderName(ProviderName.KPLC);
        reading2.setReadingType(ReadingType.MANUAL);
        reading2.setConsumptionKwh(new BigDecimal("180.00"));
        reading2.setReadingDate(now);
        meterReadingRepository.save(reading2);

        MeterReading reading3 = new MeterReading();
        reading3.setMeterId("KPLC-SM-00003");
        reading3.setCitizenId("CIT-KPLC-00003");
        reading3.setProviderName(ProviderName.TANESCO);
        reading3.setReadingType(ReadingType.AUTOMATED);
        reading3.setConsumptionKwh(new BigDecimal("190.00"));
        reading3.setReadingDate(now.plusDays(2));
        meterReadingRepository.save(reading3);

        LocalDateTime startDate = now.minusDays(1);
        LocalDateTime endDate = now.plusDays(1);
        List<MeterReading> readings = meterReadingRepository.findByDateRange(startDate, endDate);
    }
    @Test
    void shouldFindByMeterIdAndDateRange() {

        LocalDateTime now = LocalDateTime.now();

        testReading.setReadingDate(now.minusDays(5));
        meterReadingRepository.save(testReading);

        MeterReading reading2 = new MeterReading();
        reading2.setMeterId("KPLC-SM-00001");
        reading2.setCitizenId("CIT-KPLC-00001");
        reading2.setProviderName(ProviderName.KPLC);
        reading2.setReadingType(ReadingType.MANUAL);
        reading2.setConsumptionKwh(new BigDecimal("160.00"));
        reading2.setReadingDate(now.minusDays(3));
        meterReadingRepository.save(reading2);

        MeterReading reading3 = new MeterReading();
        reading3.setMeterId("KPLC-SM-00001");
        reading3.setCitizenId("CIT-KPLC-00001");
        reading3.setProviderName(ProviderName.KPLC);
        reading3.setReadingType(ReadingType.AUTOMATED);
        reading3.setConsumptionKwh(new BigDecimal("170.00"));
        reading3.setReadingDate(now);
        meterReadingRepository.save(reading3);


        LocalDateTime startDate = now.minusDays(4);
        LocalDateTime endDate = now.plusDays(1);
        List<MeterReading> readings = meterReadingRepository.findByMeterIdAndDateRange(
                "KPLC-SM-00001", startDate, endDate
        );


        assertEquals(2, readings.size());
        assertTrue(readings.get(0).getReadingDate().isAfter(readings.get(1).getReadingDate()) ||
                readings.get(0).getReadingDate().isEqual(readings.get(1).getReadingDate()));
    }



    }
