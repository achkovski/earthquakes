import { useState } from "react";

function Filters({ onApply }) {
  const [after, setAfter] = useState("");
  const [minMag, setMinMag] = useState("");

  function handleSubmit(e) {
    e.preventDefault();
    onApply({ after, minMag });
  }

  function handleReset() {
    setAfter("");
    setMinMag("");
    onApply({ after: "", minMag: "" });
  }

  return (
    <form className="row g-2 align-items-end" onSubmit={handleSubmit}>
      <div className="col-sm-4">
        <label className="form-label">After date</label>
        <input
          type="date"
          className="form-control"
          value={after}
          onChange={(e) => setAfter(e.target.value)}
        />
      </div>
      <div className="col-sm-4">
        <label className="form-label">Min magnitude</label>
        <input
          type="number"
          step="0.1"
          min="0"
          className="form-control"
          value={minMag}
          onChange={(e) => setMinMag(e.target.value)}
          placeholder="e.g. 2.5"
        />
      </div>
      <div className="col-sm-4 d-flex gap-2">
        <button type="submit" className="btn btn-primary">
          Apply
        </button>
        <button type="button" className="btn btn-outline-secondary" onClick={handleReset}>
          Reset
        </button>
      </div>
    </form>
  );
}

export default Filters;
