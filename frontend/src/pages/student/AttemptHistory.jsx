import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { attemptHistory } from '../../api/student';
import Navbar from '../../components/Navbar';

export default function AttemptHistory() {
  const [attempts, setAttempts] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    attemptHistory()
      .then(({ data }) => {
        setAttempts(data || []);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  const getStatusColor = (status) => {
    switch (status) {
      case 'SUBMITTED':
        return 'bg-lime/10 text-lime border-lime/30';
      case 'IN_PROGRESS':
        return 'bg-blue-100 text-blue-700 border-blue-200';
      case 'TIMED_OUT':
        return 'bg-coral/10 text-coral border-coral/30';
      default:
        return 'bg-slate-100 text-slate-700 border-slate-200';
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <>
      <Navbar />
      <div className="page-shell">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-slate-900">Attempt History</h1>
          <p className="mt-2 text-slate-600">View all your exam attempts.</p>
        </div>

        {loading && (
          <div className="rounded-lg bg-white p-8 text-center shadow">
            <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-slate-300 border-t-sky-600"></div>
            <p className="mt-3 text-slate-600">Loading attempts...</p>
          </div>
        )}

        {!loading && attempts.length === 0 && (
          <div className="rounded-lg bg-white p-8 text-center shadow">
            <p className="text-lg font-semibold text-slate-900">No attempt history</p>
            <p className="mt-1 text-slate-600">You haven't taken any exams yet.</p>
          </div>
        )}

        {!loading && attempts.length > 0 && (
          <div className="overflow-x-auto rounded-lg border border-slate-200 bg-white shadow">
            <table className="w-full">
              <thead className="border-b border-slate-200 bg-slate-50">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-900">Exam</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-900">Status</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-900">Score</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-900">Result</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-900">Started</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-slate-900">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {attempts.map((attempt) => {
                  const percentage = attempt.totalMarks > 0 ? Math.round((attempt.score / attempt.totalMarks) * 100) : 0;
                  return (
                    <tr key={attempt.attemptId} className="hover:bg-slate-50 transition-colors">
                      <td className="px-6 py-4 text-sm font-medium text-slate-900">{attempt.examTitle}</td>
                      <td className="px-6 py-4">
                        <span className={`inline-block rounded-full px-3 py-1 text-xs font-semibold border ${getStatusColor(attempt.status)}`}>
                          {attempt.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm font-semibold text-slate-900">
                        {attempt.score}/{attempt.totalMarks}
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-2">
                          <span className="text-sm font-semibold text-slate-900">{percentage}%</span>
                          <span className={attempt.passed ? 'text-lime text-xl' : 'text-coral text-xl'}>
                            {attempt.passed ? '✓' : '✗'}
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm text-slate-600">{formatDate(attempt.startedAt)}</td>
                      <td className="px-6 py-4">
                        <button
                          onClick={() => navigate(`/student/results/${attempt.attemptId}`)}
                          className="inline-block rounded-lg bg-sky-100 px-3 py-1 text-sm font-semibold text-sky-700 hover:bg-sky-200 transition-colors"
                        >
                          View
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  );
}
