# Academic Risk Assessment Engine Model

## Overview

The SEBI Member Compliance Monitoring System implements a deterministic, fully explainable risk scoring engine. 

> [!NOTE]
> This model is an academic formulation designed to demonstrate RegTech risk scoring methodology. It is configurable and does NOT claim to represent official SEBI risk formulas.

---

## 1. Weighted Formula & Factors

The risk score is calculated on a **0 to 100 scale**, where:
- `0` represents perfectly compliant / zero risk
- `100` represents maximum non-compliance / critical risk

### Weighted Factors (100% total)

$$\text{Risk Score} = w_1 \cdot F_{\text{viol\_freq}} + w_2 \cdot F_{\text{viol\_sev}} + w_3 \cdot F_{\text{late}} + w_4 \cdot F_{\text{history}} + w_5 \cdot F_{\text{repeat}}$$

| Factor | Weight | Formula / Normalized Computation | Description |
| :--- | :--- | :--- | :--- |
| **Violation Frequency** | 30% | $\min(100, \text{Open Violations} \times 20)$ | Impact of currently open/unresolved compliance violations. |
| **Violation Severity** | 25% | $\frac{\sum \text{Severity Weight}}{\text{Max Possible}} \times 100$<br>*(Critical=40, High=25, Medium=15, Low=5)* | Measures the weight of the active violations. |
| **Late Submissions** | 20% | $\min(100, \text{Overdue Submissions in 12 mo} \times 25)$ | Frequency of missing prescribed regulatory deadlines. |
| **Compliance History** | 15% | $100 - \text{Compliance Rate \%}$ | Inverse ratio of successful on-time filings vs total assigned filings. |
| **Repeated Violations** | 10% | $\min(100, \text{Repeat Violations} \times 33.3)$ | Recurrence of the exact same rule failure code within 1 year. |

---

## 2. Risk Level Classifications

| Risk Score | Risk Level | System Action & UI Color |
| :--- | :--- | :--- |
| **0 – 30** | **LOW** | Green badge. Standard scheduled monitoring. |
| **31 – 60** | **MEDIUM** | Amber/Yellow badge. Officer warning alert triggered. |
| **61 – 80** | **HIGH** | Orange badge. Officer inspection required, escalation alert. |
| **81 – 100** | **CRITICAL** | Red badge. Urgent audit flag, automatic high-severity notification. |

---

## 3. Explainability & Factors JSON

Each risk assessment stores an explicit JSON payload of `contributing_factors`:
```json
{
  "violationFrequencyFactor": 40.0,
  "violationSeverityFactor": 65.0,
  "lateSubmissionFactor": 50.0,
  "complianceHistoryFactor": 18.0,
  "repeatedViolationFactor": 33.3,
  "explanations": [
    "2 active High/Critical severity violations present",
    "2 submissions overdue past deadline in last 12 months",
    "Historical compliance rate is 82.0%",
    "1 repeated cybersecurity filing violation detected"
  ]
}
```
This ensures every score in the UI is accompanied by a human-readable explanation card ("Why ABC Securities received HIGH risk level").
