export default function QuestionCard({ question, selectedOption, onSelect }) {
  if (!question) return null;

  const options = ['A', 'B', 'C', 'D'];

  return (
    <div className="rounded-xl bg-white p-5 shadow">
      <h3 className="mb-4 text-lg font-semibold">{question.questionText}</h3>
      <div className="space-y-2">
        {options.map((opt) => (
          <label key={opt} className="flex cursor-pointer items-center gap-3 rounded border p-3 hover:bg-slate-50">
            <input
              type="radio"
              name={`q-${question.id}`}
              checked={selectedOption === opt}
              onChange={() => onSelect(opt)}
            />
            <span>{question[`option${opt}`]}</span>
          </label>
        ))}
      </div>
    </div>
  );
}
