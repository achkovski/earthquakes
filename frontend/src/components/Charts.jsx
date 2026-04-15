import {
  BarChart,
  Bar,
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
} from "recharts";

function buildHistogram(quakes) {
  const buckets = { "0-1": 0, "1-2": 0, "2-3": 0, "3-4": 0, "4-5": 0, "5-6": 0, "6+": 0 };
  quakes.forEach((q) => {
    const m = Number(q.magnitude);
    if (m < 1) buckets["0-1"]++;
    else if (m < 2) buckets["1-2"]++;
    else if (m < 3) buckets["2-3"]++;
    else if (m < 4) buckets["3-4"]++;
    else if (m < 5) buckets["4-5"]++;
    else if (m < 6) buckets["5-6"]++;
    else buckets["6+"]++;
  });
  return Object.entries(buckets).map(([range, count]) => ({ range, count }));
}

function buildTimeSeries(quakes) {
  const byDay = {};
  quakes.forEach((q) => {
    const day = new Date(q.eventTime).toISOString().slice(0, 10);
    byDay[day] = (byDay[day] || 0) + 1;
  });
  return Object.entries(byDay)
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([day, count]) => ({ day, count }));
}

function Charts({ quakes }) {
  const histogram = buildHistogram(quakes);
  const timeSeries = buildTimeSeries(quakes);

  return (
    <div className="row g-3">
      <div className="col-md-6">
        <h5>Magnitude distribution</h5>
        <ResponsiveContainer width="100%" height={260}>
          <BarChart data={histogram}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="range" />
            <YAxis allowDecimals={false} />
            <Tooltip />
            <Bar dataKey="count" fill="#0d6efd" />
          </BarChart>
        </ResponsiveContainer>
      </div>
      <div className="col-md-6">
        <h5>Events per day</h5>
        <ResponsiveContainer width="100%" height={260}>
          <LineChart data={timeSeries}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="day" />
            <YAxis allowDecimals={false} />
            <Tooltip />
            <Line type="monotone" dataKey="count" stroke="#dc3545" />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

export default Charts;
