function QuakeList({ quakes }) {
  return (
    <div className="table-responsive">
      <table className="table table-striped table-hover">
        <thead>
          <tr>
            <th>Time</th>
            <th>Magnitude</th>
            <th>Type</th>
            <th>Place</th>
            <th>Depth (km)</th>
          </tr>
        </thead>
        <tbody>
          {[...quakes]
            .sort((a, b) => new Date(b.eventTime) - new Date(a.eventTime))
            .map((q) => (
            <tr key={q.id}>
              <td>{new Date(q.eventTime).toLocaleString()}</td>
              <td>{Number(q.magnitude).toFixed(1)}</td>
              <td>{q.magType}</td>
              <td>{q.place}</td>
              <td>{q.depth}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default QuakeList;
