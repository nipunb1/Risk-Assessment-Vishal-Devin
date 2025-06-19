package com.riskassessment.service;

import com.riskassessment.entity.Risk;
import com.riskassessment.repository.RiskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RiskServiceTest {

    @Mock
    private RiskRepository riskRepository;

    @InjectMocks
    private RiskService riskService;

    private Risk testRisk;

    @BeforeEach
    void setUp() {
        testRisk = new Risk(
            LocalDate.now(),
            Risk.RiskType.MARKET_PRACTICE,
            Risk.RiskProbability.HIGH,
            "Test risk description",
            Risk.RiskStatus.OPEN,
            "Test remarks"
        );
        testRisk.setRiskId(1L);
    }

    @Test
    void getAllRisks_ShouldReturnAllRisks() {
        List<Risk> expectedRisks = Arrays.asList(testRisk);
        when(riskRepository.findAll()).thenReturn(expectedRisks);

        List<Risk> actualRisks = riskService.getAllRisks();

        assertEquals(expectedRisks, actualRisks);
        verify(riskRepository).findAll();
    }

    @Test
    void getRiskById_WhenRiskExists_ShouldReturnRisk() {
        when(riskRepository.findById(1L)).thenReturn(Optional.of(testRisk));

        Optional<Risk> result = riskService.getRiskById(1L);

        assertTrue(result.isPresent());
        assertEquals(testRisk, result.get());
        verify(riskRepository).findById(1L);
    }

    @Test
    void getRiskById_WhenRiskNotExists_ShouldReturnEmpty() {
        when(riskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Risk> result = riskService.getRiskById(1L);

        assertFalse(result.isPresent());
        verify(riskRepository).findById(1L);
    }

    @Test
    void createRisk_ShouldSaveAndReturnRisk() {
        when(riskRepository.save(any(Risk.class))).thenReturn(testRisk);

        Risk result = riskService.createRisk(testRisk);

        assertEquals(testRisk, result);
        verify(riskRepository).save(testRisk);
    }

    @Test
    void updateRisk_WhenRiskExists_ShouldUpdateAndReturnRisk() {
        Risk updatedDetails = new Risk(
            LocalDate.now().plusDays(1),
            Risk.RiskType.PRICING,
            Risk.RiskProbability.MEDIUM,
            "Updated description",
            Risk.RiskStatus.IN_PROGRESS,
            "Updated remarks"
        );

        when(riskRepository.findById(1L)).thenReturn(Optional.of(testRisk));
        when(riskRepository.save(any(Risk.class))).thenReturn(testRisk);

        Risk result = riskService.updateRisk(1L, updatedDetails);

        assertEquals(updatedDetails.getRiskDate(), testRisk.getRiskDate());
        assertEquals(updatedDetails.getRiskType(), testRisk.getRiskType());
        assertEquals(updatedDetails.getRiskProbability(), testRisk.getRiskProbability());
        assertEquals(updatedDetails.getRiskDesc(), testRisk.getRiskDesc());
        assertEquals(updatedDetails.getRiskStatus(), testRisk.getRiskStatus());
        assertEquals(updatedDetails.getRiskRemarks(), testRisk.getRiskRemarks());
        
        verify(riskRepository).findById(1L);
        verify(riskRepository).save(testRisk);
    }

    @Test
    void updateRisk_WhenRiskNotExists_ShouldThrowException() {
        when(riskRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> riskService.updateRisk(1L, testRisk));

        assertEquals("Risk not found with id: 1", exception.getMessage());
        verify(riskRepository).findById(1L);
        verify(riskRepository, never()).save(any());
    }

    @Test
    void deleteRisk_WhenRiskExists_ShouldDeleteRisk() {
        when(riskRepository.findById(1L)).thenReturn(Optional.of(testRisk));

        assertDoesNotThrow(() -> riskService.deleteRisk(1L));

        verify(riskRepository).findById(1L);
        verify(riskRepository).delete(testRisk);
    }

    @Test
    void deleteRisk_WhenRiskNotExists_ShouldThrowException() {
        when(riskRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> riskService.deleteRisk(1L));

        assertEquals("Risk not found with id: 1", exception.getMessage());
        verify(riskRepository).findById(1L);
        verify(riskRepository, never()).delete(any());
    }

    @Test
    void getNonClosedRisks_ShouldReturnNonClosedRisks() {
        List<Risk> expectedRisks = Arrays.asList(testRisk);
        when(riskRepository.findAllNonClosedRisks()).thenReturn(expectedRisks);

        List<Risk> result = riskService.getNonClosedRisks();

        assertEquals(expectedRisks, result);
        verify(riskRepository).findAllNonClosedRisks();
    }

    @Test
    void getRiskCountByType_ShouldReturnCountMap() {
        List<Object[]> mockResults = Arrays.asList(
            new Object[]{Risk.RiskType.MARKET_PRACTICE, 5L},
            new Object[]{Risk.RiskType.PRICING, 3L}
        );
        when(riskRepository.countNonClosedRisksByType()).thenReturn(mockResults);

        Map<String, Long> result = riskService.getRiskCountByType();

        assertEquals(2, result.size());
        assertEquals(5L, result.get("Market Practice"));
        assertEquals(3L, result.get("Pricing"));
        verify(riskRepository).countNonClosedRisksByType();
    }

    @Test
    void getRisksByType_ShouldReturnRisksOfSpecificType() {
        List<Risk> expectedRisks = Arrays.asList(testRisk);
        when(riskRepository.findByRiskType(Risk.RiskType.MARKET_PRACTICE)).thenReturn(expectedRisks);

        List<Risk> result = riskService.getRisksByType(Risk.RiskType.MARKET_PRACTICE);

        assertEquals(expectedRisks, result);
        verify(riskRepository).findByRiskType(Risk.RiskType.MARKET_PRACTICE);
    }
}
