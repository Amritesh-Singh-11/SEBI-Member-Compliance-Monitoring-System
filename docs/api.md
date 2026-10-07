# ReguLens — REST API Reference Documentation

## Overview
Base URL: `/api`  
Swagger OpenAPI Interactive Specification: `/api/swagger-ui.html`

---

## Endpoint Specification

### Authentication
- `POST /api/auth/login` — Authenticate user and receive JWT access & refresh tokens
- `POST /api/auth/register` — Register new user account
- `POST /api/auth/refresh` — Refresh expired access token
- `POST /api/auth/logout` — Logout user and invalidate token session

### Member Management
- `GET /api/members` — Paginated list of members with search, status, and risk filters
- `GET /api/members/{id}` — Retrieve full member profile & historical summary
- `POST /api/members` — Register new market intermediary (Admin)
- `PUT /api/members/{id}` — Update member registration status and details
- `DELETE /api/members/{id}` — Remove/deactivate member entity (Admin)

### Compliance Requirements & Records
- `GET /api/requirements` — List configurable compliance filing requirements
- `POST /api/requirements` — Add new regulatory requirement (Admin)
- `GET /api/compliance` — List member filing period records
- `POST /api/compliance` — Assign requirement filing to member
- `PUT /api/compliance/{id}` — Update filing status / review

### Document Management
- `POST /api/documents/upload` — Upload multipart file evidence
- `GET /api/documents` — List uploaded compliance documents
- `GET /api/documents/{id}/download` — Download document stream
- `PUT /api/documents/{id}/approve` — Approve document proof
- `PUT /api/documents/{id}/reject` — Reject document proof with reason

### Configurable Rule Engine
- `GET /api/rules` — View active rule configurations
- `POST /api/rules` — Create new rule condition
- `PUT /api/rules/{id}` — Update rule configuration
- `POST /api/rules/execute` — Trigger automated rule engine evaluation

### Violations & CAPA
- `GET /api/violations` — List detected violations
- `POST /api/violations` — Log new compliance violation
- `PUT /api/violations/{id}/resolve` — Resolve violation ticket
- `GET /api/corrective-actions` — List CAPA remediation plans
- `POST /api/corrective-actions` — Issue CAPA plan
- `PATCH /api/corrective-actions/{id}/verify` — Verify CAPA completion

### Risk Assessment Engine
- `GET /api/risk` — List member risk scores
- `GET /api/risk/member/{memberId}` — Explainable risk calculation breakdown
- `POST /api/risk/calculate` — Recalculate deterministic risk score

### Dashboard & Analytics
- `GET /api/dashboard/summary` — Aggregated real-time metrics
- `GET /api/dashboard/compliance` — Status distribution chart data
- `GET /api/dashboard/violations` — Severity distribution chart data
- `GET /api/dashboard/risk` — Risk level distribution chart data

### Notifications & Audit Trail
- `GET /api/notifications` — User in-app alerts
- `PUT /api/notifications/{id}/read` — Mark alert as read
- `GET /api/audit-logs` — Immutable audit trail (Admin)
