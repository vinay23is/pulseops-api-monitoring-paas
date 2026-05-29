# PulseOps — Resume Bullets

Use these on your resume under a "Projects" section:

---

**PulseOps — API Monitoring & Incident Alerting PaaS** | Java 21 · Spring Boot 3 · React · PostgreSQL · Redis · Docker

- Architected a full-stack PaaS with multi-tenant project/monitor management, JWT-secured REST APIs (10+ endpoints with Swagger/OpenAPI docs), and a BCrypt-hashed auth system using Spring Security 6 and a custom JWT filter chain.

- Built an async health-check engine using Redis Streams and Java 21 Virtual Threads — a scheduler dispatches due monitors every 30s to a stream queue, and a concurrent consumer worker performs HTTP probes, measuring status codes and latency with non-blocking WebClient (Project Reactor).

- Implemented an automated incident lifecycle system that opens incidents on monitor failure, increments failure counts on repeat checks, fires multi-channel alerts (in-app, mock email, webhook with failure tracking), and auto-resolves incidents when health returns.

- Designed a project API key system with BCrypt-hashed keys, prefix-based lookups, and Redis-backed sliding-window rate limiting (60 req/min per key) for custom event ingestion via a public `POST /api/v1/events` endpoint.

- Delivered a React + Vite + Tailwind dashboard with real-time uptime charts (Recharts), monitor status tables, incident timelines, and a public `/status/{slug}` page — fully containerized with Docker Compose (Postgres 16, Redis 7, Spring Boot, Nginx) for zero-config local deployment.
