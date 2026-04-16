import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { studentCourses, studentExams, startExam } from '../../api/student';
import Navbar from '../../components/Navbar';

export default function StudentDashboard() {
  const [courses, setCourses] = useState([]);
  const [exams, setExams] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([studentCourses(), studentExams()])
      .then(([coursesResp, examsResp]) => {
        setCourses(coursesResp.data || []);
        setExams(examsResp.data || []);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  const examsByCourse = exams.reduce((acc, exam) => {
    const key = exam.courseId;
    if (!acc[key]) {
      acc[key] = [];
    }
    acc[key].push(exam);
    return acc;
  }, {});

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
          <p className="mt-2 text-slate-600">View your registered subjects and currently available exams in each subject.</p>
        </div>

        {loading && <div className="rounded-xl bg-white p-6 shadow">Loading your courses and exams...</div>}

        {!loading && courses.length === 0 && (
          <div className="rounded-xl bg-white p-6 shadow">
            <p className="text-center text-slate-600">You are not registered in any courses yet.</p>
          </div>
        )}

        {!loading && courses.length > 0 && (
          <div className="space-y-6">
            {courses.map((course) => {
              const courseExams = examsByCourse[course.id] || [];
              return (
                <div key={course.id} className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
                  <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
                    <div>
                      <h2 className="text-xl font-bold text-slate-900">{course.name}</h2>
                      <p className="text-sm text-slate-600">{course.description || 'No course description available.'}</p>
                    </div>
                    <span className="inline-block rounded-full bg-sky-100 px-3 py-1 text-xs font-semibold text-sky-700">
                      {courseExams.length} current exams
                    </span>
                  </div>

                  {courseExams.length === 0 && (
                    <p className="rounded-lg bg-slate-50 p-4 text-sm text-slate-600">No current exams in this subject.</p>
                  )}

                  {courseExams.length > 0 && (
                    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                      {courseExams.map((exam) => (
                        <div key={exam.id} className="rounded-lg border border-slate-200 p-4">
                          <h3 className="font-semibold text-slate-900">{exam.title}</h3>
                          <p className="mt-1 line-clamp-2 text-sm text-slate-600">{exam.description}</p>
                          <div className="mt-3 space-y-1 text-sm text-slate-700">
                            <div>Duration: {exam.durationMinutes} min</div>
                            <div>Questions: {exam.questionCount}</div>
                            <div>Pass mark: {exam.passPercentage}%</div>
                          </div>
                          <button
                            onClick={() => handleStartExam(exam.id)}
                            className="mt-4 w-full rounded-lg bg-sky-600 py-2 font-semibold text-white hover:bg-sky-700"
                          >
                            Start Exam
                          </button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>
    </>
  );
}
