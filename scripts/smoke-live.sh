#!/usr/bin/env bash
set -euo pipefail

BACKEND_URL="${BACKEND_URL:-https://pulseops-backend-mee4.onrender.com}"
FRONTEND_URL="${FRONTEND_URL:-https://pulseops-frontend.onrender.com}"
API_BASE_URL="${API_BASE_URL:-${BACKEND_URL}/api/v1}"

echo "Checking frontend at ${FRONTEND_URL}"
curl --fail --silent --show-error "${FRONTEND_URL}" | grep -qi '<div id="root"></div>'

echo "Checking SPA route rewrite at ${FRONTEND_URL}/login"
curl --fail --silent --show-error "${FRONTEND_URL}/login" | grep -qi '<div id="root"></div>'

echo "Checking backend landing endpoint at ${BACKEND_URL}"
curl --fail --silent --show-error "${BACKEND_URL}" | grep -q '"status":"running"'

echo "Checking backend health at ${BACKEND_URL}/actuator/health"
curl --fail --silent --show-error "${BACKEND_URL}/actuator/health" | grep -q '"status":"UP"'

echo "Checking Swagger UI at ${BACKEND_URL}/swagger-ui.html"
curl --fail --silent --show-error --location "${BACKEND_URL}/swagger-ui.html" | grep -qi 'swagger'

echo "Checking CORS preflight from ${FRONTEND_URL}"
curl --fail --silent --show-error --include --request OPTIONS "${API_BASE_URL}/auth/login" \
  --header "Origin: ${FRONTEND_URL}" \
  --header "Access-Control-Request-Method: POST" \
  --header "Access-Control-Request-Headers: content-type" \
  | grep -qi "access-control-allow-origin: ${FRONTEND_URL}"

echo "Checking demo login"
curl --fail --silent --show-error --request POST "${API_BASE_URL}/auth/login" \
  --header 'Content-Type: application/json' \
  --data '{"email":"demo@pulseops.dev","password":"demo123"}' \
  | grep -q '"token"'

echo "Live smoke checks passed"
