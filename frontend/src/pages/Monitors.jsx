import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { Plus, Trash2, Pencil, ToggleLeft, ToggleRight, Activity, ArrowLeft } from 'lucide-react'
import { listMonitors, createMonitor, updateMonitor, deleteMonitor, toggleMonitor } from '../api/monitors'
import Modal from '../components/Modal'
import LoadingSpinner from '../components/LoadingSpinner'
import { MonitorStatusBadge } from '../components/StatusBadge'
import { formatDistanceToNow } from 'date-fns'

const defaultForm = {
  name: '', url: '', method: 'GET', expectedStatusCode: 200,
  intervalSeconds: 60, timeoutSeconds: 10, active: true,
}

export default function Monitors() {
  const { projectId } = useParams()
  const [monitors, setMonitors] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(defaultForm)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    listMonitors(projectId).then(setMonitors).finally(() => setLoading(false))
  }, [projectId])

  function openCreate() { setEditing(null); setForm(defaultForm); setError(''); setShowModal(true) }
  function openEdit(m) {
    setEditing(m.id)
    setForm({ name: m.name, url: m.url, method: m.method, expectedStatusCode: m.expectedStatusCode,
               intervalSeconds: m.intervalSeconds, timeoutSeconds: m.timeoutSeconds, active: m.active })
    setError('')
    setShowModal(true)
  }

  async function handleSave(e) {
    e.preventDefault()
    setSaving(true)
    setError('')
    try {
      if (editing) {
        const updated = await updateMonitor(projectId, editing, form)
        setMonitors(ms => ms.map(m => m.id === editing ? updated : m))
      } else {
        const created = await createMonitor(projectId, form)
        setMonitors(ms => [...ms, created])
      }
      setShowModal(false)
    } catch (err) {
      setError(err.response?.data?.message || 'Save failed')
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(m) {
    if (!confirm(`Delete monitor "${m.name}"?`)) return
    await deleteMonitor(projectId, m.id)
    setMonitors(ms => ms.filter(x => x.id !== m.id))
  }

  async function handleToggle(m) {
    const updated = await toggleMonitor(projectId, m.id)
    setMonitors(ms => ms.map(x => x.id === m.id ? updated : x))
  }

  if (loading) return <LoadingSpinner />

  return (
    <div className="max-w-5xl space-y-6">
      <div className="flex items-center gap-3">
        <Link to="/projects" className="text-slate-400 hover:text-white transition-colors">
          <ArrowLeft size={18} />
        </Link>
        <div className="flex-1">
          <h1 className="text-2xl font-bold text-white">Monitors</h1>
          <p className="text-slate-400 text-sm mt-1">Configure endpoints to watch</p>
        </div>
        <button onClick={openCreate} className="btn-primary flex items-center gap-2">
          <Plus size={16} /> Add monitor
        </button>
      </div>

      {monitors.length === 0 ? (
        <div className="card text-center py-16">
          <Activity size={32} className="text-slate-600 mx-auto mb-3" />
          <p className="text-slate-400 mb-4">No monitors yet. Add one to start checking your endpoints.</p>
          <button onClick={openCreate} className="btn-primary inline-flex items-center gap-2">
            <Plus size={16} /> Add first monitor
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          {monitors.map(m => (
            <div key={m.id} className="card flex items-center gap-4 hover:border-slate-700 transition-colors">
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <span className="font-semibold text-white">{m.name}</span>
                  <MonitorStatusBadge active={m.active} />
                  <span className="text-xs bg-slate-800 text-slate-400 px-2 py-0.5 rounded font-mono">{m.method}</span>
                  <span className="text-xs text-slate-500">every {m.intervalSeconds}s</span>
                </div>
                <p className="text-sm text-slate-400 truncate mt-0.5">{m.url}</p>
                <p className="text-xs text-slate-500 mt-0.5">
                  Expects {m.expectedStatusCode} · timeout {m.timeoutSeconds}s ·
                  {m.lastCheckedAt ? ` checked ${formatDistanceToNow(new Date(m.lastCheckedAt), { addSuffix: true })}` : ' never checked'}
                </p>
              </div>
              <div className="flex items-center gap-1 shrink-0">
                <button
                  onClick={() => handleToggle(m)}
                  className="p-2 text-slate-400 hover:text-brand-400 transition-colors"
                  title={m.active ? 'Pause' : 'Resume'}
                >
                  {m.active ? <ToggleRight size={18} /> : <ToggleLeft size={18} />}
                </button>
                <button onClick={() => openEdit(m)} className="p-2 text-slate-400 hover:text-white transition-colors">
                  <Pencil size={15} />
                </button>
                <button onClick={() => handleDelete(m)} className="p-2 text-slate-400 hover:text-red-400 transition-colors">
                  <Trash2 size={15} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {showModal && (
        <Modal title={editing ? 'Edit Monitor' : 'Add Monitor'} onClose={() => setShowModal(false)}>
          <form onSubmit={handleSave} className="space-y-4">
            {error && <p className="text-sm text-red-400 bg-red-900/20 px-3 py-2 rounded-lg border border-red-800">{error}</p>}
            <div>
              <label className="label">Name</label>
              <input className="input" placeholder="API Health" value={form.name}
                onChange={e => setForm(f => ({ ...f, name: e.target.value }))} required />
            </div>
            <div>
              <label className="label">URL</label>
              <input className="input" placeholder="https://api.example.com/health" value={form.url}
                onChange={e => setForm(f => ({ ...f, url: e.target.value }))} required />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="label">Method</label>
                <select className="input" value={form.method} onChange={e => setForm(f => ({ ...f, method: e.target.value }))}>
                  {['GET', 'POST', 'PUT', 'DELETE', 'HEAD'].map(m => <option key={m}>{m}</option>)}
                </select>
              </div>
              <div>
                <label className="label">Expected Status</label>
                <input className="input" type="number" min="100" max="599" value={form.expectedStatusCode}
                  onChange={e => setForm(f => ({ ...f, expectedStatusCode: +e.target.value }))} />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="label">Interval (seconds)</label>
                <input className="input" type="number" min="10" max="3600" value={form.intervalSeconds}
                  onChange={e => setForm(f => ({ ...f, intervalSeconds: +e.target.value }))} />
              </div>
              <div>
                <label className="label">Timeout (seconds)</label>
                <input className="input" type="number" min="1" max="60" value={form.timeoutSeconds}
                  onChange={e => setForm(f => ({ ...f, timeoutSeconds: +e.target.value }))} />
              </div>
            </div>
            <div className="flex items-center gap-2">
              <input type="checkbox" id="active" checked={form.active}
                onChange={e => setForm(f => ({ ...f, active: e.target.checked }))}
                className="rounded border-slate-600 bg-slate-800 text-brand-500" />
              <label htmlFor="active" className="text-sm text-slate-300">Active (start monitoring immediately)</label>
            </div>
            <div className="flex gap-3 pt-1">
              <button type="submit" className="btn-primary flex-1" disabled={saving}>
                {saving ? 'Saving...' : editing ? 'Save changes' : 'Add monitor'}
              </button>
              <button type="button" className="btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  )
}
