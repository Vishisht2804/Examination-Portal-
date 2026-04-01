import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { studentExams, startExam } from '../../api/student';
import Navbar from '../../components/Navbar';

export default function StudentDashboard() {
  const [exams, setExams] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    studentExams()
      .then((r) => {
        setExams(r.data || []);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  const handleStartExam = async (id) => {
    try {
      const { data } = await startExam(id);
      navigate(`/student/exam-room/${data.attemptId}`);
    } catch (err) {
      alert('Failed to start exam: ' + err.response?.data?.message);
    }
  };

  return (
    <>
      <Navbar />
      <div className="page-shell">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-900">Student Dashboard</h1>
          <p className="mt-2 text-slate-600">Welcome! Below are the exams you can take.</p>
        </div>

        {loading && <div className="rounded-xl bg-white p-6 shadow">Loading exams...</div>}

        {!loading && exams.length === 0 && (
          <div className="rounded-xl bg-white p-6 shadow">
            <p className="text-center text-slate-600">No exams available at this time.</p>
          </div>
        )}

        {!loading && exams.length > 0 && (
          <div>
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-xl font-bold text-slate-900">Available Exams</h2>
              <span className="inline-block rounded-full bg-sky-100 px-3 py-1 text-sm font-semibold text-sky-700">{exams.length} exams</span>
            </div>
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {exams.map((exam) => (
                <div key={exam.id} className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow hover:shadow-lg transition-shadow">
                  <div className="border-b border-slate-200 bg-gradient-to-r from-sky-500 to-sky-600 p-4">
                    <h3 className="text-lg font-bold text-white">{exam.title}</h3>
                  </div>
                  <div className="p-4">
                    <p className="mb-3 line-clamp-2 text-sm text-slate-600">{exam.description}</p>
                    <div className="mb-4 space-y-2 text-sm">
                      <div className="flex items-center justify-between">
                        <span className="text-slate-600">Duration:</span>
                        <span className="font-semibold text-slate-900">{exam.durationMinutes} min</span>
                      </div>
                      <div className="flex items-center justify-between">
                        <span className="text-slate-600">Questions:</span>
                        <span className="font-semibold text-slate-900">{exam.questionCount}</span>
                      </div>
                      <div className="flex items-center justify-between">
                        <span className="text-slate-600">Total Marks:</span>
                        <span className="font-semibold text-slate-900">{exam.totalMarks}</span>
                      </div>
                      <div className="flex items-center justify-between">
                        <span className="text-slate-600">Pass %:</span>
                        <span className="font-semibold text-slate-900">{exam.passPercentage}%</span>
                      </div>
                    </div>
                    <button
                      onClick={() => handleStartExam(exam.id)}
                      className="w-full rounded-lg bg-sky-600 py-2 text-white font-semibold hover:bg-sky-700 transition-colors"
                    >
                      → Start Exam
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </>
  );
}
