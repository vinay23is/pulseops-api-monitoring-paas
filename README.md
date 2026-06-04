# PulseOps — API Monitoring & Incident Alerting PaaS

A production-style, free, local-first API monitoring platform similar to a mini Better Stack / UptimeRobot / Datadog uptime monitor. Built with Java 21, Spring Boot 3, React, PostgreSQL, and Redis.

---

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
│  │   Port 5432  │    │  Port 6379   │                        │
│  └──────────────┘    └──────────────┘                        │
│                                                                │
│  ┌──────────────┐    ┌──────────────┐                        │
│  │  Prometheus  │───▶│   Grafana    │                        │
│  │   Port 9090  │    │  Port 3000   │                        │
│  └──────────────┘    └──────────────┘                        │
└────────────────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Backend     | Java 21, Spring Boot 3.2, Maven     |
| Security    | Spring Security 6, JWT (jjwt 0.12)  |
| Database    | PostgreSQL 16, Spring Data JPA       |
| Queue/Cache | Redis 7, Redis Streams               |
| HTTP Client | Spring WebFlux (WebClient)           |
| API Docs    | SpringDoc OpenAPI / Swagger UI       |
| Observability | Spring Boot Actuator, Micrometer, Prometheus, Grafana |
| Frontend    | React 18, Vite 5, Tailwind CSS 3    |
| Charts      | Recharts                             |
| Container   | Docker, Docker Compose, Kubernetes, Helm |
| Web Server  | Nginx (SPA proxy)                    |
| IaC         | Terraform skeleton for Helm releases |

---

## Features

- **JWT Auth** — register, login, BCrypt passwords, stateless token auth
- **Multi-tenant projects** — users own projects; projects own monitors
- **Monitor management** — CRUD, GET/POST/PUT/DELETE/HEAD, configurable interval/timeout/status codes, toggle active
- **Health check engine** — scheduler dispatches due monitors every 30s to a Redis Stream; a Virtual Thread worker consumes and performs HTTP checks, recording latency and status
- **Incident system** — auto-open on failure, increment failure count, auto-resolve on recovery
- **Alert system** — IN_APP, MOCK_EMAIL, WEBHOOK alerts with delivery tracking and webhook failure logging
- **API Key system** — project-scoped keys, BCrypt-hashed, prefix-based lookup, revoke endpoint
- **Rate limiting** — Redis sliding-window rate limiter (60 req/min per API key)
- **Production observability** — Actuator health probes, Prometheus metrics, Grafana datasource provisioning, custom monitor check counters/latency histograms
- **CI pipeline** — GitHub Actions runs backend tests, frontend production builds, and Docker image build checks on pull requests and pushes to `main`
- **Infrastructure validation** — CI lints/renders Helm and runs Terraform formatting/validation checks
- **Kubernetes deployment** — Helm chart with Deployments/StatefulSets, Services, ConfigMaps, Secrets, probes, resource requests/limits, and optional Ingress
- **Operational testing** — Compose smoke test script and k6 load smoke script for health/metrics endpoints
- **Dashboard APIs** — uptime %, avg latency, open incidents, recent checks, recent alerts
- **Public status page** — `/status/{slug}` — no auth required
- **Seed data** — demo user and project auto-seeded on first run

---

## Local Setup

### Prerequisites
- Docker 24+ and Docker Compose V2
- (Optional) Java 21 + Maven for local backend dev
- (Optional) Node.js 20+ for local frontend dev

### Start everything with Docker Compose

```bash
git clone https://github.com/YOUR_USERNAME/pulseops-api-monitoring-paas.git
cd pulseops-api-monitoring-paas

docker compose up --build
```

| Service      | URL                                        |
|--------------|--------------------------------------------|
| Frontend     | http://localhost:5173                      |
| Backend API  | http://localhost:8080                      |
| Swagger UI   | http://localhost:8080/swagger-ui.html      |
| Health Probe  | http://localhost:8080/actuator/health     |
| Prometheus Metrics | http://localhost:8080/actuator/prometheus |
| Prometheus   | http://localhost:9090                      |
| Grafana      | http://localhost:3000 (admin/admin)        |
| PostgreSQL   | localhost:5432 (pulseops/pulseops)         |
| Redis        | localhost:6379                             |

### Demo Credentials

```
Email:    demo@pulseops.dev
Password: demo123
```

### Local dev (without Docker)

