import { useEffect, useState } from 'react';
import { listCourses, listExams, listUsers } from '../../api/admin';
import Navbar from '../../components/Navbar';

export default function AdminDashboard() {
  const [stats, setStats] = useState({ users: 0, courses: 0, exams: 0 });
  const [users, setUsers] = useState([]);
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    Promise.all([listUsers(), listCourses(), listExams()]).then(([users, courses, exams]) => {
      setUsers(users.data || []);
      setCourses(courses.data || []);
      setStats({ users: users.data.length, courses: courses.data.length, exams: exams.data.length });
    });
  }, []);

  const students = users.filter((u) => u.role === 'STUDENT');
  const teachers = users.filter((u) => u.role === 'TEACHER');

  return (
    <>
      <Navbar />
      <div className="page-shell space-y-6">
        <div className="grid gap-4 sm:grid-cols-3">
          <div className="rounded-xl bg-white p-5 shadow"><p>Total Users</p><h3 className="text-3xl font-bold">{stats.users}</h3></div>
          <div className="rounded-xl bg-white p-5 shadow"><p>Total Courses</p><h3 className="text-3xl font-bold">{stats.courses}</h3></div>
          <div className="rounded-xl bg-white p-5 shadow"><p>Total Exams</p><h3 className="text-3xl font-bold">{stats.exams}</h3></div>
        </div>

        <div className="grid gap-4 lg:grid-cols-2">
          <div className="rounded-xl bg-white p-5 shadow">
            <h2 className="mb-3 text-lg font-bold">Teachers</h2>
            {teachers.length === 0 && <p className="text-sm text-slate-600">No teachers found.</p>}
            {teachers.length > 0 && (
              <ul className="space-y-2 text-sm">
                {teachers.map((teacher) => (
                  <li key={teacher.id} className="rounded border p-2">
                    <div className="font-semibold text-slate-900">{teacher.fullName}</div>
                    <div className="text-slate-600">{teacher.email}</div>
                  </li>
                ))}
              </ul>
            )}
          </div>

          <div className="rounded-xl bg-white p-5 shadow">
            <h2 className="mb-3 text-lg font-bold">Students</h2>
            {students.length === 0 && <p className="text-sm text-slate-600">No students found.</p>}
            {students.length > 0 && (
              <ul className="space-y-2 text-sm">
                {students.slice(0, 10).map((student) => (
                  <li key={student.id} className="rounded border p-2">
                    <div className="font-semibold text-slate-900">{student.fullName}</div>
                    <div className="text-slate-600">{student.email}</div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>

        <div className="rounded-xl bg-white p-5 shadow">
          <h2 className="mb-3 text-lg font-bold">Courses Overview</h2>
          {courses.length === 0 ? (
            <p className="text-sm text-slate-600">No courses available.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b text-left text-slate-600">
                    <th className="pb-2">Course</th>
                    <th className="pb-2">Teacher</th>
                    <th className="pb-2">Students</th>
                  </tr>
                </thead>
                <tbody>
                  {courses.map((course) => (
                    <tr key={course.id} className="border-b last:border-0">
                      <td className="py-2 font-medium text-slate-900">{course.name}</td>
                      <td className="py-2">{course.teacherName || 'Unassigned'}</td>
                      <td className="py-2">{(course.studentIds || []).length}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
