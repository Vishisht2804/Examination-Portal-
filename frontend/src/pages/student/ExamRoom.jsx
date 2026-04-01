import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getAttempt, saveAnswer, submitAttempt } from '../../api/student';
import Timer from '../../components/Timer';
import QuestionCard from '../../components/QuestionCard';

export default function ExamRoom() {
  const { attemptId } = useParams();
  const navigate = useNavigate();
  const [attempt, setAttempt] = useState(null);
  const [loading, setLoading] = useState(true);
  const [idx, setIdx] = useState(0);
  const [answers, setAnswers] = useState({});

  useEffect(() => {
    getAttempt(attemptId)
      .then(({ data }) => {
        setAttempt(data);
        const map = {};
        data.answers.forEach((a) => { map[a.questionId] = a.selectedOption; });
        setAnswers(map);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [attemptId]);

  const questions = attempt?.questions || [];
  const current = questions[idx];

  const onSelect = (opt) => {
    if (!current) return;
    setAnswers((prev) => ({ ...prev, [current.id]: opt }));
    saveAnswer(attemptId, { questionId: current.id, selectedOption: opt }).catch(() => {});
  };

  const submit = async () => {
    if (window.confirm('Are you sure you want to submit? You cannot change answers after submission.')) {
      try {
        await submitAttempt(attemptId);
        navigate(`/student/results/${attemptId}`);
      } catch (err) {
        alert('Error submitting exam: ' + err.response?.data?.message);
      }
    }
  };

  const answeredCount = useMemo(() => Object.keys(answers).length, [answers]);

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center bg-slate-50">
        <div className="text-center">
          <div className="inline-block h-12 w-12 animate-spin rounded-full border-4 border-slate-300 border-t-sky-600"></div>
          <p className="mt-4 text-lg text-slate-600">Loading exam...</p>
        </div>
      </div>
    );
  }

  if (!attempt) {
    return (
      <div className="flex h-screen items-center justify-center bg-slate-50">
        <div className="text-center">
          <p className="text-lg font-semibold text-slate-900">Exam not found</p>
          <button onClick={() => navigate('/student')} className="mt-4 rounded-lg bg-sky-600 px-4 py-2 text-white">Back to Dashboard</button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Header */}
      <div className="fixed top-0 left-0 right-0 z-40 border-b border-slate-200 bg-white shadow-sm">
        <div className="mx-auto max-w-7xl px-4 py-4 sm:px-6 lg:px-8">
          <div className="flex flex-wrap items-center justify-between gap-4">
            <div>
              <h2 className="text-2xl font-bold text-slate-900">{attempt.examTitle}</h2>
              <p className="text-sm text-slate-600">Question {idx + 1} of {questions.length}</p>
            </div>
            <div className="flex items-center gap-4">
              <div className="text-center">
                <p className="text-xs text-slate-600">Answered</p>
                <p className="text-lg font-bold text-sky-600">{answeredCount}/{questions.length}</p>
              </div>
              <div className="h-16 border-l border-slate-300"></div>
              <Timer deadline={attempt.deadline} onTimeUp={submit} />
              <button
                onClick={submit}
                className="rounded-lg bg-coral px-4 py-2 font-semibold text-white hover:bg-coral/90 transition-colors whitespace-nowrap"
              >
                ✓ Submit
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="mx-auto max-w-7xl px-4 pt-28 pb-8 sm:px-6 lg:px-8">
        <div className="grid gap-6 lg:grid-cols-4">
          {/* Question Section */}
          <div className="lg:col-span-3">
            <div className="rounded-lg bg-white p-6 shadow">
              <QuestionCard question={current} selectedOption={answers[current?.id]} onSelect={onSelect} />
              <div className="mt-6 flex justify-between">
                <button
                  className="rounded-lg border border-slate-300 px-4 py-2 font-semibold text-slate-700 hover:bg-slate-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  disabled={idx === 0}
                  onClick={() => setIdx((v) => v - 1)}
                >
                  ← Previous
                </button>
                <button
                  className="rounded-lg border border-slate-300 px-4 py-2 font-semibold text-slate-700 hover:bg-slate-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  disabled={idx === questions.length - 1}
                  onClick={() => setIdx((v) => v + 1)}
                >
                  Next →
                </button>
              </div>
            </div>
          </div>

          {/* Question Palette Sidebar */}
          <div className="rounded-lg bg-white p-4 shadow">
            <h3 className="mb-4 font-semibold text-slate-900">📋 Question Palette</h3>
            <div className="grid grid-cols-5 gap-2">
              {questions.map((q, i) => (
                <button
                  key={q.id}
                  className={`rounded-lg p-2 text-xs font-semibold transition-colors ${
                    idx === i
                      ? 'bg-sky-600 text-white ring-2 ring-sky-400'
                      : answers[q.id]
                      ? 'bg-lime text-white hover:bg-lime/90'
                      : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                  }`}
                  onClick={() => setIdx(i)}
                  title={`Question ${i + 1}${answers[q.id] ? ' (Answered)' : ''}`}
                >
                  {i + 1}
                </button>
              ))}
            </div>
            <div className="mt-4 space-y-2 text-xs">
              <div className="flex items-center gap-2">
                <div className="h-3 w-3 rounded bg-slate-100"></div>
                <span className="text-slate-600">Not answered</span>
              </div>
              <div className="flex items-center gap-2">
                <div className="h-3 w-3 rounded bg-lime"></div>
                <span className="text-slate-600">Answered</span>
              </div>
              <div className="flex items-center gap-2">
                <div className="h-3 w-3 rounded bg-sky-600"></div>
                <span className="text-slate-600">Current</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
