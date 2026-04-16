import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { startExam, studentExams } from '../../api/student';
import Navbar from '../../components/Navbar';

export default function ExamList() {
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

  const grouped = exams.reduce((acc, exam) => {
    const key = exam.courseId;
    if (!acc[key]) {
      acc[key] = { courseName: exam.courseName, exams: [] };
    }
    acc[key].exams.push(exam);
    return acc;
  }, {});

  const groupedEntries = Object.entries(grouped);

  const start = async (id) => {
    try {
      const { data } = await startExam(id);
      navigate(`/student/exam-room/${data.attemptId}`);
    } catch (err) {
      alert('Error starting exam: ' + (err.response?.data?.message || err.message));
    }
  };

  return (
    <>
      <Navbar />
      <div className="page-shell">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-900">Available Exams</h1>
          <p className="mt-2 text-slate-600">Choose an exam below to start taking it.</p>
        </div>

        {loading && (
          <div className="rounded-lg bg-white p-8 text-center shadow">
            <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-slate-300 border-t-sky-600"></div>
            <p className="mt-3 text-slate-600">Loading exams...</p>
          </div>
        )}

        {!loading && exams.length === 0 && (
          <div className="rounded-lg bg-white p-8 text-center shadow">
            <p className="text-lg font-semibold text-slate-900">No exams available</p>
            <p className="mt-1 text-slate-600">Check back later for new exams.</p>
          </div>
        )}

        {!loading && exams.length > 0 && (
          <div className="space-y-6">
            {groupedEntries.map(([courseId, course]) => (
              <div key={courseId} className="rounded-xl border border-slate-200 bg-white p-5 shadow">
                <h2 className="mb-4 text-lg font-bold text-slate-900">{course.courseName}</h2>
                <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
                  {course.exams.map((exam) => (
                    <div key={exam.id} className="flex flex-col overflow-hidden rounded-lg border border-slate-200 bg-white shadow-md hover:shadow-xl transition-shadow">
                      <div className="flex-1 p-5">
                        <div className="mb-2 inline-block rounded-full bg-sky-100 px-3 py-1 text-xs font-semibold text-sky-700">Available</div>
                        <h3 className="mb-2 text-xl font-bold text-slate-900">{exam.title}</h3>
                        <p className="mb-4 line-clamp-3 text-sm text-slate-600">{exam.description}</p>

                        <div className="space-y-1 text-sm">
                          <div className="flex items-center justify-between text-slate-700">
                            <span>Duration:</span>
                            <span className="font-semibold">{exam.durationMinutes} minutes</span>
                          </div>
                          <div className="flex items-center justify-between text-slate-700">
                            <span>Questions:</span>
                            <span className="font-semibold">{exam.questionCount}</span>
                          </div>
                          <div className="flex items-center justify-between text-slate-700">
                            <span>Total Marks:</span>
                            <span className="font-semibold">{exam.totalMarks}</span>
                          </div>
                          <div className="flex items-center justify-between text-slate-700">
                            <span>Pass Mark:</span>
                            <span className="font-semibold">{exam.passPercentage}%</span>
                          </div>
                        </div>
                      </div>

                      <div className="border-t border-slate-200 bg-slate-50 p-4">
                        <button
                          onClick={() => start(exam.id)}
                          className="w-full rounded-lg bg-sky-600 py-3 font-semibold text-white hover:bg-sky-700 transition-colors duration-200"
                        >
                          Start Exam
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
}
