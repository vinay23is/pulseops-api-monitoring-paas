export function MonitorStatusBadge({ active, hasOpenIncident }) {
  if (!active) return <span className="badge-yellow">Paused</span>
  if (hasOpenIncident) return <span className="badge-red"><span className="w-1.5 h-1.5 rounded-full bg-red-400 animate-pulse inline-block" />Down</span>
  return <span className="badge-green"><span className="w-1.5 h-1.5 rounded-full bg-emerald-400 inline-block" />Up</span>
}

export function IncidentStatusBadge({ status }) {
  if (status === 'OPEN') return <span className="badge-red">Open</span>
  return <span className="badge-green">Resolved</span>
}

export function AlertTypeBadge({ type }) {
  const map = {
    IN_APP:     'badge-blue',
    MOCK_EMAIL: 'badge-yellow',
    WEBHOOK:    'badge-brand',
  }
  return <span className={map[type] || 'badge-blue'}>{type?.replace('_', ' ')}</span>
}

export function AlertStatusBadge({ status }) {
  const map = { SENT: 'badge-green', FAILED: 'badge-red', PENDING: 'badge-yellow' }
  return <span className={map[status] || 'badge-yellow'}>{status}</span>
}
