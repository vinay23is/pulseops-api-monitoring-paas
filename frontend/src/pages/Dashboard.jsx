import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Activity, AlertTriangle, Clock, TrendingUp, ExternalLink } from 'lucide-react'
import { getDashboard } from '../api/dashboard'
import StatCard from '../components/StatCard'
import { MonitorStatusBadge, IncidentStatusBadge } from '../components/StatusBadge'
import LoadingSpinner from '../components/LoadingSpinner'
import { formatDistanceToNow } from 'date-fns'
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts'

export default function Dashboard() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getDashboard()
      .then(setData)
      .catch(() => setError('Failed to load dashboard'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <LoadingSpinner />
  if (error) return <p className="text-red-400 text-sm">{error}</p>

  const chartData = (data?.recentChecks || [])
    .slice(0, 20)
    .reverse()
    .map((c, i) => ({
      name: i,
      latency: c.latencyMs,
      success: c.success ? 1 : 0,
    }))

  return (
    <div className="space-y-6 max-w-6xl">
      <div>
        <h1 className="text-2xl font-bold text-white">Dashboard</h1>
        <p className="text-slate-400 text-sm mt-1">Your infrastructure at a glance</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total Monitors"    value={data?.totalMonitors ?? 0}    icon={Activity}       color="brand" />
        <StatCard title="Uptime (24h)"      value={`${data?.uptimePercentage ?? 100}%`} icon={TrendingUp} color="green" />
        <StatCard title="Avg Latency"       value={`${Math.round(data?.avgLatencyMs ?? 0)} ms`} icon={Clock} color="yellow" />
        <StatCard title="Open Incidents"    value={data?.openIncidents ?? 0}    icon={AlertTriangle}  color="red" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Latency Chart */}
        <div className="card">
          <h2 className="font-semibold text-white mb-4">Recent Check Latency</h2>
          {chartData.length > 0 ? (
            <ResponsiveContainer width="100%" height={180}>
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                <XAxis dataKey="name" hide />
                <YAxis tick={{ fontSize: 11, fill: '#64748b' }} unit="ms" width={45} />
                <Tooltip
                  contentStyle={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 8 }}
                  labelStyle={{ color: '#94a3b8' }}
                  formatter={v => [`${v} ms`, 'Latency']}
                />
                <Line type="monotone" dataKey="latency" stroke="#0ea5e9" strokeWidth={2} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-slate-500 text-sm py-8 text-center">No check data yet</p>
          )}
        </div>

        {/* Recent Incidents */}
        <div className="card">
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-semibold text-white">Recent Incidents</h2>
            <Link to="/incidents" className="text-xs text-brand-400 hover:text-brand-300 flex items-center gap-1">
              View all <ExternalLink size={11} />
            </Link>
          </div>
          {data?.recentIncidents?.length ? (
            <div className="space-y-2">
              {data.recentIncidents.map(i => (
                <div key={i.id} className="flex items-center justify-between py-2 border-b border-slate-800 last:border-0">
                  <div className="min-w-0">
                    <p className="text-sm font-medium text-slate-200 truncate">{i.monitorName}</p>
                    <p className="text-xs text-slate-500 truncate">{i.reason}</p>
                  </div>
                  <div className="flex items-center gap-2 ml-3 shrink-0">
                    <IncidentStatusBadge status={i.status} />
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-slate-500 text-sm py-4 text-center">No incidents — all clear!</p>
          )}
        </div>
      </div>

      {/* Monitor List */}
      <div className="card">
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-semibold text-white">Monitor Status</h2>
          <Link to="/projects" className="text-xs text-brand-400 hover:text-brand-300 flex items-center gap-1">
            Manage <ExternalLink size={11} />
          </Link>
        </div>
        {data?.monitors?.length ? (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-slate-500 text-xs border-b border-slate-800">
                  <th className="text-left pb-2 font-medium">Name</th>
                  <th className="text-left pb-2 font-medium">URL</th>
                  <th className="text-left pb-2 font-medium">Status</th>
                  <th className="text-left pb-2 font-medium">Last check</th>
                </tr>
              </thead>
              <tbody>
                {data.monitors.map(m => (
                  <tr key={m.id} className="border-b border-slate-800/50 last:border-0">
                    <td className="py-2.5 font-medium text-slate-200">{m.name}</td>
                    <td className="py-2.5 text-slate-400 max-w-[200px] truncate">{m.url}</td>
                    <td className="py-2.5"><MonitorStatusBadge active={m.active} /></td>
                    <td className="py-2.5 text-slate-500">
                      {m.lastCheckedAt ? formatDistanceToNow(new Date(m.lastCheckedAt), { addSuffix: true }) : 'Never'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-8">
            <p className="text-slate-400 mb-3">No monitors yet</p>
            <Link to="/projects" className="btn-primary text-sm">Create a project</Link>
          </div>
        )}
      </div>
    </div>
  )
}
