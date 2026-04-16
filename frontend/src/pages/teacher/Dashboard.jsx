import { useEffect, useState } from 'react';
import { teacherCourseStudents, teacherCourses, teacherStudentCourseResults } from '../../api/teacher';
import Navbar from '../../components/Navbar';

export default function TeacherDashboard() {
  const [courses, setCourses] = useState([]);
  const [studentsByCourse, setStudentsByCourse] = useState({});
  const [selected, setSelected] = useState(null);
  const [selectedResults, setSelectedResults] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    teacherCourses()
      .then(async ({ data }) => {
        const list = data || [];
        setCourses(list);
        const rosterResponses = await Promise.all(
          list.map(async (course) => {
            const studentsResp = await teacherCourseStudents(course.id);
            return [course.id, studentsResp.data || []];
          })
        );
        setStudentsByCourse(Object.fromEntries(rosterResponses));
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  const openStudentResults = async (course, student) => {
    try {
      const { data } = await teacherStudentCourseResults(course.id, student.id);
      setSelected({
        courseName: course.name,
        studentName: student.fullName,
      });
      setSelectedResults(data || []);
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to load student results');
    }
  };

  const formatScore = (row) => {
    const score = row.score ?? 0;
    const total = row.totalMarks ?? 0;
    return `${score}/${total}`;
  };

  return (
    <>
      <Navbar />
      <div className="page-shell">
        <h1 className="mb-6 text-3xl font-bold text-slate-900">Teacher Dashboard</h1>

        {loading && <div className="rounded-xl bg-white p-6 shadow">Loading course data...</div>}

        {!loading && courses.length === 0 && (
          <div className="rounded-xl bg-white p-6 shadow">No courses are currently assigned to you.</div>
        )}

        {!loading && courses.length > 0 && (
          <div className="space-y-6">
            {courses.map((course) => {
              const students = studentsByCourse[course.id] || [];
              return (
                <div key={course.id} className="rounded-xl border border-slate-200 bg-white p-5 shadow">
                  <div className="mb-4 flex items-center justify-between">
                    <div>
                      <h2 className="text-xl font-bold text-slate-900">{course.name}</h2>
                      <p className="text-sm text-slate-600">{course.description || 'No description'}</p>
                    </div>
                    <span className="rounded-full bg-sky-100 px-3 py-1 text-xs font-semibold text-sky-700">
                      {students.length} students
                    </span>
                  </div>

                  {students.length === 0 && (
                    <p className="rounded-lg bg-slate-50 p-4 text-sm text-slate-600">No students enrolled in this course.</p>
                  )}

                  {students.length > 0 && (
                    <div className="overflow-x-auto">
                      <table className="w-full text-sm">
                        <thead>
                          <tr className="border-b text-left text-slate-600">
                            <th className="pb-2">Student</th>
                            <th className="pb-2">Email</th>
                            <th className="pb-2">Action</th>
                          </tr>
                        </thead>
                        <tbody>
                          {students.map((student) => (
                            <tr key={student.id} className="border-b last:border-0">
                              <td className="py-2 font-medium text-slate-900">{student.fullName}</td>
                              <td className="py-2 text-slate-600">{student.email}</td>
                              <td className="py-2">
                                <button
                                  onClick={() => openStudentResults(course, student)}
                                  className="rounded-lg bg-sky-100 px-3 py-1 text-sky-700 hover:bg-sky-200"
                                >
                                  View Results
                                </button>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}

        {selected && (
          <div className="mt-8 rounded-xl border border-slate-200 bg-white p-5 shadow">
            <h3 className="text-lg font-bold text-slate-900">{selected.studentName} - {selected.courseName}</h3>
            {selectedResults.length === 0 ? (
              <p className="mt-2 text-sm text-slate-600">No attempts found for this student in this course.</p>
            ) : (
              <div className="mt-3 overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b text-left text-slate-600">
                      <th className="pb-2">Exam</th>
                      <th className="pb-2">Score</th>
                      <th className="pb-2">Status</th>
                      <th className="pb-2">Result</th>
                    </tr>
                  </thead>
                  <tbody>
                    {selectedResults.map((row) => (
                      <tr key={row.attemptId} className="border-b last:border-0">
                        <td className="py-2 text-slate-900">{row.examTitle}</td>
                        <td className="py-2">{formatScore(row)}</td>
                        <td className="py-2">{row.status}</td>
                        <td className="py-2">{row.passed ? 'Passed' : 'Failed'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </div>
    </>
  );
}
