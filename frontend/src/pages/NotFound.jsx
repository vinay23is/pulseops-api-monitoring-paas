import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center text-center p-6">
      <div>
        <p className="text-6xl font-black text-brand-600 mb-4">404</p>
        <h1 className="text-2xl font-bold text-white mb-2">Page not found</h1>
        <p className="text-slate-400 mb-6">The page you're looking for doesn't exist.</p>
        <Link to="/" className="btn-primary">Back to home</Link>
      </div>
    </div>
  )
}
