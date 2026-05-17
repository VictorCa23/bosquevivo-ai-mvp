import { useCallback, useEffect, useMemo, useState, type FormEvent } from "react";
import { IncidentMap } from "./components/IncidentMap";
import {
  assignIncident,
  closeIncident,
  createIncident,
  clearSession,
  deleteIncident,
  getIncident,
  getIncidentEvents,
  getIncidentSummary,
  getStoredSession,
  listBrigades,
  listIncidents,
  login,
  prioritizeIncident,
  reopenIncident,
  startIncidentAttention,
  storeSession,
  SESSION_EXPIRED_EVENT,
  type AuthSession,
  updateIncident,
  type Brigade,
  type CreateIncidentPayload,
  type Incident,
  type IncidentEvent,
  type IncidentFilters,
  type IncidentSeverity,
  type IncidentStatus,
  type IncidentSummary,
  type IncidentType,
  type UserRole,
} from "./services/incidents";
import "./styles.css";

const incidentTypes: Array<{ value: IncidentType; label: string }> = [
  { value: "FIRE", label: "Fuego" },
  { value: "SMOKE", label: "Humo" },
  { value: "ILLEGAL_LOGGING", label: "Tala" },
  { value: "POLLUTION", label: "Contaminacion" },
  { value: "OTHER", label: "Otro" },
];

const statuses: Array<{ value: IncidentStatus; label: string }> = [
  { value: "CREATED", label: "Creado" },
  { value: "PRIORITIZED", label: "Priorizado" },
  { value: "ASSIGNED", label: "Asignado" },
  { value: "IN_ATTENTION", label: "En atencion" },
  { value: "CLOSED", label: "Cerrado" },
  { value: "REOPENED", label: "Reabierto" },
];

const severities: Array<{ value: IncidentSeverity; label: string }> = [
  { value: "LOW", label: "Baja" },
  { value: "MEDIUM", label: "Media" },
  { value: "HIGH", label: "Alta" },
  { value: "CRITICAL", label: "Critica" },
];

const workflowSteps = [
  { status: "CREATED", label: "Reporte" },
  { status: "PRIORITIZED", label: "Priorizacion" },
  { status: "ASSIGNED", label: "Asignacion" },
  { status: "IN_ATTENTION", label: "Atencion" },
  { status: "CLOSED", label: "Cierre" },
] as const;

const statusLabels: Record<IncidentStatus, string> = Object.fromEntries(
  statuses.map((status) => [status.value, status.label]),
) as Record<IncidentStatus, string>;

const severityLabels: Record<IncidentSeverity, string> = Object.fromEntries(
  severities.map((severity) => [severity.value, severity.label]),
) as Record<IncidentSeverity, string>;

const typeLabels: Record<IncidentType, string> = {
  FIRE: "Fuego",
  SMOKE: "Humo",
  ILLEGAL_LOGGING: "Tala",
  POLLUTION: "Contaminacion",
  OTHER: "Otro",
};

const eventLabels: Record<string, string> = {
  INCIDENT_CREATED: "Incidente reportado",
  INCIDENT_UPDATED: "Incidente actualizado",
  INCIDENT_PRIORITIZED: "Incidente priorizado",
  BRIGADE_ASSIGNED: "Brigada asignada",
  ATTENTION_STARTED: "Atencion iniciada",
  INCIDENT_CLOSED: "Incidente cerrado",
  INCIDENT_REOPENED: "Incidente reabierto",
  INCIDENT_DELETED: "Incidente eliminado",
};

const roleLabels: Record<UserRole, string> = {
  ADMIN: "Administrador",
  CITIZEN: "Ciudadano",
};

const demoUsers = [
  { username: "admin", password: "admin123", label: "Administrador" },
  { username: "ciudadano", password: "ciudadano123", label: "Ciudadano" },
];

const emptyForm: CreateIncidentPayload = {
  title: "",
  description: "",
  type: "SMOKE",
  severity: "MEDIUM",
  latitude: -17.7833,
  longitude: -63.1821,
};

const emptyFilters: IncidentFilters = {
  status: "",
  type: "",
  severity: "",
  search: "",
};

