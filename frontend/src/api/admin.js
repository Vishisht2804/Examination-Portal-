import api from './axios';

export const listUsers = () => api.get('/admin/users');
export const createUser = (payload) => api.post('/admin/users', payload);
export const updateUser = (id, payload) => api.put(`/admin/users/${id}`, payload);
export const deactivateUser = (id) => api.delete(`/admin/users/${id}`);

export const listCourses = () => api.get('/admin/courses');
export const listExams = () => api.get('/admin/exams');
export const createCourse = (payload) => api.post('/admin/courses', payload);
export const enrollStudents = (id, payload) => api.put(`/admin/courses/${id}/enroll`, payload);
export const reassignTeacher = (id, payload) => api.put(`/admin/courses/${id}/teacher`, payload);

export const examResults = (examId) => api.get(`/admin/results/exam/${examId}`);
export const publishResults = (examId) => api.post(`/admin/results/publish/${examId}`);
