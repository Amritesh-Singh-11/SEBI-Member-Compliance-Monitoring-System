package com.sebi.compliance.dashboard;

import com.sebi.compliance.member.MemberDTO;

import java.util.List;
import java.util.Map;

public class DashboardMetricsDTO {

    private long totalMembers;
    private long activeMembers;
    private long compliantMembers;
    private long pendingComplianceCount;
    private long overdueRequirementsCount;
    private long openViolationsCount;
    private long highCriticalRiskMembersCount;
    private double overallComplianceRate;

    private Map<String, Long> complianceStatusDistribution;
    private Map<String, Long> riskLevelDistribution;
    private Map<String, Long> violationsBySeverity;
    private Map<String, Long> violationsByCategory;

    private List<MemberDTO> topHighRiskMembers;

    public DashboardMetricsDTO() {}

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public long getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(long activeMembers) {
        this.activeMembers = activeMembers;
    }

    public long getCompliantMembers() {
        return compliantMembers;
    }

    public void setCompliantMembers(long compliantMembers) {
        this.compliantMembers = compliantMembers;
    }

    public long getPendingComplianceCount() {
        return pendingComplianceCount;
    }

    public void setPendingComplianceCount(long pendingComplianceCount) {
        this.pendingComplianceCount = pendingComplianceCount;
    }

    public long getOverdueRequirementsCount() {
        return overdueRequirementsCount;
    }

    public void setOverdueRequirementsCount(long overdueRequirementsCount) {
        this.overdueRequirementsCount = overdueRequirementsCount;
    }

    public long getOpenViolationsCount() {
        return openViolationsCount;
    }

    public void setOpenViolationsCount(long openViolationsCount) {
        this.openViolationsCount = openViolationsCount;
    }

    public long getHighCriticalRiskMembersCount() {
        return highCriticalRiskMembersCount;
    }

    public void setHighCriticalRiskMembersCount(long highCriticalRiskMembersCount) {
        this.highCriticalRiskMembersCount = highCriticalRiskMembersCount;
    }

    public double getOverallComplianceRate() {
        return overallComplianceRate;
    }

    public void setOverallComplianceRate(double overallComplianceRate) {
        this.overallComplianceRate = overallComplianceRate;
    }

    public Map<String, Long> getComplianceStatusDistribution() {
        return complianceStatusDistribution;
    }

    public void setComplianceStatusDistribution(Map<String, Long> complianceStatusDistribution) {
        this.complianceStatusDistribution = complianceStatusDistribution;
    }

    public Map<String, Long> getRiskLevelDistribution() {
        return riskLevelDistribution;
    }

    public void setRiskLevelDistribution(Map<String, Long> riskLevelDistribution) {
        this.riskLevelDistribution = riskLevelDistribution;
    }

    public Map<String, Long> getViolationsBySeverity() {
        return violationsBySeverity;
    }

    public void setViolationsBySeverity(Map<String, Long> violationsBySeverity) {
        this.violationsBySeverity = violationsBySeverity;
    }

    public Map<String, Long> getViolationsByCategory() {
        return violationsByCategory;
    }

    public void setViolationsByCategory(Map<String, Long> violationsByCategory) {
        this.violationsByCategory = violationsByCategory;
    }

    public List<MemberDTO> getTopHighRiskMembers() {
        return topHighRiskMembers;
    }

    public void setTopHighRiskMembers(List<MemberDTO> topHighRiskMembers) {
        this.topHighRiskMembers = topHighRiskMembers;
    }
}
