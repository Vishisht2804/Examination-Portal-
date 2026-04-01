import { Navigate, Route, Routes } from 'react-router-dom';
import ProtectedRoute from '../components/ProtectedRoute';
import Login from '../pages/Login';

import AdminDashboard from '../pages/admin/Dashboard';
import AdminUsers from '../pages/admin/Users';
import AdminCourses from '../pages/admin/Courses';
import AdminResults from '../pages/admin/Results';

import TeacherDashboard from '../pages/teacher/Dashboard';
import TeacherExams from '../pages/teacher/Exams';
import ExamBuilder from '../pages/teacher/ExamBuilder';
import QuestionManager from '../pages/teacher/QuestionManager';
import ExamResults from '../pages/teacher/ExamResults';

import StudentDashboard from '../pages/student/Dashboard';
import ExamList from '../pages/student/ExamList';
import ExamRoom from '../pages/student/ExamRoom';
import StudentResults from '../pages/student/Results';
import AttemptHistory from '../pages/student/AttemptHistory';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route path="/admin" element={<ProtectedRoute roles={['ADMIN']}><AdminDashboard /></ProtectedRoute>} />
      <Route path="/admin/users" element={<ProtectedRoute roles={['ADMIN']}><AdminUsers /></ProtectedRoute>} />
      <Route path="/admin/courses" element={<ProtectedRoute roles={['ADMIN']}><AdminCourses /></ProtectedRoute>} />
      <Route path="/admin/results" element={<ProtectedRoute roles={['ADMIN']}><AdminResults /></ProtectedRoute>} />

      <Route path="/teacher" element={<ProtectedRoute roles={['TEACHER']}><TeacherDashboard /></ProtectedRoute>} />
      <Route path="/teacher/exams" element={<ProtectedRoute roles={['TEACHER']}><TeacherExams /></ProtectedRoute>} />
      <Route path="/teacher/exam-builder" element={<ProtectedRoute roles={['TEACHER']}><ExamBuilder /></ProtectedRoute>} />
      <Route path="/teacher/exam-builder/:id" element={<ProtectedRoute roles={['TEACHER']}><ExamBuilder /></ProtectedRoute>} />
      <Route path="/teacher/question-manager/:examId" element={<ProtectedRoute roles={['TEACHER']}><QuestionManager /></ProtectedRoute>} />
      <Route path="/teacher/exam-results/:examId" element={<ProtectedRoute roles={['TEACHER']}><ExamResults /></ProtectedRoute>} />

      <Route path="/student" element={<ProtectedRoute roles={['STUDENT']}><StudentDashboard /></ProtectedRoute>} />
      <Route path="/student/exams" element={<ProtectedRoute roles={['STUDENT']}><ExamList /></ProtectedRoute>} />
      <Route path="/student/exam-room/:attemptId" element={<ProtectedRoute roles={['STUDENT']}><ExamRoom /></ProtectedRoute>} />
      <Route path="/student/results/:attemptId" element={<ProtectedRoute roles={['STUDENT']}><StudentResults /></ProtectedRoute>} />
      <Route path="/student/attempt-history" element={<ProtectedRoute roles={['STUDENT']}><AttemptHistory /></ProtectedRoute>} />

      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
