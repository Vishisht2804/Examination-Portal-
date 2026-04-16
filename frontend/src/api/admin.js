import api from './axios';

export const listUsers = () => api.get('/admin/users');
export const createUser = (payload) => api.post('/admin/users', payload);
export const updateUser = (id, payload) => api.put(`/admin/users/${id}`, payload);
export const deactivateUser = (id) => api.delete(`/admin/users/${id}`);

export const listCourses = () => api.get('/admin/courses');
export const listExams = () => api.get('/admin/exams');
export const createCourse = (payload) => api.post('/admin/courses', payload);
export const updateCourse = (id, payload) => api.put(`/admin/courses/${id}`, payload);
export const deleteCourse = (id) => api.delete(`/admin/courses/${id}`);
export const enrollStudents = (id, payload) => api.put(`/admin/courses/${id}/enroll`, payload);
export const reassignTeacher = (id, payload) => api.put(`/admin/courses/${id}/teacher`, payload);
export const removeTeacherFromCourse = (id) => api.delete(`/admin/courses/${id}/teacher`);
export const addStudentToCourse = (courseId, studentId) => api.post(`/admin/courses/${courseId}/students/${studentId}`);
export const removeStudentFromCourse = (courseId, studentId) => api.delete(`/admin/courses/${courseId}/students/${studentId}`);

export const examResults = (examId) => api.get(`/admin/results/exam/${examId}`);
export const publishResults = (examId) => api.post(`/admin/results/publish/${examId}`);
