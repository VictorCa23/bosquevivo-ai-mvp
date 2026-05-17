export type IncidentType = "FIRE" | "SMOKE" | "ILLEGAL_LOGGING" | "POLLUTION" | "OTHER";
export type IncidentStatus =
  | "CREATED"
  | "PRIORITIZED"
  | "ASSIGNED"
  | "IN_ATTENTION"
  | "CLOSED"
  | "REOPENED";
export type IncidentSeverity = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";

export type Incident = {
  id: string;
  title: string;
  description: string | null;
  type: IncidentType;
  status: IncidentStatus;
  severity: IncidentSeverity;
  priorityScore: number;
  priorityReason: string | null;
  slaDueAt: string | null;
  assignedBrigadeId: string | null;
  assignedAt: string | null;
  attentionStartedAt: string | null;
  closedAt: string | null;
  closureNotes: string | null;
  latitude: number;
  longitude: number;
  createdAt: string;
  updatedAt: string;
};

export type Brigade = {
  id: string;
  name: string;
  zone: string;
  available: boolean;
};

export type IncidentEvent = {
  id: string;
  incidentId: string;
  type: string;
  detail: string;
  createdAt: string;
};

export type IncidentSummary = {
  total: number;
  open: number;
  prioritized: number;
  assigned: number;
  inAttention: number;
  closed: number;
  critical: number;
};

export type CreateIncidentPayload = {
  title: string;
  description: string;
  type: IncidentType;
  severity: IncidentSeverity;
  latitude: number;
  longitude: number;
};

export type UpdateIncidentPayload = CreateIncidentPayload;

export type IncidentFilters = {
  status?: IncidentStatus | "";
  type?: IncidentType | "";
  severity?: IncidentSeverity | "";
  search?: string;
};

export type UserRole = "ADMIN" | "CITIZEN";

export type AuthSession = {
  token: string;
  username: string;
  displayName: string;
  role: UserRole;
};

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8082";
const IAM_URL = import.meta.env.VITE_IAM_URL ?? "http://localhost:8089";
const SESSION_KEY = "bosquevivo.session";
export const SESSION_EXPIRED_EVENT = "bosquevivo:session-expired";

const demoUsersByRole: Record<UserRole, string> = {
  ADMIN: "admin",
  CITIZEN: "ciudadano",
};

export function getStoredSession(): AuthSession | null {
  const raw = localStorage.getItem(SESSION_KEY);
  if (!raw) return null;
  try {
    const parsed = JSON.parse(raw) as AuthSession;
    if (!isSupportedSession(parsed)) {
      localStorage.removeItem(SESSION_KEY);
      return null;
    }
    return parsed;
  } catch {
    localStorage.removeItem(SESSION_KEY);
    return null;
  }
}

export function storeSession(session: AuthSession) {
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function clearSession() {
  localStorage.removeItem(SESSION_KEY);
}

function isSupportedSession(session: Partial<AuthSession>): session is AuthSession {
  if (!session.token || !session.username || !session.displayName || !session.role) {
    return false;
  }
  if (session.role !== "ADMIN" && session.role !== "CITIZEN") {
    return false;
  }
  return demoUsersByRole[session.role] === session.username;
}

function expireSession() {
  clearSession();
  window.dispatchEvent(new Event(SESSION_EXPIRED_EVENT));
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const token = getStoredSession()?.token;
  const response = await fetch(`${API_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options?.headers,
    },
    ...options,
  });

  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      expireSession();
      throw new Error("Sesion expirada o sin permisos. Inicia sesion nuevamente.");
    }
    const message = await readErrorMessage(response);
    throw new Error(message || `Request failed with status ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export function login(username: string, password: string): Promise<AuthSession> {
  return authRequest<AuthSession>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
}

async function authRequest<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${IAM_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options?.headers,
    },
    ...options,
  });

  if (!response.ok) {
    const message = await readErrorMessage(response);
    throw new Error(message || `Request failed with status ${response.status}`);
  }

  return response.json() as Promise<T>;
}

async function readErrorMessage(response: Response): Promise<string> {
  const body = await response.text();
  if (!body) return "";

  try {
    const parsed = JSON.parse(body) as { message?: string; error?: string };
    return parsed.message || parsed.error || body;
  } catch {
    return body;
  }
}

export function listIncidents(filters: IncidentFilters = {}): Promise<Incident[]> {
  const params = new URLSearchParams();
  if (filters.status) params.set("status", filters.status);
  if (filters.type) params.set("type", filters.type);
  if (filters.severity) params.set("severity", filters.severity);
  if (filters.search?.trim()) params.set("search", filters.search.trim());
  const query = params.toString();
  return request<Incident[]>(`/api/incidents${query ? `?${query}` : ""}`);
}

export function getIncidentSummary(): Promise<IncidentSummary> {
  return request<IncidentSummary>("/api/incidents/summary");
}

export function createIncident(payload: CreateIncidentPayload): Promise<Incident> {
  return request<Incident>("/api/incidents", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function getIncident(id: string): Promise<Incident> {
  return request<Incident>(`/api/incidents/${id}`);
}

export function getIncidentEvents(id: string): Promise<IncidentEvent[]> {
  return request<IncidentEvent[]>(`/api/incidents/${id}/events`);
}

export function updateIncident(id: string, payload: UpdateIncidentPayload): Promise<Incident> {
  return request<Incident>(`/api/incidents/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
}

export function deleteIncident(id: string): Promise<void> {
  return request<void>(`/api/incidents/${id}`, {
    method: "DELETE",
  });
}

export function prioritizeIncident(id: string): Promise<Incident> {
  return request<Incident>(`/api/incidents/${id}/prioritize`, { method: "POST" });
}

export function assignIncident(id: string, brigadeId: string): Promise<Incident> {
  return request<Incident>(`/api/incidents/${id}/assign`, {
    method: "POST",
    body: JSON.stringify({ brigadeId }),
  });
}

export function startIncidentAttention(id: string): Promise<Incident> {
  return request<Incident>(`/api/incidents/${id}/start-attention`, { method: "POST" });
}

export function closeIncident(
  id: string,
  payload: { areaSecured: boolean; riskControlled: boolean; notes: string },
): Promise<Incident> {
  return request<Incident>(`/api/incidents/${id}/close`, {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function reopenIncident(id: string, reason: string): Promise<Incident> {
  return request<Incident>(`/api/incidents/${id}/reopen`, {
    method: "POST",
    body: JSON.stringify({ reason }),
  });
}

export function listBrigades(): Promise<Brigade[]> {
  return request<Brigade[]>("/api/brigades");
}
