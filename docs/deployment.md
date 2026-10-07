# ReguLens — Deployment & Cloud Architecture Guide

## Local Development Deployment
```bash
docker compose up --build
```
Spins up:
- MySQL 8.0 on port `3306`
- Spring Boot Backend on port `8080`
- React SPA Frontend on port `3000`
- Python FastAPI ML Service on port `8000`

---

## AWS Production Cloud Deployment Architecture

1. **Database**: AWS RDS MySQL 8.0 Multi-AZ instance.
2. **Backend**: AWS ECS / Fargate container service running `sebi-backend:latest`.
3. **Frontend**: Static web assets hosted on AWS S3 with CloudFront CDN distribution.
4. **Document Storage**: AWS S3 bucket configured via `S3StorageService` implementation.
5. **Secrets Management**: AWS Secrets Manager storing database credentials and JWT signing keys.
6. **Monitoring**: AWS CloudWatch container logs and metrics.
