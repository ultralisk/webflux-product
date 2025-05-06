import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100,
    duration: '10s',
};

export default function () {
    const res = http.get('http://localhost:9218/demo/info');

    check(res, {
        '✅ Response Code 200': (r) => r.status === 200,
        '✅ Response Time < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(1);
}
