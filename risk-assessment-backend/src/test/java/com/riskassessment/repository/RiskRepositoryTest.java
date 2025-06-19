package com.riskassessment.repository;

import com.riskassessment.entity.Risk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class RiskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RiskRepository riskRepository;

    private Risk openRisk;
    private Risk closedRisk;
    private Risk inProgressRisk;

    @BeforeEach
    void setUp() {
        openRisk = new Risk(
            LocalDate.now(),
            Risk.RiskType.MARKET_PRACTICE,
            Risk.RiskProbability.HIGH,
            "Open risk description",
            Risk.RiskStatus.OPEN,
            "Open risk remarks"
        );

        closedRisk = new Risk(
            LocalDate.now().minusDays(5),
            Risk.RiskType.PRICING,
            Risk.RiskProbability.MEDIUM,
            "Closed risk description",
            Risk.RiskStatus.CLOSED,
            "Closed risk remarks"
        );

        inProgressRisk = new Risk(
            LocalDate.now().minusDays(2),
            Risk.RiskType.REGULATORY,
            Risk.RiskProbability.LOW,
            "In progress risk description",
            Risk.RiskStatus.IN_PROGRESS,
            "In progress risk remarks"
        );

        entityManager.persistAndFlush(openRisk);
        entityManager.persistAndFlush(closedRisk);
        entityManager.persistAndFlush(inProgressRisk);
    }

    @Test
    void findAllNonClosedRisks_ShouldReturnOnlyNonClosedRisks() {
        List<Risk> nonClosedRisks = riskRepository.findAllNonClosedRisks();

        assertEquals(2, nonClosedRisks.size());
        assertTrue(nonClosedRisks.stream().noneMatch(risk -> risk.getRiskStatus() == Risk.RiskStatus.CLOSED));
        assertTrue(nonClosedRisks.stream().anyMatch(risk -> risk.getRiskStatus() == Risk.RiskStatus.OPEN));
        assertTrue(nonClosedRisks.stream().anyMatch(risk -> risk.getRiskStatus() == Risk.RiskStatus.IN_PROGRESS));
    }

    @Test
    void countNonClosedRisksByType_ShouldReturnCorrectCounts() {
        List<Object[]> results = riskRepository.countNonClosedRisksByType();

        assertEquals(2, results.size());
        
        for (Object[] result : results) {
            Risk.RiskType type = (Risk.RiskType) result[0];
            Long count = (Long) result[1];
            
            if (type == Risk.RiskType.MARKET_PRACTICE) {
                assertEquals(1L, count);
            } else if (type == Risk.RiskType.REGULATORY) {
                assertEquals(1L, count);
            }
        }
    }

    @Test
    void findByRiskType_ShouldReturnRisksOfSpecificType() {
        List<Risk> marketPracticeRisks = riskRepository.findByRiskType(Risk.RiskType.MARKET_PRACTICE);
        List<Risk> pricingRisks = riskRepository.findByRiskType(Risk.RiskType.PRICING);

        assertEquals(1, marketPracticeRisks.size());
        assertEquals(Risk.RiskType.MARKET_PRACTICE, marketPracticeRisks.get(0).getRiskType());

        assertEquals(1, pricingRisks.size());
        assertEquals(Risk.RiskType.PRICING, pricingRisks.get(0).getRiskType());
    }

    @Test
    void findByRiskStatus_ShouldReturnRisksOfSpecificStatus() {
        List<Risk> openRisks = riskRepository.findByRiskStatus(Risk.RiskStatus.OPEN);
        List<Risk> closedRisks = riskRepository.findByRiskStatus(Risk.RiskStatus.CLOSED);
        List<Risk> inProgressRisks = riskRepository.findByRiskStatus(Risk.RiskStatus.IN_PROGRESS);

        assertEquals(1, openRisks.size());
        assertEquals(Risk.RiskStatus.OPEN, openRisks.get(0).getRiskStatus());

        assertEquals(1, closedRisks.size());
        assertEquals(Risk.RiskStatus.CLOSED, closedRisks.get(0).getRiskStatus());

        assertEquals(1, inProgressRisks.size());
        assertEquals(Risk.RiskStatus.IN_PROGRESS, inProgressRisks.get(0).getRiskStatus());
    }
}
