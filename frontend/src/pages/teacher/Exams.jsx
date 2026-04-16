import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { teacherExams } from '../../api/teacher';
import Navbar from '../../components/Navbar';

export default function TeacherExams() {
  const [exams, setExams] = useState([]);
  useEffect(() => { teacherExams().then((r) => setExams(r.data)); }, []);

  return (
    <>
      <Navbar />
      <div className="page-shell rounded-xl bg-white p-5 shadow">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-xl font-bold">My Exams</h2>
          <Link to="/teacher/exam-builder" className="rounded bg-sky-600 px-3 py-2 text-white">Create Exam</Link>
        </div>
        <ul className="space-y-2">
          {exams.map((e) => (
            <li key={e.id} className="flex items-center justify-between rounded border p-3">
              <span>{e.title} ({e.published ? 'Published' : 'Draft'})</span>
              <div className="space-x-3">
                {e.published ? (
                  <span className="text-slate-400" title="Published exams are locked">Edit</span>
                ) : (
                  <Link to={`/teacher/exam-builder/${e.id}`} className="text-sky-700">Edit</Link>
                )}
                {e.published ? (
                  <span className="text-slate-400" title="Published exams are locked">Questions</span>
                ) : (
                  <Link to={`/teacher/question-manager/${e.id}`} className="text-lime-700">Questions</Link>
                )}
                {e.published && <Link to={`/teacher/exam-results/${e.id}`} className="text-amber-700">Attempts</Link>}
              </div>
            </li>
          ))}
        </ul>
      </div>
    </>
  );
}
