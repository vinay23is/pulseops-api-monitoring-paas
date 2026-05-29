import client from './client'

export const getDashboard = () => client.get('/dashboard').then(r => r.data)

export const getIncidents = () => client.get('/incidents').then(r => r.data)

export const getAlerts = () => client.get('/alerts').then(r => r.data)
