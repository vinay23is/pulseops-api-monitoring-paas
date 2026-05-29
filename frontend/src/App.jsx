import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './contexts/AuthContext'
import Landing from './pages/Landing'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import Projects from './pages/Projects'
import Monitors from './pages/Monitors'
import Incidents from './pages/Incidents'
import Alerts from './pages/Alerts'
import StatusPage from './pages/StatusPage'
import NotFound from './pages/NotFound'
import Layout from './components/Layout'

function ProtectedRoute({ children }) {
  const { user } = useAuth()
  return user ? children : <Navigate to="/login" replace />
}

function PublicOnlyRoute({ children }) {
  const { user } = useAuth()
  return user ? <Navigate to="/dashboard" replace /> : children
}

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<PublicOnlyRoute><Login /></PublicOnlyRoute>} />
        <Route path="/register" element={<PublicOnlyRoute><Register /></PublicOnlyRoute>} />
        <Route path="/status/:slug" element={<StatusPage />} />

        <Route path="/" element={<ProtectedRoute><Layout /></ProtectedRoute>}>
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="projects" element={<Projects />} />
          <Route path="projects/:projectId/monitors" element={<Monitors />} />
          <Route path="incidents" element={<Incidents />} />
          <Route path="alerts" element={<Alerts />} />
        </Route>

        <Route path="*" element={<NotFound />} />
      </Routes>
    </AuthProvider>
  )
}
