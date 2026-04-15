import { useEffect, useState } from "react";
import { getEarthquakes, refreshEarthquakes } from "./api";
import Filters from "./components/Filters";
import Map from "./components/Map";
import Charts from "./components/Charts";
import QuakeList from "./components/QuakeList";

function App() {
  const [quakes, setQuakes] = useState([]);
  const [filters, setFilters] = useState({ after: "", minMag: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    getEarthquakes(filters)
      .then(setQuakes)
      .catch((err) => setError(err.message || "Failed to load"))
      .finally(() => setLoading(false));
  }, [filters]);

  async function handleRefresh() {
    try {
      setLoading(true);
      await refreshEarthquakes();
      const data = await getEarthquakes(filters);
      setQuakes(data);
    } catch (err) {
      setError(err.message || "Refresh failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="container py-4">
      <header className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="h3 m-0">SeismoScore</h1>
        <button className="btn btn-outline-primary" onClick={handleRefresh} disabled={loading}>
          {loading ? "Loading..." : "Refresh from USGS"}
        </button>
      </header>

      <section className="mb-4">
        <Filters onApply={setFilters} />
      </section>

      {error && <div className="alert alert-danger">{error}</div>}

      <section className="mb-4">
        <Map quakes={quakes} />
      </section>

      <section className="mb-4">
        <Charts quakes={quakes} />
      </section>

      <section className="mb-4">
        <h5>
          Events <span className="text-muted">({quakes.length})</span>
        </h5>
        <QuakeList quakes={quakes} />
      </section>
    </div>
  );
}

export default App;
