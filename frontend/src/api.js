import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

export function getEarthquakes({ after, minMag } = {}) {
  const params = {};
  if (after) params.after = after;
  if (minMag !== "" && minMag !== undefined && minMag !== null) {
    params.minMag = minMag;
  }
  return api.get("/api/earthquakes", { params }).then((res) => res.data);
}

export function refreshEarthquakes() {
  return api.post("/api/earthquakes/refresh").then((res) => res.data);
}
