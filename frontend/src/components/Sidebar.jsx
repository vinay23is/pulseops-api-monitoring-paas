import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import {
  LayoutDashboard, FolderOpen, Activity, AlertTriangle,
  Bell, LogOut, Zap
} from 'lucide-react'

const navItems = [
  { to: '/dashboard',  icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/projects',   icon: FolderOpen,      label: 'Projects'  },
  { to: '/incidents',  icon: AlertTriangle,   label: 'Incidents' },
  { to: '/alerts',     icon: Bell,            label: 'Alerts'    },
]

export default function Sidebar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/')
  }

  return (
    <aside className="w-60 bg-slate-900 border-r border-slate-800 flex flex-col h-full shrink-0">
      <div className="px-6 py-5 border-b border-slate-800">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-brand-600 flex items-center justify-center">
            <Zap size={16} className="text-white" />
          </div>
          <span className="font-bold text-lg tracking-tight text-white">PulseOps</span>
        </div>
      </div>

      <nav className="flex-1 px-3 py-4 space-y-0.5">
        {navItems.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors duration-100 ${
                isActive
                  ? 'bg-brand-600/20 text-brand-400'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
              }`
            }
          >
            <Icon size={16} />
            {label}
          </NavLink>
        ))}
      </nav>

      <div className="px-4 py-4 border-t border-slate-800">
        <div className="mb-3 px-2">
          <p className="text-xs text-slate-500 truncate">{user?.email}</p>
          <p className="text-sm font-medium text-slate-300 truncate">{user?.name}</p>
        </div>
        <button
          onClick={handleLogout}
          className="w-full flex items-center gap-2 px-3 py-2 text-sm text-slate-400 hover:text-red-400 hover:bg-red-900/20 rounded-lg transition-colors"
        >
          <LogOut size={14} />
          Sign out
        </button>
      </div>
    </aside>
  )
}
