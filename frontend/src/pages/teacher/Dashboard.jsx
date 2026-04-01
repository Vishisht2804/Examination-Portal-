import { useEffect, useState } from 'react';
import { teacherExams } from '../../api/teacher';
import Navbar from '../../components/Navbar';

export default function TeacherDashboard() {
  const [count, setCount] = useState(0);
  useEffect(() => { teacherExams().then((r) => setCount(r.data.length)); }, []);
  return (
    <>
      <Navbar />
      <div className="page-shell">
        <div className="rounded-xl bg-white p-6 shadow">
          <h2 className="text-xl font-bold">Teacher Dashboard</h2>
          <p className="mt-3">My exams: <span className="font-bold">{count}</span></p>
        </div>
      </div>
    </>
  );
}
