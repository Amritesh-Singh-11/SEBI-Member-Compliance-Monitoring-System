# ReguLens — Security Architecture & Data Protection

## Security Controls

1. **Authentication & Password Hashing**
   - User passwords are encoded using **BCrypt** with strength factor 10. Passwords are never stored or logged in plain text.

2. **Stateless JWT Authorization & RBAC**
   - Authentication relies on HMAC-SHA256 signed JWT tokens.
   - Spring Security enforces Role-Based Access Control (`@PreAuthorize("hasRole('ADMIN')")`, `@PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER')")`) at the backend controller level.

3. **File Upload Hardening**
   - Filenames are sanitized using `StringUtils.cleanPath()` to prevent directory path traversal attacks (`..`).
   - Allowed file formats are restricted to PDF, DOCX, XLSX, CSV, PNG, JPG.
   - Max file size is capped at 25MB.
   - Files are stored using unique generated UUID keys, preventing direct web exposure of underlying storage paths.
   - SHA-256 checksums are calculated upon upload.

4. **SQL Injection Protection**
   - All database queries use parameterized Spring Data JPA and Hibernate ORM repositories.

5. **Secrets & Credentials Management**
   - Secrets (`JWT_SECRET`, `DB_PASSWORD`) are loaded via environment variables (`.env`).
   - `.env` and sensitive local files are excluded from Git repository tracking via `.gitignore`.
