import { MapContainer, Marker, Popup, TileLayer, useMap, useMapEvents } from "react-leaflet";
import type { LatLngExpression } from "leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";

export type MapMarker = {
  id: string;
  title: string;
  status: string;
  latitude: number;
  longitude: number;
};

type IncidentMapProps = {
  value: { latitude: number; longitude: number } | null;
  onChange?: (point: { latitude: number; longitude: number }) => void;
  markers?: MapMarker[];
  onMarkerClick?: (id: string) => void;
  readOnly?: boolean;
};

const defaultCenter: LatLngExpression = [-17.7833, -63.1821];

const markerIcon = L.icon({
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
  iconSize: [25, 41],
  iconAnchor: [12, 41],
});

function ClickHandler({ onChange }: Pick<IncidentMapProps, "onChange">) {
  useMapEvents({
    click(event) {
      onChange?.({
        latitude: Number(event.latlng.lat.toFixed(6)),
        longitude: Number(event.latlng.lng.toFixed(6)),
      });
    },
  });

  return null;
}

function MapFocus({ center }: { center: LatLngExpression }) {
  const map = useMap();
  map.setView(center);
  return null;
}

export function IncidentMap({
  value,
  onChange,
  markers = [],
  onMarkerClick,
  readOnly = false,
}: IncidentMapProps) {
  const markerPosition: LatLngExpression | null = value ? [value.latitude, value.longitude] : null;
  const firstMarker = markers[0];
  const center = markerPosition ?? (firstMarker ? [firstMarker.latitude, firstMarker.longitude] : defaultCenter);

  return (
    <MapContainer center={center} zoom={12} scrollWheelZoom className="incident-map">
      <MapFocus center={center} />
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      {!readOnly && <ClickHandler onChange={onChange} />}
      {markers.map((marker) => (
        <Marker
          eventHandlers={{ click: () => onMarkerClick?.(marker.id) }}
          icon={markerIcon}
          key={marker.id}
          position={[marker.latitude, marker.longitude]}
        >
          <Popup>
            <strong>{marker.title}</strong>
            <br />
            {marker.status}
          </Popup>
        </Marker>
      ))}
      {markerPosition && <Marker position={markerPosition} icon={markerIcon} />}
    </MapContainer>
  );
}
