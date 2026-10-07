# ReguLens — Database Design & Entity Schema

## Database Overview
**ReguLens** uses a normalized MySQL 8.x relational database schema managed via **Flyway** migration scripts (`src/main/resources/db/migration/`).

---

## ER Table Schema

### 1. `roles` & `users` & `user_roles`
Stores user credentials (BCrypt hashes) and RBAC role assignments (`ROLE_ADMIN`, `ROLE_COMPLIANCE_OFFICER`, `ROLE_MEMBER_USER`).

### 2. `members`
Regulated market intermediary entities (Stock Brokers).
- `id`, `member_code` (e.g. `SB-001`), `organization_name`, `registration_number` (e.g. `DEMO-SB-0001`), `member_type` (`STOCK_BROKER`), `registration_date`, `registration_validity`, `status` (`ACTIVE`, `SUSPENDED`, `EXPIRED`, `INACTIVE`), `risk_score` (0-100), `risk_level` (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).

### 3. `compliance_requirements`
Configurable regulatory filing requirements.
- `id`, `requirement_code` (e.g. `REQ-CYB-01`), `title`, `category` (`CYBERSECURITY`, `FINANCIAL`, `INVESTOR_PROTECTION`, `GOVERNANCE`), `frequency` (`MONTHLY`, `QUARTERLY`, `HALF_YEARLY`, `ANNUAL`), `due_date_offset`, `severity`, `mandatory`, `applicable_member_type`.

### 4. `compliance_records`
Links Member + Requirement + Filing Period. Unique constraint: `(member_id, requirement_id, period_start, period_end)`.
- `id`, `member_id`, `requirement_id`, `period_start`, `period_end`, `due_date`, `submission_date`, `status` (`PENDING`, `SUBMITTED`, `COMPLIANT`, `OVERDUE`, `REJECTED`).

### 5. `documents`
Uploaded file evidence metadata.
- `id`, `member_id`, `compliance_record_id`, `document_type`, `original_filename`, `storage_key`, `content_type`, `file_size`, `checksum` (SHA-256), `status` (`UPLOADED`, `APPROVED`, `REJECTED`).

### 6. `rules` & `rule_execution_logs`
Configurable rule definitions & execution logs.
- `id`, `rule_code` (e.g. `RULE-CMP-001`), `condition_expression`, `action_type`, `severity_impact`, `risk_score_delta`.

### 7. `violations` & `corrective_actions`
Rule-triggered breaches & CAPA remediation tickets.
- `id`, `violation_code`, `severity` (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`), `status` (`OPEN`, `UNDER_REVIEW`, `ACTION_REQUIRED`, `RESOLVED`, `CLOSED`).

### 8. `risk_assessments`
Historical deterministic risk calculations & explainable factor JSON.

### 9. `notifications` & `regulations` & `complaints` & `audit_logs`
In-app notifications, regulatory library circulars, internal grievance tickets, and immutable audit logs.

---

## Database Migrations
- `V1__initial_schema.sql`: Table DDL, foreign keys, indexes on `registration_number`, `member_code`, `status`, `risk_level`, `due_date`, `created_at`.
- `V2__seed_initial_data.sql`: Populates baseline academic RegTech seed data.
