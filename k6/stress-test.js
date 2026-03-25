import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// Custom metrics
const createLatency = new Trend('create_planet_latency', true);
const listLatency   = new Trend('list_planets_latency', true);
const getByIdLatency = new Trend('get_planet_by_id_latency', true);
const deleteLatency  = new Trend('delete_planet_latency', true);
const errorRate      = new Rate('error_rate');
const planetsCreated = new Counter('planets_created');

// ramping-arrival-rate: controla iterações/s para ~10.000 chamadas SWAPI (~50k requests totais)
// Total de ~230s
export const options = {
  scenarios: {
    stressTest: {
      executor: 'ramping-arrival-rate',
      preAllocatedVUs: 100,
      maxVUs: 200,
      timeUnit: '1s',
      stages: [
        { duration: '30s', target: 20  },  // Warm-up         → ~300 iter
        { duration: '30s', target: 50  },  // Ramp-up         → ~1.050 iter
        { duration: '30s', target: 50  },  // Base load       → ~1.500 iter
        { duration: '20s', target: 100 },  // 1º Pico         → ~1.500 iter
        { duration: '20s', target: 30  },  // Recuperação     → ~1.300 iter
        { duration: '30s', target: 50  },  // Estabiliza      → ~1.200 iter
        { duration: '20s', target: 100 },  // 2º Pico         → ~1.500 iter
        { duration: '20s', target: 30  },  // Recuperação     → ~1.300 iter
        { duration: '30s', target: 0   },  // Ramp-down       → ~450 iter
      ],
    },
  },
  thresholds: {
    'http_req_duration':        ['p(95)<500'],
    'error_rate':               ['rate<0.05'],
    'create_planet_latency':    ['p(95)<800'],
    'list_planets_latency':     ['p(95)<500'],
    'get_planet_by_id_latency': ['p(95)<300'],
  },
};

const PLANETS = [
  { name: 'Tatooine',  climate: 'arid',     terrain: 'desert' },
  { name: 'Alderaan',  climate: 'temperate', terrain: 'grasslands, mountains' },
  { name: 'Naboo',     climate: 'temperate', terrain: 'grassy hills, swamps, forests' },
  { name: 'Endor',     climate: 'temperate', terrain: 'forests' },
  { name: 'Hoth',      climate: 'frozen',    terrain: 'tundra, ice caves' },
  { name: 'Dagobah',   climate: 'murky',     terrain: 'swamp, jungles' },
  { name: 'Coruscant', climate: 'temperate', terrain: 'cityscape, mountains' },
];

const headers = { 'Content-Type': 'application/json' };

export default function () {
  const planet = PLANETS[Math.floor(Math.random() * PLANETS.length)];
  // Add VU id to name to avoid duplicate key issues across VUs
  const uniqueName = `${planet.name}-${__VU}-${__ITER}`;

  // 1. POST — Create planet
  const createRes = http.post(
    `${BASE_URL}/api/planets`,
    JSON.stringify({ name: uniqueName, climate: planet.climate, terrain: planet.terrain }),
    { headers, tags: { name: 'POST /api/planets' } },
  );
  createLatency.add(createRes.timings.duration);
  const createOk = check(createRes, {
    'create: status 201': (r) => r.status === 201,
    'create: has id':     (r) => JSON.parse(r.body).id !== undefined,
  });
  errorRate.add(!createOk);

  if (!createOk) {
    sleep(0.5);
    return;
  }

  planetsCreated.add(1);
  const createdId = JSON.parse(createRes.body).id;

  // 2. GET — List planets (paginated)
  const listRes = http.get(
    `${BASE_URL}/api/planets?page=0&size=10`,
    { tags: { name: 'GET /api/planets' } },
  );
  listLatency.add(listRes.timings.duration);
  const listOk = check(listRes, {
    'list: status 200':    (r) => r.status === 200,
    'list: has content':   (r) => JSON.parse(r.body).content !== undefined,
  });
  errorRate.add(!listOk);

  // 3. GET — Find by ID
  const getRes = http.get(
    `${BASE_URL}/api/planets/${createdId}`,
    { tags: { name: 'GET /api/planets/{id}' } },
  );
  getByIdLatency.add(getRes.timings.duration);
  const getOk = check(getRes, {
    'get by id: status 200': (r) => r.status === 200,
    'get by id: correct id': (r) => JSON.parse(r.body).id === createdId,
  });
  errorRate.add(!getOk);

  // 4. GET — Find by name
  const nameRes = http.get(
    `${BASE_URL}/api/planets/name/${encodeURIComponent(uniqueName)}`,
    { tags: { name: 'GET /api/planets/name/{name}' } },
  );
  check(nameRes, {
    'get by name: status 200': (r) => r.status === 200,
  });

  // 5. DELETE
  const delRes = http.del(
    `${BASE_URL}/api/planets/${createdId}`,
    null,
    { tags: { name: 'DELETE /api/planets/{id}' } },
  );
  deleteLatency.add(delRes.timings.duration);
  const delOk = check(delRes, {
    'delete: status 204': (r) => r.status === 204,
  });
  errorRate.add(!delOk);

  sleep(Math.random() * 0.5);
}

export function handleSummary(data) {
  return {
    'k6/stress-test-report.json': JSON.stringify(data, null, 2),
  };
}
