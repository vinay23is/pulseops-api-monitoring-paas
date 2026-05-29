import { useEffect, useState } from 'react'
import { Bell } from 'lucide-react'
import { getAlerts } from '../api/dashboard'
import LoadingSpinner from '../components/LoadingSpinner'
import { AlertTypeBadge, AlertStatusBadge } from '../components/StatusBadge'
import { formatDistanceToNow } from 'date-fns'

export default function Alerts() {
  const [alerts, setAlerts] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('ALL')

  useEffect(() => {
    getAlerts().then(setAlerts).finally(() => setLoading(false))
  }, [])

  const filtered = alerts.filter(a => filter === 'ALL' || a.type === filter)

  if (loading) return <LoadingSpinner />

  return (
    <div className="max-w-4xl space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Alerts</h1>
          <p className="text-slate-400 text-sm mt-1">{alerts.length} total alert{alerts.length !== 1 ? 's' : ''}</p>
        </div>
        <div className="flex gap-2 flex-wrap">
          {['ALL', 'IN_APP', 'MOCK_EMAIL', 'WEBHOOK'].map(f => (
            <button key={f} onClick={() => setFilter(f)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                filter === f ? 'bg-brand-600 text-white' : 'bg-slate-800 text-slate-400 hover:text-white'
              }`}>
              {f.replace('_', ' ')}
            </button>
          ))}
        </div>
      </div>

      {filtered.length === 0 ? (
        <div className="card text-center py-16">
          <Bell size={32} className="text-slate-600 mx-auto mb-3" />
          <p className="text-slate-400">No alerts yet</p>
        </div>
      ) : (
        <div className="space-y-2">
          {filtered.map(a => (
            <div key={a.id} className="card py-4">
              <div className="flex items-start justify-between gap-4">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 flex-wrap mb-1">
                    <AlertTypeBadge type={a.type} />
                    <AlertStatusBadge status={a.status} />
                    <span className="text-xs text-slate-500 font-medium">{a.monitorName}</span>
                  </div>
                  <p className="text-sm text-slate-300 leading-relaxed">{a.message}</p>
                  {a.failureReason && (
                    <p className="text-xs text-red-400 mt-1">Error: {a.failureReason}</p>
                  )}
                  {a.webhookUrl && (
                    <p className="text-xs text-slate-500 mt-1 font-mono truncate">{a.webhookUrl}</p>
                  )}
                </div>
                <p className="text-xs text-slate-500 shrink-0 whitespace-nowrap">
                  {formatDistanceToNow(new Date(a.createdAt), { addSuffix: true })}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
