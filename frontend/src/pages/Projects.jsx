import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Plus, Trash2, ExternalLink, FolderOpen } from 'lucide-react'
import { listProjects, createProject, deleteProject } from '../api/projects'
import Modal from '../components/Modal'
import LoadingSpinner from '../components/LoadingSpinner'
import { formatDistanceToNow } from 'date-fns'

export default function Projects() {
  const [projects, setProjects] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [form, setForm] = useState({ name: '', slug: '', description: '' })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    listProjects().then(setProjects).finally(() => setLoading(false))
  }, [])

  function slugify(name) {
    return name.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '')
  }

  async function handleCreate(e) {
    e.preventDefault()
    setSaving(true)
    setError('')
    try {
      const p = await createProject(form)
      setProjects(ps => [...ps, p])
      setShowModal(false)
      setForm({ name: '', slug: '', description: '' })
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create project')
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(id) {
    if (!confirm('Delete this project and all its monitors?')) return
    await deleteProject(id)
    setProjects(ps => ps.filter(p => p.id !== id))
  }

  if (loading) return <LoadingSpinner />

  return (
    <div className="max-w-4xl space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Projects</h1>
          <p className="text-slate-400 text-sm mt-1">Organize your monitors by project</p>
        </div>
        <button onClick={() => setShowModal(true)} className="btn-primary flex items-center gap-2">
          <Plus size={16} /> New project
        </button>
      </div>

      {projects.length === 0 ? (
        <div className="card text-center py-16">
          <FolderOpen size={32} className="text-slate-600 mx-auto mb-3" />
          <p className="text-slate-400 mb-4">No projects yet. Create one to start monitoring.</p>
          <button onClick={() => setShowModal(true)} className="btn-primary inline-flex items-center gap-2">
            <Plus size={16} /> Create first project
          </button>
        </div>
      ) : (
        <div className="grid gap-4">
          {projects.map(p => (
            <div key={p.id} className="card flex items-center justify-between hover:border-slate-700 transition-colors">
              <div className="min-w-0">
                <div className="flex items-center gap-2">
                  <h3 className="font-semibold text-white">{p.name}</h3>
                  <span className="text-xs text-slate-500 font-mono bg-slate-800 px-2 py-0.5 rounded">{p.slug}</span>
                </div>
                {p.description && <p className="text-sm text-slate-400 mt-0.5">{p.description}</p>}
                <p className="text-xs text-slate-500 mt-1">
                  {p.monitorCount} monitor{p.monitorCount !== 1 ? 's' : ''} · Created {formatDistanceToNow(new Date(p.createdAt), { addSuffix: true })}
                </p>
              </div>
              <div className="flex items-center gap-2 ml-4 shrink-0">
                <a
                  href={`/status/${p.slug}`}
                  target="_blank"
                  rel="noreferrer"
                  className="btn-secondary text-xs flex items-center gap-1"
                >
                  Status <ExternalLink size={11} />
                </a>
                <Link to={`/projects/${p.id}/monitors`} className="btn-primary text-xs">
                  Monitors
                </Link>
                <button onClick={() => handleDelete(p.id)} className="p-2 text-slate-500 hover:text-red-400 transition-colors">
                  <Trash2 size={15} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {showModal && (
        <Modal title="Create Project" onClose={() => setShowModal(false)}>
          <form onSubmit={handleCreate} className="space-y-4">
            {error && <p className="text-sm text-red-400 bg-red-900/20 px-3 py-2 rounded-lg border border-red-800">{error}</p>}
            <div>
              <label className="label">Project name</label>
              <input
                className="input"
                placeholder="My SaaS App"
                value={form.name}
                onChange={e => setForm(f => ({ ...f, name: e.target.value, slug: slugify(e.target.value) }))}
                required
              />
            </div>
            <div>
              <label className="label">Slug (URL-safe identifier)</label>
              <input
                className="input font-mono"
                placeholder="my-saas-app"
                value={form.slug}
                onChange={e => setForm(f => ({ ...f, slug: e.target.value }))}
                required
              />
            </div>
            <div>
              <label className="label">Description (optional)</label>
              <input
                className="input"
                placeholder="Production monitoring for…"
                value={form.description}
                onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
              />
            </div>
            <div className="flex gap-3 pt-1">
              <button type="submit" className="btn-primary flex-1" disabled={saving}>
                {saving ? 'Creating...' : 'Create project'}
              </button>
              <button type="button" className="btn-secondary" onClick={() => setShowModal(false)}>
                Cancel
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  )
}
