import { useEffect, useState } from 'react'
import { AlertTriangle, CheckCircle, Clock } from 'lucide-react'
import { getIncidents } from '../api/dashboard'
import LoadingSpinner from '../components/LoadingSpinner'
import { IncidentStatusBadge } from '../components/StatusBadge'
import { formatDistanceToNow, format } from 'date-fns'

export default function Incidents() {
  const [incidents, setIncidents] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('ALL')

  useEffect(() => {
    getIncidents().then(setIncidents).finally(() => setLoading(false))
  }, [])

  const filtered = incidents.filter(i => filter === 'ALL' || i.status === filter)
  const openCount = incidents.filter(i => i.status === 'OPEN').length

  if (loading) return <LoadingSpinner />

  return (
    <div className="max-w-4xl space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Incidents</h1>
          <p className="text-slate-400 text-sm mt-1">
            {openCount > 0 ? (
              <span className="text-red-400">{openCount} open incident{openCount !== 1 ? 's' : ''}</span>
            ) : (
              <span className="text-emerald-400">All systems operational</span>
            )}
          </p>
        </div>
        <div className="flex gap-2">
          {['ALL', 'OPEN', 'RESOLVED'].map(f => (
            <button key={f} onClick={() => setFilter(f)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                filter === f ? 'bg-brand-600 text-white' : 'bg-slate-800 text-slate-400 hover:text-white'
              }`}>
              {f}
            </button>
          ))}
        </div>
      </div>

      {filtered.length === 0 ? (
        <div className="card text-center py-16">
          <CheckCircle size={32} className="text-emerald-500 mx-auto mb-3" />
          <p className="text-slate-400">No {filter === 'ALL' ? '' : filter.toLowerCase()} incidents</p>
        </div>
      ) : (
        <div className="space-y-3">
          {filtered.map(i => (
            <div key={i.id} className={`card border-l-4 ${i.status === 'OPEN' ? 'border-l-red-500' : 'border-l-emerald-600'}`}>
              <div className="flex items-start justify-between gap-4">
                <div className="flex items-start gap-3 min-w-0">
                  <div className={`mt-0.5 ${i.status === 'OPEN' ? 'text-red-400' : 'text-emerald-400'}`}>
                    {i.status === 'OPEN' ? <AlertTriangle size={16} /> : <CheckCircle size={16} />}
                  </div>
                  <div className="min-w-0">
                    <div className="flex items-center gap-2 flex-wrap">
                      <p className="font-semibold text-white">{i.monitorName}</p>
                      <IncidentStatusBadge status={i.status} />
                      {i.failureCount > 1 && (
                        <span className="text-xs text-slate-500">{i.failureCount} failures</span>
                      )}
                    </div>
                    <p className="text-sm text-slate-400 mt-0.5 truncate">{i.reason || 'Check failed'}</p>
                    <p className="text-xs text-slate-500 mt-1 truncate">{i.monitorUrl}</p>
                  </div>
                </div>
                <div className="text-right shrink-0 space-y-1">
                  <div className="flex items-center gap-1 text-xs text-slate-500 justify-end">
                    <Clock size={10} />
                    Started {formatDistanceToNow(new Date(i.startedAt), { addSuffix: true })}
                  </div>
                  {i.resolvedAt && (
                    <p className="text-xs text-emerald-500">
                      Resolved {formatDistanceToNow(new Date(i.resolvedAt), { addSuffix: true })}
                    </p>
                  )}
                  {i.status === 'OPEN' && (
                    <p className="text-xs text-red-400">
                      Duration: {formatDistanceToNow(new Date(i.startedAt))}
                    </p>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
