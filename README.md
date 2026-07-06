# PulseOps — API Monitoring & Incident Alerting PaaS

[![CI](https://github.com/vinay23is/pulseops-api-monitoring-paas/actions/workflows/ci.yml/badge.svg)](https://github.com/vinay23is/pulseops-api-monitoring-paas/actions/workflows/ci.yml)

A production-style API uptime monitoring platform — the same idea as a mini Better Stack / UptimeRobot / Datadog synthetic monitor — built to demonstrate backend, observability, and infra skills end to end, not just a CRUD app.

**Live Demo:** [pulseops-api-monitoring-paas.vercel.app](https://pulseops-api-monitoring-paas.vercel.app)

> The frontend is hosted on Vercel. It talks to a Spring Boot backend on Render's free tier, which spins down after inactivity — the first API call after a while may take 30–60s to wake up, and if it's been fully idle it may need a manual redeploy. If the dashboard shows no data on first load, that's the free-tier backend waking up, not a bug in the app.

Demo credentials (seeded automatically on backend startup):

```text
Email:    demo@pulseops.dev
Password: demo123
```

## What problem does this solve?

Teams need to know the moment an API goes down, not when a customer complains. This project builds the core of an uptime-monitoring SaaS: users register projects, attach HTTP monitors to them, and the platform polls those endpoints on a schedule, opens incidents on failure, auto-resolves them on recovery, and fires alerts. I built it to go deep on the parts that separate a toy CRUD app from something production-shaped — a real check-dispatch pipeline via Redis Streams, JWT auth, rate limiting, Prometheus/Grafana observability, and a Kubernetes/Helm deployment path alongside the simpler Docker Compose one.

## Tech Stack

- **Frontend:** React 18, Vite 5, Tailwind CSS 3, Recharts
- **Backend:** Java 21, Spring Boot 3.2, Spring Security 6, Spring Data JPA, Spring WebFlux (WebClient), Maven
- **Database:** PostgreSQL 16
- **Queue/Cache:** Redis 7 (Redis Streams for the check-dispatch pipeline)
- **Auth:** Stateless JWT (jjwt 0.12), BCrypt password hashing
- **Observability:** Spring Boot Actuator, Micrometer, Prometheus, Grafana
- **Infra/Deployment:** Docker + Docker Compose (local), Render Blueprint (hosted demo — frontend on Vercel), Kubernetes via a Helm chart, Terraform skeleton for Helm-based installs, Nginx (SPA proxy)

## Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                        Docker Compose                          │
│                                                                │
│  ┌──────────────┐    ┌──────────────────────────────────────┐ │
│  │   Frontend   │    │             Backend (8080)           │ │
│  │  React/Vite  │───▶│  Spring Boot 3 · Java 21             │ │
│  │   Nginx:80   │    │                                      │ │
│  └──────────────┘    │  ┌─────────────┐ ┌────────────────┐ │ │
│                      │  │  REST APIs  │ │ Scheduler (30s)│ │ │
│                      │  │  (JWT Auth) │ └───────┬────────┘ │ │
│                      │  └─────────────┘         │          │ │
│                      │                    ┌──────▼──────┐   │ │
│                      │                    │Redis Stream  │   │ │
│                      │                    │monitor-checks│   │ │
│                      │                    └──────┬──────┘   │ │
│                      │  ┌─────────────┐   ┌──────▼──────┐  │ │
│                      │  │  PostgreSQL  │◀──│   Worker    │  │ │
│                      │  │   (JPA)      │   │(Virtual Th) │  │ │
│                      │  └─────────────┘   └─────────────┘  │ │
│                      └──────────────────────────────────────┘ │
│                                                                │
│  ┌──────────────┐    ┌──────────────┐                        │
│  │  PostgreSQL  │    │    Redis 7   │                        │
│  └──────────────┘    └──────────────┘                        │
│  ┌──────────────┐    ┌──────────────┐                        │
│  │  Prometheus  │───▶│   Grafana    │                        │
│  └──────────────┘    └──────────────┘                        │
└────────────────────────────────────────────────────────────────┘
```

A `MonitorScheduler` runs every 30s and pushes due monitors onto a Redis Stream (`monitor-checks`). A pool of virtual threads (`MonitorWorker`) consumes that stream, executes the actual HTTP check against the target URL, and writes latency/status results back to PostgreSQL. Failures increment an incident's failure count and open/escalate it; recovery auto-resolves it. Alerts (in-app, mock email, webhook) are dispatched from that same path, with delivery tracked in the database.

## Key Features

- JWT auth (register/login, BCrypt, stateless tokens) with multi-tenant projects — users own projects, projects own monitors
- Health check engine decoupled from the API layer via Redis Streams, so check execution scales independently of request traffic
- Incident lifecycle: auto-open on failure, failure-count tracking, auto-resolve on recovery
- Alerting system with IN_APP, MOCK_EMAIL, and WEBHOOK channels, including webhook failure logging
- Project-scoped API keys (BCrypt-hashed, prefix-based lookup) with a revoke endpoint, plus a Redis sliding-window rate limiter (60 req/min per key)
- Public, unauthenticated status pages at `/status/{slug}`
- Prometheus metrics and a provisioned Grafana datasource, with custom counters/histograms for monitor checks
- CI pipeline (GitHub Actions) running backend tests, frontend production builds, Docker image builds, Helm chart rendering, and Terraform validation
- Full Kubernetes deployment path via a Helm chart (Deployments/StatefulSets, probes, ConfigMaps/Secrets, optional Ingress) in addition to Docker Compose

## Interesting Engineering Decisions

- **Redis Streams instead of a simple cron-per-monitor loop.** Decoupling "decide what's due" (scheduler) from "execute the check" (worker) via a stream means the check-execution tier can scale horizontally without the scheduler needing to know about worker instances.
- **Virtual threads for the check worker.** HTTP health checks are I/O-bound and spend most of their time waiting on the network; Java 21 virtual threads let the worker handle many concurrent checks without the memory overhead of one platform thread per in-flight check.
- **Two deployment paths on purpose.** Docker Compose for a five-minute local spin-up, and a full Helm chart + Terraform skeleton for Kubernetes — built to demonstrate the same app deployed both ways rather than picking one and stopping.
- **Prefix-based API key lookup instead of hashing the whole key on every request.** Keys are stored BCrypt-hashed, but a prefix is kept in the clear to narrow the lookup before the (relatively expensive) BCrypt comparison — avoids a full-table BCrypt scan on every API-key-authenticated request.

## Running Locally

**Prerequisites:** Docker 24+ and Docker Compose V2 (optionally Java 21 + Maven, and Node 20+ for running frontend/backend outside containers).

```bash
git clone https://github.com/vinay23is/pulseops-api-monitoring-paas.git
cd pulseops-api-monitoring-paas
docker compose up --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Health Probe | http://localhost:8080/actuator/health |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 (admin/admin) |

Demo login: `demo@pulseops.dev` / `demo123` (seeded automatically).

**Local dev without Docker:**
```bash
# backend — needs Postgres & Redis running locally
cd backend && mvn spring-boot:run

# frontend
cd frontend && npm install && npm run dev
```

**Smoke/load checks:**
```bash
./scripts/smoke-compose.sh          # after docker compose up --build
k6 run tests/load/pulseops-smoke.js # optional k6 load smoke
```

**Kubernetes (Helm):**
```bash
docker build -t pulseops-backend:latest ./backend
docker build -t pulseops-frontend:latest ./frontend
helm upgrade --install pulseops ./charts/pulseops --namespace pulseops --create-namespace
kubectl -n pulseops port-forward svc/pulseops-pulseops-frontend 5173:80
```

See `infra/terraform` for Terraform-driven Helm installs, and [RESUME_BULLETS.md](./RESUME_BULLETS.md) for resume-ready summary bullets of this project.

## License

MIT
