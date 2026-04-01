import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { attemptResult } from '../../api/student';
import Navbar from '../../components/Navbar';

export default function StudentResults() {
  const { attemptId } = useParams();
  const navigate = useNavigate();
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    attemptResult(attemptId)
      .then(({ data }) => {
        setResult(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [attemptId]);

  if (loading) {
    return (
      <>
        <Navbar />
        <div className="page-shell text-center">
          <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-slate-300 border-t-sky-600"></div>
          <p className="mt-3 text-slate-600">Loading results...</p>
        </div>
      </>
    );
  }

  if (!result) {
    return (
      <>
        <Navbar />
        <div className="page-shell text-center">
          <p className="text-lg font-semibold text-slate-900">Results not found</p>
        </div>
      </>
    );
  }

  const percentage = Math.round((result.score / result.totalMarks) * 100);
  const isPass = result.passed;

  return (
    <>
      <Navbar />
      <div className="page-shell">
        <div className="mx-auto max-w-2xl">
          {/* Result Summary Card */}
          <div className={`rounded-lg p-8 shadow-lg ${
            isPass ? 'bg-gradient-to-br from-lime/20 to-lime/10' : 'bg-gradient-to-br from-coral/20 to-coral/10'
          }`}>
            <div className="text-center">
              <div className="mb-4 text-6xl">{isPass ? '✅' : '❌'}</div>
              <h1 className="text-3xl font-bold text-slate-900">{isPass ? 'Passed!' : 'Not Passed'}</h1>
              <p className="mt-2 text-lg text-slate-700">{result.examTitle}</p>
            </div>

            <div className="mt-8 grid grid-cols-2 gap-4 sm:grid-cols-3">
              <div className="rounded-lg bg-white p-4 text-center">
                <p className="text-sm font-semibold text-slate-600">Score</p>
                <p className="mt-2 text-3xl font-bold text-sky-600">{result.score}/{result.totalMarks}</p>
              </div>
              <div className="rounded-lg bg-white p-4 text-center">
                <p className="text-sm font-semibold text-slate-600">Percentage</p>
                <p className={`mt-2 text-3xl font-bold ${percentage >= result.passPercentage ? 'text-lime' : 'text-coral'}`}>
                  {percentage}%
                </p>
              </div>
              <div className="rounded-lg bg-white p-4 text-center sm:col-span-1 col-span-2">
                <p className="text-sm font-semibold text-slate-600">Pass Percentage</p>
                <p className="mt-2 text-3xl font-bold text-slate-900">{result.passPercentage}%</p>
              </div>
            </div>
          </div>

          {/* Question Results */}
          {result.questions && result.questions.length > 0 && (
            <div className="mt-8">
              <h2 className="mb-4 text-2xl font-bold text-slate-900">Your Answers</h2>
              <div className="space-y-4">
                {result.questions.map((q, idx) => {
                  const isCorrect = q.selectedOption === q.correctOption;
                  return (
                    <div
                      key={q.questionId}
                      className={`rounded-lg border-l-4 p-4 ${
                        isCorrect
                          ? 'border-l-lime bg-lime/5'
                          : 'border-l-coral bg-coral/5'
                      }`}
                    >
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <p className="font-semibold text-slate-900">{idx + 1}. {q.questionText}</p>
                          <div className="mt-3 space-y-2 text-sm">
                            <p className="text-slate-700">
                              <span className="font-semibold">Your answer:</span> {q.selectedOption || '(Not answered)'}
                            </p>
                            {q.selectedOption !== q.correctOption && (
                              <p className="text-lime font-semibold">
                                Correct answer: {q.correctOption}
                              </p>
                            )}
                          </div>
                        </div>
                        <div className="ml-4 text-2xl">{isCorrect ? '✓' : '✗'}</div>
                      </div>
                      <div className="mt-2 text-right text-sm font-semibold text-slate-700">
                        {q.marksAwarded}/{q.maxMarks} marks
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Action Buttons */}
          <div className="mt-8 flex gap-4">
            <button
              onClick={() => navigate('/student')}
              className="flex-1 rounded-lg border border-slate-300 py-3 font-semibold text-slate-700 hover:bg-slate-50 transition-colors"
            >
              Back to Dashboard
            </button>
            <button
              onClick={() => navigate('/student/attempt-history')}
              className="flex-1 rounded-lg bg-sky-600 py-3 font-semibold text-white hover:bg-sky-700 transition-colors"
            >
              View All Attempts
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
