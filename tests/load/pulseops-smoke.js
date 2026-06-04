import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 5,
  duration: "30s",
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<500"],
  },
};

const baseUrl = __ENV.BASE_URL || "http://localhost:8080";

export default function () {
  const health = http.get(`${baseUrl}/actuator/health`);
  check(health, {
    "health is up": (response) => response.status === 200 && response.body.includes('"status":"UP"'),
  });

  const metrics = http.get(`${baseUrl}/actuator/prometheus`);
  check(metrics, {
    "metrics exposed": (response) => response.status === 200 && response.body.includes("pulseops_monitor_checks_total"),
  });

  sleep(1);
}
