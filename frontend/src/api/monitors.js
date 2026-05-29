import client from './client'

export const listMonitors = (projectId) =>
  client.get(`/projects/${projectId}/monitors`).then(r => r.data)

export const createMonitor = (projectId, data) =>
  client.post(`/projects/${projectId}/monitors`, data).then(r => r.data)

export const updateMonitor = (projectId, monitorId, data) =>
  client.put(`/projects/${projectId}/monitors/${monitorId}`, data).then(r => r.data)

export const deleteMonitor = (projectId, monitorId) =>
  client.delete(`/projects/${projectId}/monitors/${monitorId}`)

export const toggleMonitor = (projectId, monitorId) =>
  client.patch(`/projects/${projectId}/monitors/${monitorId}/toggle`).then(r => r.data)
