import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { addQuestion, deleteQuestion, listQuestions, publishExam } from '../../api/teacher';
import Navbar from '../../components/Navbar';

export default function QuestionManager() {
  const { examId } = useParams();
  const [questions, setQuestions] = useState([]);
  const [form, setForm] = useState({ questionText: '', optionA: '', optionB: '', optionC: '', optionD: '', correctOption: 'A', marks: 1 });

  const load = () => listQuestions(examId).then((r) => setQuestions(r.data));
  useEffect(() => { load(); }, [examId]);

  const submit = async (e) => {
    e.preventDefault();
    await addQuestion(examId, { ...form, marks: Number(form.marks) });
    setForm({ questionText: '', optionA: '', optionB: '', optionC: '', optionD: '', correctOption: 'A', marks: 1 });
    load();
  };

  return (
    <>
      <Navbar />
      <div className="page-shell grid gap-5 lg:grid-cols-2">
        <form className="space-y-2 rounded-xl bg-white p-4 shadow" onSubmit={submit}>
          <h2 className="text-xl font-bold">Add Question</h2>
          {['questionText', 'optionA', 'optionB', 'optionC', 'optionD'].map((k) => (
            <input key={k} className="w-full rounded border p-2" placeholder={k} value={form[k]} onChange={(e) => setForm({ ...form, [k]: e.target.value })} />
          ))}
          <div className="grid grid-cols-2 gap-2">
            <select className="rounded border p-2" value={form.correctOption} onChange={(e) => setForm({ ...form, correctOption: e.target.value })}>
              <option>A</option><option>B</option><option>C</option><option>D</option>
            </select>
            <input className="rounded border p-2" type="number" value={form.marks} onChange={(e) => setForm({ ...form, marks: e.target.value })} />
          </div>
          <button className="rounded bg-sky-600 px-4 py-2 text-white">Add</button>
        </form>
        <div className="rounded-xl bg-white p-4 shadow">
          <div className="mb-3 flex items-center justify-between">
            <h2 className="text-xl font-bold">Questions</h2>
            <button disabled={questions.length < 1} className="rounded bg-lime px-3 py-2 text-white disabled:bg-slate-300" onClick={() => publishExam(examId)}>
              Publish Exam
            </button>
          </div>
          <ul className="space-y-2">{questions.map((q) => <li key={q.id} className="flex justify-between rounded border p-2"><span>{q.questionText}</span><button className="text-red-600" onClick={() => deleteQuestion(q.id).then(load)}>Delete</button></li>)}</ul>
        </div>
      </div>
    </>
  );
}
