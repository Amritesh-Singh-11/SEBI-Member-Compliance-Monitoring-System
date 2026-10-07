-- Seed Initial Data for SEBI Compliance Monitoring System (Academic RegTech Prototype)

-- 1. Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_COMPLIANCE_OFFICER');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_MEMBER_USER');

-- 2. Users (Passwords: Admin@123, Officer@123, Member@123 encoded with BCrypt)
-- Hash generated using standard BCrypt strength 10
INSERT INTO users (id, username, password_hash, email, full_name, status) VALUES 
(1, 'admin', '$2a$10$7zB.b1.g3gqJ3rZ9KzF8e.V5L/f0q.1234567890abcdefghijklm', 'admin@regtech-prototype.academic', 'System Administrator', 'ACTIVE'),
(2, 'officer', '$2a$10$7zB.b1.g3gqJ3rZ9KzF8e.V5L/f0q.1234567890abcdefghijklm', 'officer@regtech-prototype.academic', 'Senior Compliance Officer', 'ACTIVE'),
(3, 'member_user', '$2a$10$7zB.b1.g3gqJ3rZ9KzF8e.V5L/f0q.1234567890abcdefghijklm', 'compliance@abc-securities.demo', 'ABC Securities Officer', 'ACTIVE');

INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1),
(2, 2),
(3, 3);

-- 3. Synthetic Stock Broker Members
INSERT INTO members (id, member_code, organization_name, registration_number, member_type, registration_date, registration_validity, status, address, city, state, country, contact_email, contact_phone, compliance_officer_id, risk_score, risk_level) VALUES
(1, 'SB-001', 'ABC Securities Private Limited (Fictional)', 'DEMO-SB-0001', 'STOCK_BROKER', '2015-04-12', '2030-04-11', 'ACTIVE', 'Dalal Street, Fort', 'Mumbai', 'Maharashtra', 'India', 'compliance@abc-securities.demo', '+91 9820011223', 2, 74.0, 'HIGH'),
(2, 'SB-002', 'Apex Broking Services Ltd (Fictional)', 'DEMO-SB-0002', 'STOCK_BROKER', '2018-09-01', '2028-08-31', 'ACTIVE', 'Connaught Place', 'New Delhi', 'Delhi', 'India', 'compliance@apexbroking.demo', '+91 9811122334', 2, 22.5, 'LOW'),
(3, 'SB-003', 'Zenith Wealth & Securities (Fictional)', 'DEMO-SB-0003', 'STOCK_BROKER', '2020-01-15', '2025-01-14', 'ACTIVE', 'MG Road', 'Bengaluru', 'Karnataka', 'India', 'compliance@zenithwealth.demo', '+91 9845099887', 2, 48.0, 'MEDIUM'),
(4, 'SB-004', 'Vanguard Stock Trading Corp (Fictional)', 'DEMO-SB-0004', 'STOCK_BROKER', '2012-06-20', '2027-06-19', 'SUSPENDED', 'Bandra Kurla Complex', 'Mumbai', 'Maharashtra', 'India', 'info@vanguardtrade.demo', '+91 9822233445', 2, 88.0, 'CRITICAL'),
(5, 'SB-005', 'Horizon Equities & Derivatives (Fictional)', 'DEMO-SB-0005', 'STOCK_BROKER', '2021-11-10', '2026-11-09', 'ACTIVE', 'Park Street', 'Kolkata', 'West Bengal', 'India', 'compliance@horizonequities.demo', '+91 9833344556', 2, 15.0, 'LOW');

