import client from './client'

export const listProjects = () => client.get('/projects').then(r => r.data)
export const createProject = (data) => client.post('/projects', data).then(r => r.data)
export const deleteProject = (id) => client.delete(`/projects/${id}`)
