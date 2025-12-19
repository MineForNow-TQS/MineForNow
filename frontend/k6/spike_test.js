import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 0 },   // Start with 0
        { duration: '1m', target: 100 },  // Spike to 100 users fast
        { duration: '10s', target: 0 },   // Scale down
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'], // Allow higher latency during spike
    },
};

export default function () {
    const res = http.get('http://localhost:8080/api/vehicles');
    check(res, { 'status was 200': (r) => r.status == 200 });
    sleep(1);
}