-- 4. Academic Compliance Requirements
INSERT INTO compliance_requirements (id, requirement_code, title, description, category, frequency, due_date_offset, severity, mandatory, applicable_member_type, effective_from, regulatory_reference, active) VALUES
(1, 'REQ-CYB-01', 'Quarterly Cybersecurity & System Audit', 'Filing of comprehensive IT infrastructure cybersecurity vulnerability assessment and CERT-In audit certificate.', 'CYBERSECURITY', 'QUARTERLY', 15, 'HIGH', true, 'STOCK_BROKER', '2024-01-01', 'SEBI/HO/MIRSD/CIR/P/2018/147', true),
(2, 'REQ-FIN-02', 'Monthly Net Worth & Capital Adequacy Return', 'Submission of CA-certified net worth computation report verifying minimum margin capital compliance.', 'FINANCIAL', 'MONTHLY', 10, 'CRITICAL', true, 'STOCK_BROKER', '2024-01-01', 'SEBI Circular MIRSD/SE/Cir-19/2009', true),
(3, 'REQ-INV-03', 'Half-Yearly Investor Grievance Redressal Report', 'Filing detailing grievances received, resolved, and pending beyond 30 days during the half-year period.', 'INVESTOR_PROTECTION', 'HALF_YEARLY', 30, 'MEDIUM', true, 'STOCK_BROKER', '2024-01-01', 'SEBI/HO/MIRSD/DOC/P/CIR/2021/42', true),
(4, 'REQ-GOV-04', 'Annual Governance & Code of Conduct Certificate', 'Declaration confirming compliance officer appointment and adherence to regulatory ethics guidelines.', 'GOVERNANCE', 'ANNUAL', 45, 'HIGH', true, 'STOCK_BROKER', '2024-01-01', 'SEBI (Stock Brokers) Regulations 1992', true);

-- 5. Academic Rules
INSERT INTO rules (id, rule_code, rule_name, description, category, condition_expression, action_type, severity_impact, risk_score_delta, active) VALUES
(1, 'RULE-CMP-001', 'Late Filing Submission Rule', 'Triggers violation when submission date exceeds prescribed requirement due date.', 'REGULATORY_FILING', 'submissionDate > dueDate OR (currentDate > dueDate AND status == ''PENDING'')', 'CREATE_VIOLATION', 'MEDIUM', 15.0, true),
(2, 'RULE-CMP-002', 'Mandatory Document Attachment Missing', 'Triggers violation when mandatory compliance filing lacks uploaded document evidence.', 'DOCUMENTATION', 'mandatory == true AND documentCount == 0 AND status == ''SUBMITTED''', 'CREATE_VIOLATION', 'HIGH', 25.0, true),
(3, 'RULE-CMP-003', 'Expired Audit Certificate', 'Triggers violation when submitted cybersecurity/networth audit document expiry date has elapsed.', 'CYBERSECURITY', 'documentExpiryDate < currentDate', 'CREATE_VIOLATION', 'HIGH', 25.0, true),
(4, 'RULE-CMP-004', 'Repeated Compliance Recurrence Failure', 'Escalates risk when a member incurs 2+ violations of the same requirement type in 12 months.', 'OPERATIONAL', 'sameViolationCount >= 2', 'CREATE_VIOLATION', 'CRITICAL', 35.0, true);

-- 6. Compliance Records for ABC Securities (Member 1)
INSERT INTO compliance_records (id, member_id, requirement_id, period_start, period_end, due_date, submission_date, status, remarks, reviewed_by, reviewed_at) VALUES
(1, 1, 1, '2026-01-01', '2026-03-31', '2026-04-15', '2026-04-20', 'OVERDUE', 'Submission attempted 5 days past deadline', 2, '2026-04-21 10:30:00'),
(2, 1, 2, '2026-08-01', '2026-08-31', '2026-09-10', '2026-09-08', 'COMPLIANT', 'Verified monthly net worth certificate attached', 2, '2026-09-09 14:00:00'),
(3, 1, 3, '2026-01-01', '2026-06-30', '2026-07-31', NULL, 'PENDING', 'Awaiting half-yearly grievance compilation', NULL, NULL);

-- 7. Initial Violations
INSERT INTO violations (id, violation_code, member_id, compliance_record_id, rule_id, title, description, category, severity, status, detected_at, assigned_officer_id, due_date) VALUES
(1, 'VIOL-2026-001', 1, 1, 1, 'Late Submission of Q1 Cybersecurity Audit', 'ABC Securities submitted Q1 Cybersecurity Audit on 2026-04-20 while due date was 2026-04-15.', 'CYBERSECURITY', 'HIGH', 'ACTION_REQUIRED', '2026-04-16 00:05:00', 2, '2026-05-15'),
(2, 'VIOL-2026-002', 4, NULL, 4, 'Repeated Capital Adequacy Margin Shortfall', 'Vanguard Stock Trading incurred 3 consecutive margin shortfall flags.', 'FINANCIAL', 'CRITICAL', 'OPEN', '2026-09-01 11:15:00', 2, '2026-09-30');

