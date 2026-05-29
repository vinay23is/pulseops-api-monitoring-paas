import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { Zap } from 'lucide-react'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(form.email, form.password)
      navigate('/dashboard')
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid credentials')
    } finally {
      setLoading(false)
    }
  }

  function fillDemo() {
    setForm({ email: 'demo@pulseops.dev', password: 'demo123' })
  }

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center p-4">
      <div className="w-full max-w-sm">
        <div className="flex items-center justify-center gap-2 mb-8">
          <div className="w-9 h-9 rounded-xl bg-brand-600 flex items-center justify-center">
            <Zap size={18} className="text-white" />
          </div>
          <span className="text-2xl font-bold">PulseOps</span>
        </div>

        <div className="card">
          <h1 className="text-xl font-bold text-white mb-6">Sign in</h1>

          {error && (
            <div className="mb-4 px-3 py-2.5 bg-red-900/30 border border-red-800 rounded-lg text-sm text-red-400">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="label">Email</label>
              <input
                type="email"
                className="input"
                placeholder="you@example.com"
                value={form.email}
                onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                required
              />
            </div>
            <div>
              <label className="label">Password</label>
              <input
                type="password"
                className="input"
                placeholder="••••••••"
                value={form.password}
                onChange={e => setForm(f => ({ ...f, password: e.target.value }))}
                required
              />
            </div>
            <button type="submit" className="btn-primary w-full py-2.5" disabled={loading}>
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
          </form>

          <button
            type="button"
            onClick={fillDemo}
            className="w-full mt-3 text-sm text-brand-400 hover:text-brand-300 transition-colors py-1"
          >
            Use demo credentials
          </button>
        </div>

        <p className="text-center text-sm text-slate-400 mt-4">
          No account?{' '}
          <Link to="/register" className="text-brand-400 hover:text-brand-300 font-medium">
            Register free
          </Link>
        </p>
      </div>
    </div>
  )
}
