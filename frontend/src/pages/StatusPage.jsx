import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { Zap, CheckCircle, AlertTriangle, Clock } from 'lucide-react'
import axios from 'axios'
import { formatDistanceToNow } from 'date-fns'
import { API_BASE_URL } from '../api/client'

export default function StatusPage() {
  const { slug } = useParams()
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    axios.get(`${API_BASE_URL}/status/${slug}`)
      .then(r => setData(r.data))
      .catch(() => setError('Status page not found'))
      .finally(() => setLoading(false))
  }, [slug])

  if (loading) return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center">
      <div className="w-7 h-7 border-2 border-slate-700 border-t-brand-500 rounded-full animate-spin" />
    </div>
  )

  if (error) return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center text-slate-400">{error}</div>
  )

  const isOperational = data.status === 'OPERATIONAL'

  return (
    <div className="min-h-screen bg-slate-950 text-white">
      <div className="max-w-3xl mx-auto px-6 py-12">
        {/* Header */}
        <div className="flex items-center gap-3 mb-10">
          <div className="w-9 h-9 rounded-xl bg-brand-600 flex items-center justify-center">
            <Zap size={16} className="text-white" />
          </div>
          <div>
            <h1 className="text-xl font-bold">{data.project.name}</h1>
            <p className="text-sm text-slate-400">Status Page</p>
          </div>
        </div>

        {/* Overall Status Banner */}
        <div className={`rounded-2xl px-6 py-5 mb-8 flex items-center gap-3 ${
          isOperational ? 'bg-emerald-900/30 border border-emerald-800' : 'bg-red-900/30 border border-red-800'
        }`}>
          {isOperational
            ? <CheckCircle size={24} className="text-emerald-400 shrink-0" />
            : <AlertTriangle size={24} className="text-red-400 shrink-0" />
          }
          <div>
            <p className={`font-bold text-lg ${isOperational ? 'text-emerald-300' : 'text-red-300'}`}>
              {isOperational ? 'All Systems Operational' : 'Degraded Performance'}
            </p>
            <p className="text-sm text-slate-400">
              {data.openIncidents} open incident{data.openIncidents !== 1 ? 's' : ''}
            </p>
          </div>
        </div>

        {/* Monitors */}
        <div className="card mb-6">
          <h2 className="font-semibold text-white mb-4">Services</h2>
          {data.monitors.length === 0 ? (
            <p className="text-slate-400 text-sm">No monitors configured.</p>
          ) : (
            <div className="space-y-3">
              {data.monitors.map(m => (
                <div key={m.id} className="flex items-center justify-between py-2 border-b border-slate-800 last:border-0">
                  <div>
                    <p className="font-medium text-slate-200">{m.name}</p>
                    <p className="text-xs text-slate-500 truncate max-w-xs">{m.url}</p>
                  </div>
                  <div className="flex items-center gap-3">
                    {m.lastCheckedAt && (
                      <span className="text-xs text-slate-500 flex items-center gap-1">
                        <Clock size={10} />
                        {formatDistanceToNow(new Date(m.lastCheckedAt), { addSuffix: true })}
                      </span>
                    )}
                    <span className={m.active ? 'badge-green' : 'badge-yellow'}>
                      {m.active ? 'Operational' : 'Paused'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Recent Incidents */}
        {data.recentIncidents?.length > 0 && (
          <div className="card">
            <h2 className="font-semibold text-white mb-4">Recent Incidents</h2>
            <div className="space-y-3">
              {data.recentIncidents.map(i => (
                <div key={i.id} className={`pl-3 border-l-2 py-1 ${i.status === 'OPEN' ? 'border-red-500' : 'border-emerald-600'}`}>
                  <div className="flex items-center gap-2">
                    <p className="text-sm font-medium text-slate-200">{i.monitorName}</p>
                    <span className={i.status === 'OPEN' ? 'badge-red' : 'badge-green'}>
                      {i.status === 'OPEN' ? 'Ongoing' : 'Resolved'}
                    </span>
                  </div>
                  <p className="text-xs text-slate-400 mt-0.5">{i.reason}</p>
                  <p className="text-xs text-slate-500 mt-0.5">
                    Started {formatDistanceToNow(new Date(i.startedAt), { addSuffix: true })}
                    {i.resolvedAt && ` · Resolved ${formatDistanceToNow(new Date(i.resolvedAt), { addSuffix: true })}`}
                  </p>
                </div>
              ))}
            </div>
          </div>
        )}

        <p className="text-center text-xs text-slate-600 mt-8">
          Powered by <a href="/" className="text-brand-500 hover:text-brand-400">PulseOps</a>
        </p>
      </div>
    </div>
  )
}
