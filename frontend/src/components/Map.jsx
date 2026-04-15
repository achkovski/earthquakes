import { MapContainer, TileLayer, CircleMarker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";

function markerColor(mag) {
  if (mag >= 6) return "#b30000";
  if (mag >= 5) return "#e34a33";
  if (mag >= 4) return "#fc8d59";
  if (mag >= 3) return "#fdbb84";
  return "#fdd49e";
}

function Map({ quakes }) {
  return (
    <MapContainer
      center={[20, 0]}
      zoom={2}
      scrollWheelZoom
      style={{ height: "420px", width: "100%" }}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      {quakes.map((q) => {
        const mag = Number(q.magnitude);
        return (
          <CircleMarker
            key={q.id}
            center={[Number(q.latitude), Number(q.longitude)]}
            radius={Math.max(3, mag * 2)}
            pathOptions={{ color: markerColor(mag), fillOpacity: 0.6 }}
          >
            <Popup>
              <strong>M {mag.toFixed(1)}</strong>
              <br />
              {q.place}
              <br />
              <small>{new Date(q.eventTime).toLocaleString()}</small>
            </Popup>
          </CircleMarker>
        );
      })}
    </MapContainer>
  );
}

export default Map;
