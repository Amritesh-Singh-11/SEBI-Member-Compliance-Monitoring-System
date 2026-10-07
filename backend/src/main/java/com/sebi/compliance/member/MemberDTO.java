package com.sebi.compliance.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class MemberDTO {

    private Long id;

    @NotBlank(message = "Member code is required")
    private String memberCode;

    @NotBlank(message = "Organization name is required")
    private String organizationName;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotNull(message = "Member type is required")
    private MemberType memberType = MemberType.STOCK_BROKER;

    @NotNull(message = "Registration date is required")
    private LocalDate registrationDate;

    @NotNull(message = "Registration validity is required")
    private LocalDate registrationValidity;

    private MemberStatus status = MemberStatus.ACTIVE;

    private String address;
    private String city;
    private String state;
    private String country = "India";

    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be valid")
    private String contactEmail;

    private String contactPhone;
    private Long complianceOfficerId;
    private String complianceOfficerName;
    private Double riskScore = 0.0;
    private String riskLevel = "LOW";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDate getRegistrationValidity() {
        return registrationValidity;
    }

    public void setRegistrationValidity(LocalDate registrationValidity) {
        this.registrationValidity = registrationValidity;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Long getComplianceOfficerId() {
        return complianceOfficerId;
    }

    public void setComplianceOfficerId(Long complianceOfficerId) {
        this.complianceOfficerId = complianceOfficerId;
    }

    public String getComplianceOfficerName() {
        return complianceOfficerName;
    }

    public void setComplianceOfficerName(String complianceOfficerName) {
        this.complianceOfficerName = complianceOfficerName;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}
