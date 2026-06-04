#!/usr/bin/env bash
set -euo pipefail

BACKEND_URL="${BACKEND_URL:-http://localhost:8080}"
FRONTEND_URL="${FRONTEND_URL:-http://localhost:5173}"

echo "Checking backend health at ${BACKEND_URL}/actuator/health"
curl --fail --silent --show-error "${BACKEND_URL}/actuator/health" | grep -q '"status":"UP"'

echo "Checking Prometheus metrics at ${BACKEND_URL}/actuator/prometheus"
curl --fail --silent --show-error "${BACKEND_URL}/actuator/prometheus" | grep -q 'pulseops_monitor_checks_total'

echo "Checking frontend at ${FRONTEND_URL}"
curl --fail --silent --show-error "${FRONTEND_URL}" | grep -qi '<div id="root"></div>'

echo "Smoke checks passed"
