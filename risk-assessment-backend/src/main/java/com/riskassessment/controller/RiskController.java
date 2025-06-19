package com.riskassessment.controller;

import com.riskassessment.entity.Risk;
import com.riskassessment.service.RiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/risks")
@CrossOrigin(origins = "*")
public class RiskController {
    
    @Autowired
    private RiskService riskService;
    
    @GetMapping
    public List<Risk> getAllRisks() {
        return riskService.getAllRisks();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Risk> getRiskById(@PathVariable Long id) {
        return riskService.getRiskById(id)
                .map(risk -> ResponseEntity.ok().body(risk))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Risk createRisk(@Valid @RequestBody Risk risk) {
        return riskService.createRisk(risk);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Risk> updateRisk(@PathVariable Long id, @Valid @RequestBody Risk riskDetails) {
        try {
            Risk updatedRisk = riskService.updateRisk(id, riskDetails);
            return ResponseEntity.ok(updatedRisk);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRisk(@PathVariable Long id) {
        try {
            riskService.deleteRisk(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/non-closed")
    public List<Risk> getNonClosedRisks() {
        return riskService.getNonClosedRisks();
    }
    
    @GetMapping("/dashboard/counts")
    public Map<String, Long> getRiskCountByType() {
        return riskService.getRiskCountByType();
    }
    
    @GetMapping("/type/{riskType}")
    public List<Risk> getRisksByType(@PathVariable Risk.RiskType riskType) {
        return riskService.getRisksByType(riskType);
    }
}
