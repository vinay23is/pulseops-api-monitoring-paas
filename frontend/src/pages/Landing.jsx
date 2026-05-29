import { Link } from 'react-router-dom'
import { Zap, Activity, Bell, Shield, Clock, BarChart2, ArrowRight, CheckCircle } from 'lucide-react'

const features = [
  { icon: Activity, title: 'Real-Time Monitoring', desc: 'HTTP health checks every 30s with configurable intervals, timeouts, and expected status codes.' },
  { icon: Bell, title: 'Smart Alerting', desc: 'In-app, mock email, and webhook alerts fire automatically when a monitor goes down or recovers.' },
  { icon: BarChart2, title: 'Uptime Analytics', desc: 'Track 24-hour uptime %, average latency, check result history, and incident timelines.' },
  { icon: Shield, title: 'Incident Management', desc: 'Incidents open automatically on failure, track failure count, and resolve when health returns.' },
  { icon: Clock, title: 'Public Status Pages', desc: 'Share a public /status/{slug} page — no login required — with real-time project health.' },
  { icon: Zap, title: 'API Key Events', desc: 'Ingest custom events from your app using project API keys with Redis-backed rate limiting.' },
]

const benefits = [
  '100% free, runs locally via Docker Compose',
  'JWT-secured multi-tenant architecture',
  'Redis Streams queue for async health checks',
  'Spring Boot 3 + Java 21 Virtual Threads',
  'Full Swagger / OpenAPI docs at /swagger-ui.html',
]

export default function Landing() {
  return (
    <div className="min-h-screen bg-slate-950 text-white">
      {/* Nav */}
      <nav className="border-b border-slate-800 px-6 py-4 flex items-center justify-between max-w-7xl mx-auto">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-brand-600 flex items-center justify-center">
            <Zap size={16} className="text-white" />
          </div>
          <span className="font-bold text-lg tracking-tight">PulseOps</span>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/login" className="text-slate-300 hover:text-white text-sm font-medium transition-colors">Sign in</Link>
          <Link to="/register" className="btn-primary text-sm">Get started</Link>
        </div>
      </nav>

      {/* Hero */}
      <section className="max-w-5xl mx-auto px-6 pt-24 pb-20 text-center">
        <div className="inline-flex items-center gap-2 bg-brand-900/40 border border-brand-800 text-brand-300 text-xs font-medium px-3 py-1.5 rounded-full mb-6">
          <Zap size={12} />
          Open-source · Free · Docker-ready
        </div>
        <h1 className="text-5xl sm:text-6xl font-extrabold tracking-tight mb-6 bg-gradient-to-br from-white via-slate-200 to-slate-400 bg-clip-text text-transparent leading-tight">
          API Monitoring &<br />Incident Alerting
        </h1>
        <p className="text-xl text-slate-400 max-w-2xl mx-auto mb-10 leading-relaxed">
          Monitor your APIs, track uptime, manage incidents, and get alerts — all running
          locally for free. A production-grade PaaS stack in one Docker Compose command.
        </p>
        <div className="flex items-center justify-center gap-4 flex-wrap">
          <Link to="/register" className="btn-primary flex items-center gap-2 text-base px-6 py-3">
            Start monitoring free <ArrowRight size={16} />
          </Link>
          <Link to="/login" className="btn-secondary flex items-center gap-2 text-base px-6 py-3">
            View demo dashboard
          </Link>
        </div>
        <p className="mt-4 text-sm text-slate-500">Demo: demo@pulseops.dev / demo123</p>
      </section>

      {/* Terminal snippet */}
      <section className="max-w-3xl mx-auto px-6 mb-20">
        <div className="bg-slate-900 border border-slate-700 rounded-2xl overflow-hidden">
          <div className="flex items-center gap-1.5 px-4 py-3 bg-slate-800 border-b border-slate-700">
            <div className="w-3 h-3 rounded-full bg-red-500" />
            <div className="w-3 h-3 rounded-full bg-yellow-500" />
            <div className="w-3 h-3 rounded-full bg-green-500" />
            <span className="ml-3 text-xs text-slate-400 font-mono">terminal</span>
          </div>
          <pre className="text-sm font-mono text-slate-300 px-6 py-5 leading-relaxed">
{`$ git clone https://github.com/you/pulseops-api-monitoring-paas
$ cd pulseops-api-monitoring-paas
$ docker compose up --build

✓ PostgreSQL ready on :5432
✓ Redis ready on :6379
✓ Backend ready on :8080
✓ Frontend ready on :5173
✓ Swagger UI at localhost:8080/swagger-ui.html`}
          </pre>
        </div>
      </section>

      {/* Features */}
      <section className="max-w-6xl mx-auto px-6 py-16">
        <h2 className="text-3xl font-bold text-center mb-3">Everything you need</h2>
        <p className="text-slate-400 text-center mb-12">Production-grade features, zero cost.</p>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map(({ icon: Icon, title, desc }) => (
            <div key={title} className="card hover:border-slate-700 transition-colors">
              <div className="w-10 h-10 rounded-xl bg-brand-900/40 flex items-center justify-center mb-4">
                <Icon size={18} className="text-brand-400" />
              </div>
              <h3 className="font-semibold text-white mb-2">{title}</h3>
              <p className="text-slate-400 text-sm leading-relaxed">{desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Benefits */}
      <section className="max-w-3xl mx-auto px-6 py-16 text-center">
        <h2 className="text-2xl font-bold mb-8">Built for engineers who care about uptime</h2>
        <ul className="space-y-3 text-left max-w-md mx-auto">
          {benefits.map(b => (
            <li key={b} className="flex items-start gap-3">
              <CheckCircle size={16} className="text-emerald-400 mt-0.5 shrink-0" />
              <span className="text-slate-300 text-sm">{b}</span>
            </li>
          ))}
        </ul>
      </section>

      {/* CTA */}
      <section className="max-w-2xl mx-auto px-6 py-16 text-center">
        <div className="card border-brand-800 bg-brand-900/20">
          <h2 className="text-2xl font-bold mb-3">Ready to monitor?</h2>
          <p className="text-slate-400 mb-6">Set up in 60 seconds. No credit card, no cloud account.</p>
          <Link to="/register" className="btn-primary inline-flex items-center gap-2 px-6 py-3">
            Create free account <ArrowRight size={16} />
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-slate-800 py-8 text-center text-slate-500 text-sm">
        <p>PulseOps — Open source API monitoring PaaS · Built with Spring Boot 3 + React + Redis + PostgreSQL</p>
      </footer>
    </div>
  )
}