const statusTone: Record<IncidentStatus, string> = {
  CREATED: "tone-neutral",
  PRIORITIZED: "tone-warning",
  ASSIGNED: "tone-info",
  IN_ATTENTION: "tone-active",
  CLOSED: "tone-success",
  REOPENED: "tone-danger",
};

const severityTone: Record<IncidentSeverity, string> = {
  LOW: "tone-neutral",
  MEDIUM: "tone-info",
  HIGH: "tone-warning",
  CRITICAL: "tone-danger",
};

function formatDate(value: string | null) {
  return value ? new Date(value).toLocaleString() : "Pendiente";
}

function getNextActionLabel(status: IncidentStatus) {
  switch (status) {
    case "CREATED":
      return "Priorizar";
    case "PRIORITIZED":
    case "REOPENED":
      return "Asignar brigada";
    case "ASSIGNED":
      return "Iniciar atencion";
    case "IN_ATTENTION":
      return "Cerrar con checklist";
    case "CLOSED":
      return "Reabrir si es necesario";
  }
}

function App() {
  const [session, setSession] = useState<AuthSession | null>(() => getStoredSession());
  const [loginForm, setLoginForm] = useState({ username: "ciudadano", password: "ciudadano123" });
  const [form, setForm] = useState<CreateIncidentPayload>(emptyForm);
  const [filters, setFilters] = useState<IncidentFilters>(emptyFilters);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [selected, setSelected] = useState<Incident | null>(null);
  const [events, setEvents] = useState<IncidentEvent[]>([]);
  const [brigades, setBrigades] = useState<Brigade[]>([]);
  const [summary, setSummary] = useState<IncidentSummary | null>(null);
  const [selectedBrigadeId, setSelectedBrigadeId] = useState("");
  const [closureChecklist, setClosureChecklist] = useState({
    areaSecured: false,
    riskControlled: false,
  });
  const [closureNotes, setClosureNotes] = useState("");
  const [reopenReason, setReopenReason] = useState("");
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const selectedPoint = useMemo(
    () => ({ latitude: form.latitude, longitude: form.longitude }),
    [form.latitude, form.longitude],
  );

  const mapMarkers = useMemo(
    () =>
      incidents.map((incident) => ({
        id: incident.id,
        title: incident.title,
        status: statusLabels[incident.status],
        latitude: incident.latitude,
        longitude: incident.longitude,
      })),
    [incidents],
  );

  const assignedBrigade = selected?.assignedBrigadeId
    ? brigades.find((brigade) => brigade.id === selected.assignedBrigadeId)
    : null;

  const canEdit = selected?.status !== "CLOSED";
  const isAdmin = hasAnyRole(session, "ADMIN");
  const canCreate = hasAnyRole(session, "ADMIN", "CITIZEN");
  const canUpdateIncident = isAdmin;
  const canDeleteIncident = isAdmin;
  const canListBrigades = isAdmin;
  const canViewEvents = isAdmin;
  const canClose =
    selected?.status === "IN_ATTENTION" &&
    closureChecklist.areaSecured &&
    closureChecklist.riskControlled &&
    closureNotes.trim().length > 0;

  const refreshEvents = useCallback(async (incidentId: string | null) => {
    if (!incidentId) {
      setEvents([]);
      return;
    }
    setEvents(await getIncidentEvents(incidentId));
  }, []);

  const refreshIncidents = useCallback(
    async (activeFilters: IncidentFilters = filters) => {
      setLoading(true);
      setError(null);
      try {
        const [data, nextSummary] = await Promise.all([
          listIncidents(activeFilters),
          getIncidentSummary(),
        ]);
        setIncidents(data);
        setSummary(nextSummary);
        setSelected((current) =>
          current ? (data.find((incident) => incident.id === current.id) ?? current) : current,
        );
      } catch (err) {
        setError(err instanceof Error ? err.message : "No se pudieron cargar los incidentes");
      } finally {
        setLoading(false);
      }
    },
    [filters],
  );

  useEffect(() => {
    function handleExpiredSession() {
      setSession(null);
      setSelected(null);
      setIncidents([]);
      setEvents([]);
      setBrigades([]);
      setSummary(null);
      setSuccess(null);
      setError("Sesion expirada o sin permisos. Inicia sesion nuevamente.");
    }

    window.addEventListener(SESSION_EXPIRED_EVENT, handleExpiredSession);
    return () => window.removeEventListener(SESSION_EXPIRED_EVENT, handleExpiredSession);
  }, []);

  useEffect(() => {
    if (!session) return;
    void refreshIncidents();
    if (canListBrigades) {
      void listBrigades().then(setBrigades).catch(() => setBrigades([]));
    }
  }, [canListBrigades, refreshIncidents, session]);

  useEffect(() => {
    if (!canViewEvents) {
      setEvents([]);
      return;
    }
    void refreshEvents(selected?.id ?? null);
  }, [canViewEvents, refreshEvents, selected?.id]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(null);
    try {
      const saved = editingId ? await updateIncident(editingId, form) : await createIncident(form);
      setForm(emptyForm);
      setEditingId(null);
      await refreshIncidents();
      setSelected(saved);
      await refreshEvents(saved.id);
      setSuccess(editingId ? "Incidente actualizado correctamente." : "Incidente reportado correctamente.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo guardar el incidente");
    } finally {
      setSaving(false);
    }
  }

  async function handleLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const nextSession = await login(loginForm.username, loginForm.password);
      storeSession(nextSession);
      setSession(nextSession);
      setSuccess(`Sesion iniciada como ${nextSession.displayName}.`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo iniciar sesion");
    } finally {
      setSaving(false);
    }
  }

  function handleLogout() {
    clearSession();
    setSession(null);
    setSelected(null);
    setIncidents([]);
    setEvents([]);
    setSummary(null);
    setSuccess(null);
    setError(null);
  }

  async function handleSelect(id: string) {
    setError(null);
    setSuccess(null);
    try {
      const incident = await getIncident(id);
      setSelected(incident);
      setSelectedBrigadeId(incident.assignedBrigadeId ?? brigades[0]?.id ?? "");
      setClosureChecklist({ areaSecured: false, riskControlled: false });
      setClosureNotes("");
      setReopenReason("");
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo abrir el incidente");
    }
  }

  async function runAction(action: () => Promise<Incident>) {
    if (!selected) return;
    setSaving(true);
    setError(null);
    setSuccess(null);
    try {
      const updated = await action();
      setSelected(updated);
      await refreshIncidents();
      await refreshEvents(updated.id);
      setSuccess(`Operacion completada. Estado actual: ${statusLabels[updated.status]}.`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo ejecutar la accion");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    if (!selected) return;
    if (!confirm("Eliminar este incidente?")) return;
    setError(null);
    setSuccess(null);
    try {
      await deleteIncident(selected.id);
      setSelected(null);
      setEvents([]);
      if (editingId === selected.id) {
        setEditingId(null);
        setForm(emptyForm);
      }
      await refreshIncidents();
      setSuccess("Incidente eliminado correctamente.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo eliminar el incidente");
    }
  }

  function startEdit(incident: Incident) {
    setEditingId(incident.id);
    setForm({
      title: incident.title,
      description: incident.description ?? "",
      type: incident.type,
      severity: incident.severity,
      latitude: incident.latitude,
      longitude: incident.longitude,
    });
  }

  function resetForm() {
    setEditingId(null);
    setForm(emptyForm);
  }

  function updateFilters(nextFilters: IncidentFilters) {
    setFilters(nextFilters);
    void refreshIncidents(nextFilters);
  }

  function clearFilters() {
    setFilters(emptyFilters);
    void refreshIncidents(emptyFilters);
  }

  if (!session) {
    return (
      <main className="login-shell">
        <section className="login-panel">
          <div>
            <span>BosqueVivo AI</span>
            <h1>MVP 1.0</h1>
            <p>Ingresa con un usuario demo para reportar o administrar incidentes ambientales.</p>
          </div>

          {error && <div className="notice error-notice">{error}</div>}

          <form onSubmit={handleLogin}>
            <label>
              Usuario
              <input
                value={loginForm.username}
                onChange={(event) =>
                  setLoginForm((current) => ({ ...current, username: event.target.value }))
                }
              />
            </label>
            <label>
              Contrasena
              <input
                type="password"
                value={loginForm.password}
                onChange={(event) =>
                  setLoginForm((current) => ({ ...current, password: event.target.value }))
                }
              />
            </label>
            <button disabled={saving} type="submit">
              {saving ? "Ingresando..." : "Ingresar"}
            </button>
          </form>

          <div className="demo-access">
            {demoUsers.map((user) => (
              <button
                className="secondary-button"
                key={user.username}
                type="button"
                onClick={() => setLoginForm({ username: user.username, password: user.password })}
              >
                {user.label}
              </button>
            ))}
          </div>
        </section>
      </main>
    );
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <h1>BosqueVivo AI</h1>
          <p>MVP 1.0 - Integracion limpia IAM + Core Platform + AI Service</p>
        </div>
        <div className="session-box">
          <span>{session.displayName}</span>
          <strong>{roleLabels[session.role]}</strong>
          <button className="secondary-button" type="button" onClick={() => void refreshIncidents()}>
            Actualizar
          </button>
          <button className="danger-button" type="button" onClick={handleLogout}>
            Salir
          </button>
        </div>
      </header>

      {summary && (
        <section className="overview">
          <div className="overview-copy">
            <span>Centro de respuesta ambiental</span>
            <h2>{summary.open} incidentes abiertos</h2>
            <p>
              Registra reportes, prioriza riesgos, asigna brigadas y deja trazabilidad del caso
              sin salir del panel.
            </p>
          </div>
          <div className="kpi-grid">
            <div><strong>{summary.total}</strong><span>Total</span></div>
            <div><strong>{summary.assigned}</strong><span>Asignados</span></div>
            <div><strong>{summary.inAttention}</strong><span>En atencion</span></div>
            <div><strong>{summary.closed}</strong><span>Cerrados</span></div>
            <div><strong>{summary.critical}</strong><span>Criticos</span></div>
          </div>
        </section>
      )}

      {error && <div className="notice error-notice">{error}</div>}
      {success && <div className="notice success-notice">{success}</div>}

      <section className="workspace">
        <form className="panel form-panel" onSubmit={handleSubmit}>
          <div className="panel-header">
            <h2>{editingId ? "Editar incidente" : "Nuevo incidente"}</h2>
            {editingId && (
              <button className="secondary-button" type="button" onClick={resetForm}>
                Cancelar
              </button>
            )}
          </div>
          <p className="panel-help">
            Usa el mapa para marcar el punto exacto. Los campos minimos son titulo, tipo,
            severidad y ubicacion.
          </p>

          <label>
            Titulo
            <input
              value={form.title}
              maxLength={140}
              required
              onChange={(event) => setForm((current) => ({ ...current, title: event.target.value }))}
            />
          </label>

          <div className="two-fields">
            <label>
              Tipo
              <select
                value={form.type}
                onChange={(event) =>
                  setForm((current) => ({ ...current, type: event.target.value as IncidentType }))
                }
              >
                {incidentTypes.map((type) => (
                  <option key={type.value} value={type.value}>{type.label}</option>
                ))}
              </select>
            </label>

            <label>
              Severidad
              <select
                value={form.severity}
                onChange={(event) =>
                  setForm((current) => ({
                    ...current,
                    severity: event.target.value as IncidentSeverity,
                  }))
                }
              >
                {severities.map((severity) => (
                  <option key={severity.value} value={severity.value}>{severity.label}</option>
                ))}
              </select>
            </label>
          </div>

          <label>
            Descripcion
            <textarea
              value={form.description}
              rows={4}
              onChange={(event) =>
                setForm((current) => ({ ...current, description: event.target.value }))
              }
            />
          </label>

          <div className="map-label">Ubicacion</div>
          <IncidentMap
            value={selectedPoint}
            onChange={(point) =>
              setForm((current) => ({
                ...current,
                latitude: point.latitude,
                longitude: point.longitude,
              }))
            }
          />

          <div className="coordinates">
            <span>Lat: {form.latitude.toFixed(6)}</span>
            <span>Lng: {form.longitude.toFixed(6)}</span>
          </div>

          {!canCreate && (
            <p className="role-warning">Tu rol puede consultar incidentes, pero no crear nuevos reportes.</p>
          )}

          <button type="submit" disabled={saving || !canCreate}>
            {saving ? "Guardando..." : editingId ? "Guardar cambios" : "Reportar incidente"}
          </button>
        </form>

        <section className="panel list-panel">
          <div className="panel-header">
            <h2>Incidentes</h2>
            <span>{loading ? "Cargando" : `${incidents.length} registros`}</span>
          </div>

          <div className="filters">
            <label>
              Estado
              <select
                value={filters.status}
                onChange={(event) =>
                  updateFilters({ ...filters, status: event.target.value as IncidentStatus | "" })
                }
              >
                <option value="">Todos</option>
                {statuses.map((status) => (
                  <option key={status.value} value={status.value}>{status.label}</option>
                ))}
              </select>
            </label>

            <label>
              Tipo
              <select
                value={filters.type}
                onChange={(event) =>
                  updateFilters({ ...filters, type: event.target.value as IncidentType | "" })
                }
              >
                <option value="">Todos</option>
                {incidentTypes.map((type) => (
                  <option key={type.value} value={type.value}>{type.label}</option>
                ))}
              </select>
            </label>

            <label>
              Severidad
              <select
                value={filters.severity}
                onChange={(event) =>
                  updateFilters({
                    ...filters,
                    severity: event.target.value as IncidentSeverity | "",
                  })
                }
              >
                <option value="">Todas</option>
                {severities.map((severity) => (
                  <option key={severity.value} value={severity.value}>{severity.label}</option>
                ))}
              </select>
            </label>

            <label>
              Buscar
              <input
                placeholder="Titulo o descripcion"
                value={filters.search}
                onChange={(event) => updateFilters({ ...filters, search: event.target.value })}
              />
            </label>
          </div>
          <button className="secondary-button full-button" type="button" onClick={clearFilters}>
            Limpiar filtros
          </button>

          <div className="incident-list">
            {incidents.map((incident) => (
              <button
                className={selected?.id === incident.id ? "incident-row selected" : "incident-row"}
                key={incident.id}
                onClick={() => void handleSelect(incident.id)}
                type="button"
              >
                <div className="row-title">
                  <strong>{incident.title}</strong>
                  <span className={`badge ${statusTone[incident.status]}`}>
                    {statusLabels[incident.status]}
                  </span>
                </div>
                <span>{typeLabels[incident.type]} - {severityLabels[incident.severity]}</span>
                <small>
                  Prioridad {incident.priorityScore}/100 - {getNextActionLabel(incident.status)}
                </small>
              </button>
            ))}
            {!loading && incidents.length === 0 && (
              <p className="empty-state">No hay incidentes con los filtros actuales.</p>
            )}
          </div>
        </section>

        <section className="panel detail-panel">
          <div className="panel-header">
            <h2>Flujo guiado</h2>
          </div>

          {selected ? (
            <div className="detail-content">
              <div className="detail-title">
                <h3>{selected.title}</h3>
                <div className="badge-row">
                  <span className={`badge ${statusTone[selected.status]}`}>
                    {statusLabels[selected.status]}
                  </span>
                  <span className={`badge ${severityTone[selected.severity]}`}>
                    {severityLabels[selected.severity]}
                  </span>
                </div>
                <p>{selected.description || "Sin descripcion."}</p>
              </div>

              <WorkflowStepper status={selected.status} />

              <section className="next-action-card">
                <span>Siguiente accion recomendada</span>
                <strong>{getNextActionLabel(selected.status)}</strong>
              </section>

              <dl>
                <div><dt>Tipo</dt><dd>{typeLabels[selected.type]}</dd></div>
                <div><dt>Prioridad</dt><dd>{selected.priorityScore}/100</dd></div>
                <div><dt>Brigada</dt><dd>{assignedBrigade?.name ?? "Sin asignar"}</dd></div>
                <div><dt>SLA</dt><dd>{selected.slaDueAt ? formatDate(selected.slaDueAt) : "Sin priorizar"}</dd></div>
                <div><dt>Creado</dt><dd>{formatDate(selected.createdAt)}</dd></div>
                <div><dt>Cierre</dt><dd>{formatDate(selected.closedAt)}</dd></div>
              </dl>

              {selected.priorityReason && <p className="reason-box">{selected.priorityReason}</p>}

              <GuidedAction
                assignedBrigade={assignedBrigade}
                brigades={brigades}
                checklist={closureChecklist}
                closureNotes={closureNotes}
                incident={selected}
                reopenReason={reopenReason}
                saving={saving}
                selectedBrigadeId={selectedBrigadeId}
                canClose={canClose}
                sessionRole={session.role}
                onAssign={() => void runAction(() => assignIncident(selected.id, selectedBrigadeId))}
                onChecklistChange={setClosureChecklist}
                onClose={() =>
                  void runAction(() =>
                    closeIncident(selected.id, {
                      areaSecured: closureChecklist.areaSecured,
                      riskControlled: closureChecklist.riskControlled,
                      notes: closureNotes,
                    }),
                  )
                }
                onPrioritize={() => void runAction(() => prioritizeIncident(selected.id))}
                onReopen={() => void runAction(() => reopenIncident(selected.id, reopenReason))}
                onReopenReasonChange={setReopenReason}
                onSelectedBrigadeChange={setSelectedBrigadeId}
                onStartAttention={() => void runAction(() => startIncidentAttention(selected.id))}
                onNotesChange={setClosureNotes}
              />

              <div className="detail-actions">
                <button
                  disabled={!canEdit || !canUpdateIncident}
                  type="button"
                  onClick={() => startEdit(selected)}
                >
                  Editar datos
                </button>
                <button
                  className="danger-button"
                  disabled={!canDeleteIncident}
                  type="button"
                  onClick={() => void handleDelete()}
                >
                  Eliminar
                </button>
              </div>

              {canViewEvents && (
                <div className="event-list">
                  <h4>Linea de tiempo</h4>
                  {events.map((event) => (
                    <div key={event.id}>
                      <strong>{eventLabels[event.type] ?? event.type}</strong>
                      <span>{new Date(event.createdAt).toLocaleString()}</span>
                      <p>{event.detail}</p>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ) : (
            <p className="empty-state">Selecciona un incidente para operar.</p>
          )}
        </section>

        <section className="panel map-panel">
          <div className="panel-header">
            <h2>Mapa de incidentes</h2>
            <span>{incidents.length} visibles</span>
          </div>
          <IncidentMap
            markers={mapMarkers}
            onMarkerClick={(id) => void handleSelect(id)}
            readOnly
            value={selected ? { latitude: selected.latitude, longitude: selected.longitude } : null}
          />
        </section>
      </section>
    </main>
  );
}

type GuidedActionProps = {
  assignedBrigade: Brigade | null | undefined;
  brigades: Brigade[];
  checklist: { areaSecured: boolean; riskControlled: boolean };
  closureNotes: string;
  incident: Incident;
  reopenReason: string;
  saving: boolean;
  selectedBrigadeId: string;
  canClose: boolean;
  sessionRole: UserRole;
  onAssign: () => void;
  onChecklistChange: (checklist: { areaSecured: boolean; riskControlled: boolean }) => void;
  onClose: () => void;
  onNotesChange: (notes: string) => void;
  onPrioritize: () => void;
  onReopen: () => void;
  onReopenReasonChange: (reason: string) => void;
  onSelectedBrigadeChange: (id: string) => void;
  onStartAttention: () => void;
};

function GuidedAction({
  assignedBrigade,
  brigades,
  checklist,
  closureNotes,
  incident,
  reopenReason,
  saving,
  selectedBrigadeId,
  canClose,
  sessionRole,
  onAssign,
  onChecklistChange,
  onClose,
  onNotesChange,
  onPrioritize,
  onReopen,
  onReopenReasonChange,
  onSelectedBrigadeChange,
  onStartAttention,
}: GuidedActionProps) {
  const canAdminAction = sessionRole === "ADMIN";

  if (incident.status === "CREATED") {
    return (
      <section className="guided-action">
        <span>Siguiente paso</span>
        <h4>Priorizar el incidente</h4>
        <p>El sistema calculara prioridad y SLA usando reglas deterministicas del MVP.</p>
        <button disabled={saving || !canAdminAction} type="button" onClick={onPrioritize}>Priorizar</button>
        {!canAdminAction && <p>Solo administradores pueden priorizar.</p>}
      </section>
    );
  }

  if (incident.status === "PRIORITIZED" || incident.status === "REOPENED") {
    return (
      <section className="guided-action">
        <span>Siguiente paso</span>
        <h4>Asignar brigada</h4>
        <p>Selecciona una brigada disponible para coordinar la atencion en campo.</p>
        <select value={selectedBrigadeId} onChange={(event) => onSelectedBrigadeChange(event.target.value)}>
          <option value="">Seleccionar brigada</option>
          {brigades.map((brigade) => (
            <option disabled={!brigade.available} key={brigade.id} value={brigade.id}>
              {brigade.name} - {brigade.zone}
            </option>
          ))}
        </select>
        <button disabled={saving || !selectedBrigadeId || !canAdminAction} type="button" onClick={onAssign}>Asignar</button>
        {!canAdminAction && <p>Solo administradores pueden asignar brigadas.</p>}
      </section>
    );
  }

  if (incident.status === "ASSIGNED") {
    return (
      <section className="guided-action">
        <span>Siguiente paso</span>
        <h4>Iniciar atencion</h4>
        <p>{assignedBrigade?.name ?? "La brigada asignada"} inicia el trabajo en campo.</p>
        <button disabled={saving || !canAdminAction} type="button" onClick={onStartAttention}>Iniciar atencion</button>
        {!canAdminAction && <p>Solo administradores pueden iniciar atencion.</p>}
      </section>
    );
  }

  if (incident.status === "IN_ATTENTION") {
    return (
      <section className="guided-action">
        <span>Siguiente paso</span>
        <h4>Cerrar con checklist</h4>
        <label className="check-row">
          <input
            checked={checklist.areaSecured}
            type="checkbox"
            onChange={(event) =>
              onChecklistChange({ ...checklist, areaSecured: event.target.checked })
            }
          />
          Area asegurada
        </label>
        <label className="check-row">
          <input
            checked={checklist.riskControlled}
            type="checkbox"
            onChange={(event) =>
              onChecklistChange({ ...checklist, riskControlled: event.target.checked })
            }
          />
          Riesgo controlado
        </label>
        <label>
          Notas de cierre
          <textarea
            rows={3}
            value={closureNotes}
            onChange={(event) => onNotesChange(event.target.value)}
          />
        </label>
        <button disabled={saving || !canClose || !canAdminAction} type="button" onClick={onClose}>Cerrar incidente</button>
        {!canAdminAction && <p>Solo administradores pueden cerrar incidentes.</p>}
      </section>
    );
  }

  return (
    <section className="guided-action">
      <span>Caso finalizado</span>
      <h4>Incidente cerrado</h4>
      <p>Para operar nuevamente este caso, registra un motivo y reabre el incidente.</p>
      <label>
        Motivo de reapertura
        <textarea
          rows={3}
          value={reopenReason}
          onChange={(event) => onReopenReasonChange(event.target.value)}
        />
      </label>
      <button disabled={saving || !reopenReason.trim() || !canAdminAction} type="button" onClick={onReopen}>Reabrir</button>
      {!canAdminAction && <p>Solo administradores pueden reabrir.</p>}
    </section>
  );
}

function hasAnyRole(session: AuthSession | null, ...roles: UserRole[]) {
  return Boolean(session && roles.includes(session.role));
}

function WorkflowStepper({ status }: { status: IncidentStatus }) {
  const normalizedStatus = status === "REOPENED" ? "PRIORITIZED" : status;
  const currentIndex = workflowSteps.findIndex((step) => step.status === normalizedStatus);

  return (
    <ol className="workflow-stepper">
      {workflowSteps.map((step, index) => (
        <li
          className={
            index < currentIndex
              ? "done"
              : index === currentIndex
                ? "current"
                : ""
          }
          key={step.status}
        >
          <span>{index + 1}</span>
          {step.label}
        </li>
      ))}
    </ol>
  );
}

export default App;