-- 8. Corrective Action for Violation 1
INSERT INTO corrective_actions (id, violation_id, action_code, description, root_cause, responsible_person, target_date, status) VALUES
(1, 1, 'CAPA-2026-001', 'Implement automated audit calendar reminder and internal review workflow to prevent delayed auditor sign-offs.', 'Delay in CERT-In auditor field verification', 'Chief Compliance Officer - ABC Securities', '2026-05-10', 'OPEN');

-- 9. Initial Risk Assessments
INSERT INTO risk_assessments (id, member_id, risk_score, risk_level, calculation_version, contributing_factors_json) VALUES
(1, 1, 74.0, 'HIGH', '1.0-academic', '{"violationFrequencyFactor": 40.0, "violationSeverityFactor": 65.0, "lateSubmissionFactor": 50.0, "complianceHistoryFactor": 18.0, "repeatedViolationFactor": 33.3, "explanations": ["1 high-severity violation active", "1 submission filed past prescribed deadline", "Historical compliance rate is 82.0%"]}'),
(2, 4, 88.0, 'CRITICAL', '1.0-academic', '{"violationFrequencyFactor": 80.0, "violationSeverityFactor": 90.0, "lateSubmissionFactor": 75.0, "complianceHistoryFactor": 40.0, "repeatedViolationFactor": 80.0, "explanations": ["Repeated financial margin shortfalls", "Member account currently suspended"]}');

-- 10. Sample Notifications
INSERT INTO notifications (id, recipient_user_id, type, title, message, severity, related_entity_type, related_entity_id, read_status) VALUES
(1, 2, 'VIOLATION_CREATED', 'High Risk Violation Flagged', 'Violation VIOL-2026-001 created for ABC Securities Private Limited.', 'HIGH', 'VIOLATION', 1, false),
(2, 3, 'REQUIREMENT_DUE_SOON', 'Upcoming Compliance Filing Deadline', 'Half-Yearly Investor Grievance Redressal Report is due by 2026-07-31.', 'MEDIUM', 'COMPLIANCE_RECORD', 3, false);

-- 11. Initial Regulations Library
INSERT INTO regulations (id, title, reference_number, description, effective_date, applicable_member_type, source_url, version, status) VALUES
(1, 'SEBI Cyber Security Framework for Stock Brokers', 'SEBI/HO/MIRSD/CIR/P/2018/147', 'Mandatory framework for cyber security and cyber resilience for stock brokers.', '2018-12-03', 'STOCK_BROKER', 'https://www.sebi.gov.in/legal/circulars/dec-2018/cyber-security-framework-for-stock-brokers_41215.html', '1.2', 'ACTIVE'),
(2, 'Comprehensive Risk Management Framework for Stock Brokers', 'SEBI/HO/MRD/DRMN/CIR/P/2020/21', 'Guidelines covering margin requirements and risk mitigation controls.', '2020-02-14', 'STOCK_BROKER', 'https://www.sebi.gov.in/legal/circulars/feb-2020/risk-management-framework_45982.html', '2.0', 'ACTIVE');

-- 12. Initial Complaints
INSERT INTO complaints (id, complaint_code, member_id, category, received_date, description, assigned_officer_id, status) VALUES
(1, 'CMPL-2026-001', 1, 'DELAY_IN_PAYOUT', '2026-09-01', 'Investor reported 2-day delay in funds payout following option exercise.', 2, 'UNDER_REVIEW');

-- 13. Audit Log Initial Entry
INSERT INTO audit_logs (id, actor_username, action, entity_type, entity_id, timestamp, new_value_json) VALUES
(1, 'system', 'SYSTEM_INITIALIZATION', 'SYSTEM', 1, CURRENT_TIMESTAMP, '{"event":"Initial seed database schema populated with academic RegTech baseline data"}');
