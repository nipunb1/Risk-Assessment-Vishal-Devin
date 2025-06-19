package com.riskassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riskassessment.entity.Risk;
import com.riskassessment.service.RiskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiskController.class)
public class RiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RiskService riskService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getAllRisks_ShouldReturnListOfRisks() throws Exception {
        List<Risk> risks = Arrays.asList(testRisk);
        when(riskService.getAllRisks()).thenReturn(risks);

        mockMvc.perform(get("/api/risks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].riskId").value(1))
                .andExpect(jsonPath("$[0].riskDesc").value("Test risk description"));

        verify(riskService).getAllRisks();
    }

    @Test
    void getRiskById_WhenRiskExists_ShouldReturnRisk() throws Exception {
        when(riskService.getRiskById(1L)).thenReturn(Optional.of(testRisk));

        mockMvc.perform(get("/api/risks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskId").value(1))
                .andExpect(jsonPath("$.riskDesc").value("Test risk description"));

        verify(riskService).getRiskById(1L);
    }

    @Test
    void getRiskById_WhenRiskNotExists_ShouldReturnNotFound() throws Exception {
        when(riskService.getRiskById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/risks/1"))
                .andExpect(status().isNotFound());

        verify(riskService).getRiskById(1L);
    }

    @Test
    void createRisk_WithValidData_ShouldReturnCreatedRisk() throws Exception {
        when(riskService.createRisk(any(Risk.class))).thenReturn(testRisk);

        mockMvc.perform(post("/api/risks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRisk)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskId").value(1))
                .andExpect(jsonPath("$.riskDesc").value("Test risk description"));

        verify(riskService).createRisk(any(Risk.class));
    }

    @Test
    void updateRisk_WhenRiskExists_ShouldReturnUpdatedRisk() throws Exception {
        Risk updatedRisk = new Risk(
            LocalDate.now(),
            Risk.RiskType.PRICING,
            Risk.RiskProbability.MEDIUM,
            "Updated description",
            Risk.RiskStatus.IN_PROGRESS,
            "Updated remarks"
        );
        updatedRisk.setRiskId(1L);

        when(riskService.updateRisk(eq(1L), any(Risk.class))).thenReturn(updatedRisk);

        mockMvc.perform(put("/api/risks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRisk)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskDesc").value("Updated description"));

        verify(riskService).updateRisk(eq(1L), any(Risk.class));
    }

    @Test
    void updateRisk_WhenRiskNotExists_ShouldReturnNotFound() throws Exception {
        when(riskService.updateRisk(eq(1L), any(Risk.class)))
                .thenThrow(new RuntimeException("Risk not found"));

        mockMvc.perform(put("/api/risks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRisk)))
                .andExpect(status().isNotFound());

        verify(riskService).updateRisk(eq(1L), any(Risk.class));
    }

    @Test
    void deleteRisk_WhenRiskExists_ShouldReturnOk() throws Exception {
        doNothing().when(riskService).deleteRisk(1L);

        mockMvc.perform(delete("/api/risks/1"))
                .andExpect(status().isOk());

        verify(riskService).deleteRisk(1L);
    }

    @Test
    void deleteRisk_WhenRiskNotExists_ShouldReturnNotFound() throws Exception {
        doThrow(new RuntimeException("Risk not found")).when(riskService).deleteRisk(1L);

        mockMvc.perform(delete("/api/risks/1"))
                .andExpect(status().isNotFound());

        verify(riskService).deleteRisk(1L);
    }

    @Test
    void getNonClosedRisks_ShouldReturnNonClosedRisks() throws Exception {
        List<Risk> nonClosedRisks = Arrays.asList(testRisk);
        when(riskService.getNonClosedRisks()).thenReturn(nonClosedRisks);

        mockMvc.perform(get("/api/risks/non-closed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].riskStatus").value("OPEN"));

        verify(riskService).getNonClosedRisks();
    }

    @Test
    void getRiskCountByType_ShouldReturnCounts() throws Exception {
        Map<String, Long> counts = new HashMap<>();
        counts.put("Market Practice", 5L);
        counts.put("Pricing", 3L);
        
        when(riskService.getRiskCountByType()).thenReturn(counts);

        mockMvc.perform(get("/api/risks/dashboard/counts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Market Practice']").value(5))
                .andExpect(jsonPath("$['Pricing']").value(3));

        verify(riskService).getRiskCountByType();
    }

    @Test
    void getRisksByType_ShouldReturnRisksOfSpecificType() throws Exception {
        List<Risk> risks = Arrays.asList(testRisk);
        when(riskService.getRisksByType(Risk.RiskType.MARKET_PRACTICE)).thenReturn(risks);

        mockMvc.perform(get("/api/risks/type/MARKET_PRACTICE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].riskType").value("MARKET_PRACTICE"));

        verify(riskService).getRisksByType(Risk.RiskType.MARKET_PRACTICE);
    }
}
