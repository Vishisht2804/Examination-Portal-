import api from './axios';

export const studentExams = () => api.get('/student/exams');
export const studentExamInfo = (id) => api.get(`/student/exams/${id}`);
export const startExam = (id) => api.post(`/student/exams/${id}/start`);
export const getAttempt = (id) => api.get(`/student/attempts/${id}`);
export const saveAnswer = (id, payload) => api.put(`/student/attempts/${id}/answer`, payload);
export const submitAttempt = (id) => api.post(`/student/attempts/${id}/submit`);
export const attemptHistory = () => api.get('/student/attempts');
export const attemptResult = (id) => api.get(`/student/attempts/${id}/result`);