**Backend:**
```bash
cd backend
# Ensure Postgres & Redis are running locally, then:
mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

### CI checks

```bash
cd backend && mvn test
cd frontend && npm ci && npm run build
docker build -t pulseops-backend:local ./backend
docker build -t pulseops-frontend:local ./frontend
```

> If Maven is not installed locally, run the backend test from CI or use a Maven container once Docker Desktop is running.

### Smoke and load checks

After `docker compose up --build` reports healthy services:

```bash
./scripts/smoke-compose.sh
```

Optional k6 smoke load:

```bash
k6 run tests/load/pulseops-smoke.js
```

Set `BACKEND_URL`, `FRONTEND_URL`, or `BASE_URL` to target non-local environments.

---

## Free Hosted Deployment

Recommended hobby setup:

- Frontend: Vercel Hobby, root directory `frontend`
- Backend: Render free web service from `render.yaml`
- Postgres: Supabase free project
- Redis: Upstash free Redis database

### Render backend

Use the Blueprint flow in Render and select this repository. The `render.yaml` file deploys `pulseops-backend` from `backend/Dockerfile`.

Fill these Render environment variables from Supabase and Upstash:

| Key | Source |
|-----|--------|
| `DB_HOST` | Supabase database host |
| `DB_PORT` | `5432` |
| `DB_NAME` | Supabase database name |
| `DB_USER` | Supabase database user |
| `DB_PASSWORD` | Supabase database password |
| `REDIS_HOST` | Upstash Redis host |
| `REDIS_PORT` | Upstash Redis port |
| `REDIS_USERNAME` | `default` |
| `REDIS_PASSWORD` | Upstash Redis password |
| `REDIS_SSL` | `true` |
| `JWT_SECRET` | A long random secret |

After deploy, verify:

```bash
curl https://your-render-backend.onrender.com/actuator/health
```

### Vercel frontend

In Vercel, set:

```text
Framework Preset: Vite
Root Directory: frontend
Build Command: npm run build
Output Directory: dist
Install Command: npm ci
```

Add this Vercel environment variable after the Render backend is live:

```text
VITE_API_BASE_URL=https://your-render-backend.onrender.com/api/v1
```

Redeploy the Vercel project after adding the environment variable.

---

## Kubernetes Deployment

The Helm chart in `charts/pulseops` deploys the full platform:

- frontend Deployment + Service with Nginx API proxy config
- backend Deployment + Service with Actuator readiness/liveness probes
- PostgreSQL StatefulSet + Service
- Redis StatefulSet + Service
- ConfigMap and Secret templates
- optional Ingress
- CPU/memory requests and limits

Build local images first:

```bash
docker build -t pulseops-backend:latest ./backend
docker build -t pulseops-frontend:latest ./frontend
```

Install with Helm:

```bash
helm upgrade --install pulseops ./charts/pulseops \
  --namespace pulseops \
  --create-namespace
```

Port-forward the frontend:

```bash
kubectl -n pulseops port-forward svc/pulseops-pulseops-frontend 5173:80
```

For Terraform-driven installs, see `infra/terraform`.

---

## API Examples

### Register
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com","password":"secret123"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@pulseops.dev","password":"demo123"}'
# → returns { "token": "eyJ...", "email": "...", "name": "...", "userId": 1 }
```

### Create a project
```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"My App","slug":"my-app","description":"Production APIs"}'
```

### Add a monitor
```bash
curl -X POST http://localhost:8080/api/v1/projects/1/monitors \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"API Health","url":"https://api.example.com/health","method":"GET","expectedStatusCode":200,"intervalSeconds":60,"timeoutSeconds":10}'
```

### Get dashboard
```bash
curl http://localhost:8080/api/v1/dashboard \
  -H "Authorization: Bearer $TOKEN"
```

### Public status page
```bash
curl http://localhost:8080/api/v1/status/my-app
# No auth required
```

### Submit custom event via API key
```bash
curl -X POST http://localhost:8080/api/v1/events \
  -H "Content-Type: application/json" \
  -d '{"eventType":"deploy","payload":"{\"version\":\"1.2.3\"}","apiKey":"pk_..."}'
```

---

## Screenshots

> _Add screenshots here after running the app_

- Landing page
- Dashboard with latency chart
- Monitor list with status badges
- Incident timeline
- Public status page

---

## Project Structure

```
pulseops-api-monitoring-paas/
├── backend/
│   └── src/main/java/dev/pulseops/
│       ├── config/          # Security, Redis, Swagger, WebClient
│       ├── controller/      # REST controllers (10+)
│       ├── dto/             # Java records (request/response)
│       ├── entity/          # JPA entities + enums
│       ├── exception/       # GlobalExceptionHandler
│       ├── repository/      # Spring Data JPA repos
│       ├── security/        # JwtUtil, JwtAuthFilter, UserDetailsService
│       ├── service/         # Business logic
│       ├── scheduler/       # MonitorScheduler (30s dispatch)
│       ├── worker/          # MonitorWorker (Redis Stream consumer)
│       └── DataSeeder.java  # Demo data on startup
├── frontend/
│   └── src/
│       ├── api/             # Axios API clients
│       ├── components/      # Reusable UI (StatCard, Modal, badges)
│       ├── contexts/        # AuthContext
│       └── pages/           # Landing, Login, Dashboard, etc.
├── charts/pulseops/         # Helm chart for Kubernetes deployment
├── infra/terraform/         # Terraform skeleton for Helm-based installs
├── ops/                     # Prometheus and Grafana provisioning
├── scripts/                 # Local operational scripts
├── tests/load/              # k6 smoke/load checks
├── .github/workflows/       # CI pipeline
├── docker-compose.yml
├── README.md
└── RESUME_BULLETS.md
```

---

## Resume Bullets

See [RESUME_BULLETS.md](./RESUME_BULLETS.md) for 5 strong resume-ready bullet points.

---

## Future Improvements

- [ ] Email/SMS alerts via SendGrid / Twilio (when budget allows)
- [ ] On-call rotation and escalation policies
- [ ] SSL certificate expiry monitoring
- [ ] Multi-region check nodes
- [ ] Webhook alert configuration UI
- [ ] Monitor response body assertions
- [ ] SLA reports (weekly/monthly PDF export)
- [x] Prometheus/Grafana observability baseline
- [x] Kubernetes Helm chart deployment
- [x] GitHub Actions CI pipeline with Docker image builds
- [x] Smoke/load testing baseline
- [x] Terraform deployment skeleton

---

## License

MIT
