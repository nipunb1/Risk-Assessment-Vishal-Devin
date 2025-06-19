package com.riskassessment.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "risks")
public class Risk {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "risk_id")
    private Long riskId;
    
    @NotNull
    @Column(name = "risk_date")
    private LocalDate riskDate;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_type")
    private RiskType riskType;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_probability")
    private RiskProbability riskProbability;
    
    @NotBlank
    @Column(name = "risk_desc", columnDefinition = "TEXT")
    private String riskDesc;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_status")
    private RiskStatus riskStatus;
    
    @Column(name = "risk_remarks", columnDefinition = "TEXT")
    private String riskRemarks;

    public Risk() {}

    public Risk(LocalDate riskDate, RiskType riskType, RiskProbability riskProbability, 
                String riskDesc, RiskStatus riskStatus, String riskRemarks) {
        this.riskDate = riskDate;
        this.riskType = riskType;
        this.riskProbability = riskProbability;
        this.riskDesc = riskDesc;
        this.riskStatus = riskStatus;
        this.riskRemarks = riskRemarks;
    }

    public Long getRiskId() { return riskId; }
    public void setRiskId(Long riskId) { this.riskId = riskId; }

    public LocalDate getRiskDate() { return riskDate; }
    public void setRiskDate(LocalDate riskDate) { this.riskDate = riskDate; }

    public RiskType getRiskType() { return riskType; }
    public void setRiskType(RiskType riskType) { this.riskType = riskType; }

    public RiskProbability getRiskProbability() { return riskProbability; }
    public void setRiskProbability(RiskProbability riskProbability) { this.riskProbability = riskProbability; }

    public String getRiskDesc() { return riskDesc; }
    public void setRiskDesc(String riskDesc) { this.riskDesc = riskDesc; }

    public RiskStatus getRiskStatus() { return riskStatus; }
    public void setRiskStatus(RiskStatus riskStatus) { this.riskStatus = riskStatus; }

    public String getRiskRemarks() { return riskRemarks; }
    public void setRiskRemarks(String riskRemarks) { this.riskRemarks = riskRemarks; }

    public enum RiskType {
        MARKET_PRACTICE("Market Practice"),
        CONFLICT_OF_INTEREST("Conflict of Interest"),
        PRICING("Pricing"),
        REGULATORY("Regulatory"),
        GOVERNANCE("Governance");

        private final String displayName;

        RiskType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RiskProbability {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High");

        private final String displayName;

        RiskProbability(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RiskStatus {
        OPEN("Open"),
        IN_PROGRESS("In Progress"),
        CLOSED("Closed");

        private final String displayName;

        RiskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
