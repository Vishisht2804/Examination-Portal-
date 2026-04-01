import { useEffect, useState } from 'react';
import { listCourses, listExams, listUsers } from '../../api/admin';
import Navbar from '../../components/Navbar';

export default function AdminDashboard() {
  const [stats, setStats] = useState({ users: 0, courses: 0, exams: 0 });

  useEffect(() => {
    Promise.all([listUsers(), listCourses(), listExams()]).then(([users, courses, exams]) => {
      setStats({ users: users.data.length, courses: courses.data.length, exams: exams.data.length });
    });
  }, []);

  return (
    <>
      <Navbar />
      <div className="page-shell grid gap-4 sm:grid-cols-3">
        <div className="rounded-xl bg-white p-5 shadow"><p>Total Users</p><h3 className="text-3xl font-bold">{stats.users}</h3></div>
        <div className="rounded-xl bg-white p-5 shadow"><p>Total Courses</p><h3 className="text-3xl font-bold">{stats.courses}</h3></div>
        <div className="rounded-xl bg-white p-5 shadow"><p>Total Exams</p><h3 className="text-3xl font-bold">{stats.exams}</h3></div>
      </div>
    </>
  );
}
