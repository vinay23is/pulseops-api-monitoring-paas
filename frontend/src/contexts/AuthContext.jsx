import { createContext, useContext, useState, useCallback } from 'react'
import { login as apiLogin, register as apiRegister } from '../api/auth'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('user')) } catch { return null }
  })

  const login = useCallback(async (email, password) => {
    const data = await apiLogin(email, password)
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify({ email: data.email, name: data.name, userId: data.userId }))
    setUser({ email: data.email, name: data.name, userId: data.userId })
    return data
  }, [])

  const register = useCallback(async (name, email, password) => {
    const data = await apiRegister(name, email, password)
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify({ email: data.email, name: data.name, userId: data.userId }))
    setUser({ email: data.email, name: data.name, userId: data.userId })
    return data
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
