package com.riskassessment.repository;

import com.riskassessment.entity.Risk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskRepository extends JpaRepository<Risk, Long> {
    
    @Query("SELECT r FROM Risk r WHERE r.riskStatus != 'CLOSED'")
    List<Risk> findAllNonClosedRisks();
    
    @Query("SELECT r.riskType, COUNT(r) FROM Risk r WHERE r.riskStatus != 'CLOSED' GROUP BY r.riskType")
    List<Object[]> countNonClosedRisksByType();
    
    List<Risk> findByRiskType(Risk.RiskType riskType);
    
    List<Risk> findByRiskStatus(Risk.RiskStatus riskStatus);
}
