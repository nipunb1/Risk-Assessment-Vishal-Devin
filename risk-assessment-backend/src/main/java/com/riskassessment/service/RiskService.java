package com.riskassessment.service;

import com.riskassessment.entity.Risk;
import com.riskassessment.repository.RiskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RiskService {
    
    @Autowired
    private RiskRepository riskRepository;
    
    public List<Risk> getAllRisks() {
        return riskRepository.findAll();
    }
    
    public Optional<Risk> getRiskById(Long id) {
        return riskRepository.findById(id);
    }
    
    public Risk createRisk(Risk risk) {
        return riskRepository.save(risk);
    }
    
    public Risk updateRisk(Long id, Risk riskDetails) {
        Risk risk = riskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Risk not found with id: " + id));
        
        risk.setRiskDate(riskDetails.getRiskDate());
        risk.setRiskType(riskDetails.getRiskType());
        risk.setRiskProbability(riskDetails.getRiskProbability());
        risk.setRiskDesc(riskDetails.getRiskDesc());
        risk.setRiskStatus(riskDetails.getRiskStatus());
        risk.setRiskRemarks(riskDetails.getRiskRemarks());
        
        return riskRepository.save(risk);
    }
    
    public void deleteRisk(Long id) {
        Risk risk = riskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Risk not found with id: " + id));
        riskRepository.delete(risk);
    }
    
    public List<Risk> getNonClosedRisks() {
        return riskRepository.findAllNonClosedRisks();
    }
    
    public Map<String, Long> getRiskCountByType() {
        List<Object[]> results = riskRepository.countNonClosedRisksByType();
        Map<String, Long> riskCounts = new HashMap<>();
        
        for (Object[] result : results) {
            Risk.RiskType riskType = (Risk.RiskType) result[0];
            Long count = (Long) result[1];
            riskCounts.put(riskType.getDisplayName(), count);
        }
        
        return riskCounts;
    }
    
    public List<Risk> getRisksByType(Risk.RiskType riskType) {
        return riskRepository.findByRiskType(riskType);
    }
}
