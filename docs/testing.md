# ReguLens — Testing & Quality Assurance Strategy

## Automated Test Suites

### Backend Unit & Integration Tests (JUnit 5 & Mockito)
- **Rule Engine Tests** (`RuleEngineServiceTest.java`): Verifies rule evaluation for on-time submissions, late filings (`RULE-CMP-001`), missing document proofs (`RULE-CMP-002`), and repeated violations (`RULE-CMP-004`).
- **Risk Engine Tests** (`RiskEngineServiceTest.java`): Verifies deterministic 0–100 risk score calculations, factor weighting, and risk level classifications (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).

### Execution Command
```bash
cd backend
mvn test
```

### Frontend Build Validation
- **Vite Production Build**: Compiles React SPA modules and validates JSX syntax integrity.
```bash
cd frontend
npm run build
```
