-- SEBI Member Compliance Monitoring System Schema (Academic RegTech Prototype)

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_code VARCHAR(50) NOT NULL UNIQUE,
    organization_name VARCHAR(200) NOT NULL,
    registration_number VARCHAR(100) NOT NULL UNIQUE,
    member_type VARCHAR(50) NOT NULL,
    registration_date DATE NOT NULL,
    registration_validity DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100) DEFAULT 'India',
    contact_email VARCHAR(150) NOT NULL,
    contact_phone VARCHAR(30),
    compliance_officer_id BIGINT,
    risk_score DOUBLE PRECISION DEFAULT 0.0,
    risk_level VARCHAR(30) DEFAULT 'LOW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_member_officer FOREIGN KEY (compliance_officer_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS compliance_requirements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requirement_code VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    frequency VARCHAR(30) NOT NULL,
    due_date_offset INT DEFAULT 0,
    severity VARCHAR(30) NOT NULL DEFAULT 'MEDIUM',
    mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    applicable_member_type VARCHAR(50) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    regulatory_reference VARCHAR(200),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS compliance_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    requirement_id BIGINT NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    due_date DATE NOT NULL,
    submission_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    remarks TEXT,
    reviewed_by BIGINT,
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_member_req_period UNIQUE (member_id, requirement_id, period_start, period_end),
    CONSTRAINT fk_cr_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    CONSTRAINT fk_cr_requirement FOREIGN KEY (requirement_id) REFERENCES compliance_requirements(id) ON DELETE CASCADE,
    CONSTRAINT fk_cr_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    compliance_record_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    storage_key VARCHAR(255) NOT NULL UNIQUE,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    checksum VARCHAR(64) NOT NULL,
    version INT NOT NULL DEFAULT 1,
    uploaded_by VARCHAR(100) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'UPLOADED',
    rejection_reason TEXT,
    CONSTRAINT fk_doc_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_record FOREIGN KEY (compliance_record_id) REFERENCES compliance_records(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_code VARCHAR(50) NOT NULL UNIQUE,
    rule_name VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    condition_expression TEXT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    severity_impact VARCHAR(30) NOT NULL,
    risk_score_delta DOUBLE PRECISION DEFAULT 10.0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rule_execution_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_id BIGINT NOT NULL,
    compliance_record_id BIGINT,
    member_id BIGINT NOT NULL,
    triggered BOOLEAN NOT NULL,
    execution_details TEXT,
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rel_rule FOREIGN KEY (rule_id) REFERENCES rules(id) ON DELETE CASCADE,
    CONSTRAINT fk_rel_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS violations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    violation_code VARCHAR(50) NOT NULL UNIQUE,
    member_id BIGINT NOT NULL,
    compliance_record_id BIGINT,
    rule_id BIGINT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    severity VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    detected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_officer_id BIGINT,
    due_date DATE,
    resolution_date DATE,
    resolution_remarks TEXT,
    CONSTRAINT fk_viol_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    CONSTRAINT fk_viol_record FOREIGN KEY (compliance_record_id) REFERENCES compliance_records(id) ON DELETE SET NULL,
    CONSTRAINT fk_viol_rule FOREIGN KEY (rule_id) REFERENCES rules(id) ON DELETE SET NULL,
    CONSTRAINT fk_viol_officer FOREIGN KEY (assigned_officer_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS corrective_actions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    violation_id BIGINT NOT NULL,
    action_code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    root_cause TEXT,
    responsible_person VARCHAR(150),
    target_date DATE NOT NULL,
    completion_date DATE,
    evidence_document_id BIGINT,
    verification_remarks TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ca_violation FOREIGN KEY (violation_id) REFERENCES violations(id) ON DELETE CASCADE,
    CONSTRAINT fk_ca_doc FOREIGN KEY (evidence_document_id) REFERENCES documents(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS risk_assessments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    risk_score DOUBLE PRECISION NOT NULL,
    risk_level VARCHAR(30) NOT NULL,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    calculation_version VARCHAR(20) NOT NULL DEFAULT '1.0-academic',
    contributing_factors_json TEXT,
    CONSTRAINT fk_ra_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    severity VARCHAR(30) NOT NULL DEFAULT 'INFO',
    related_entity_type VARCHAR(50),
    related_entity_id BIGINT,
    read_status BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_user FOREIGN KEY (recipient_user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS regulations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    reference_number VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    effective_date DATE NOT NULL,
    applicable_member_type VARCHAR(50) NOT NULL,
    source_url VARCHAR(255),
    version VARCHAR(20) NOT NULL DEFAULT '1.0',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_code VARCHAR(50) NOT NULL UNIQUE,
    member_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    received_date DATE NOT NULL,
    description TEXT NOT NULL,
    assigned_officer_id BIGINT,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    resolution TEXT,
    closed_date DATE,
    CONSTRAINT fk_cmpl_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmpl_officer FOREIGN KEY (assigned_officer_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_username VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    old_value_json TEXT,
    new_value_json TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255)
);

-- Indexes for performance
CREATE INDEX idx_member_code ON members(member_code);
CREATE INDEX idx_member_reg ON members(registration_number);
CREATE INDEX idx_member_status ON members(status);
CREATE INDEX idx_member_risk ON members(risk_level);

CREATE INDEX idx_cr_member_status ON compliance_records(member_id, status);
CREATE INDEX idx_cr_due_date ON compliance_records(due_date);

CREATE INDEX idx_viol_member_status ON violations(member_id, status);
CREATE INDEX idx_viol_severity ON violations(severity);

CREATE INDEX idx_notif_user_read ON notifications(recipient_user_id, read_status);
CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp);
