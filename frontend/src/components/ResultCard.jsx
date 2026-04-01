export default function ResultCard({ result }) {
  if (!result) return null;

  return (
    <div className="rounded-xl bg-white p-6 shadow">
      <h2 className="text-2xl font-bold">{result.examTitle}</h2>
      <p className="mt-2 text-slate-600">Score: {result.score}/{result.totalMarks} ({result.percentage.toFixed(2)}%)</p>
      <span className={`mt-3 inline-block rounded px-3 py-1 text-sm font-bold text-white ${result.passed ? 'bg-lime' : 'bg-coral'}`}>
        {result.passed ? 'PASSED' : 'FAILED'}
      </span>
    </div>
  );
}
