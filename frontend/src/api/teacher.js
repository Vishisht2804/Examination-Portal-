import api from './axios';

export const teacherCourses = () => api.get('/teacher/courses');
export const createExam = (payload) => api.post('/teacher/exams', payload);
export const teacherExams = () => api.get('/teacher/exams');
export const examDetail = (id) => api.get(`/teacher/exams/${id}`);
export const updateExam = (id, payload) => api.put(`/teacher/exams/${id}`, payload);
export const publishExam = (id) => api.put(`/teacher/exams/${id}/publish`);

export const addQuestion = (examId, payload) => api.post(`/teacher/exams/${examId}/questions`, payload);
export const updateQuestion = (id, payload) => api.put(`/teacher/questions/${id}`, payload);
export const deleteQuestion = (id) => api.delete(`/teacher/questions/${id}`);
export const listQuestions = (examId) => api.get(`/teacher/exams/${examId}/questions`);

export const teacherExamResults = (id) => api.get(`/teacher/exams/${id}/results`);
