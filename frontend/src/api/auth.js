import api from './axios';

export const loginApi = (payload) => api.post('/auth/login', payload);
export const meApi = () => api.post('/auth/me');
