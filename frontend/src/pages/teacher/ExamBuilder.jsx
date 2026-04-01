import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { examDetail, createExam, updateExam, teacherCourses } from '../../api/teacher';
import Navbar from '../../components/Navbar';

export default function ExamBuilder() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [courses, setCourses] = useState([]);
  const [form, setForm] = useState({ title: '', description: '', courseId: '', durationMinutes: 30, scheduledStart: '', scheduledEnd: '', passPercentage: 40, randomizeQuestions: true });

  useEffect(() => {
    teacherCourses().then((r) => setCourses(r.data));
    if (id) examDetail(id).then((r) => {
      const e = r.data;
      setForm({
        title: e.title,
        description: e.description || '',
        courseId: e.courseId,
        durationMinutes: e.durationMinutes,
        scheduledStart: e.scheduledStart?.slice(0, 16),
        scheduledEnd: e.scheduledEnd?.slice(0, 16),
        passPercentage: e.passPercentage,
        randomizeQuestions: e.randomizeQuestions
      });
    });
  }, [id]);

  const submit = async (ev) => {
    ev.preventDefault();
    const payload = {
      ...form,
      courseId: Number(form.courseId),
      durationMinutes: Number(form.durationMinutes),
      passPercentage: Number(form.passPercentage),
      scheduledStart: new Date(form.scheduledStart).toISOString(),
      scheduledEnd: new Date(form.scheduledEnd).toISOString()
    };
    if (id) await updateExam(id, payload); else await createExam(payload);
    navigate('/teacher/exams');
  };

  return (
    <>
      <Navbar />
      <form className="page-shell space-y-3 rounded-xl bg-white p-5 shadow" onSubmit={submit}>
        <h2 className="text-xl font-bold">Exam Builder</h2>
        <input className="w-full rounded border p-2" placeholder="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
        <textarea className="w-full rounded border p-2" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        <select className="w-full rounded border p-2" value={form.courseId} onChange={(e) => setForm({ ...form, courseId: e.target.value })}>
          <option value="">Select course</option>
          {courses.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        <div className="grid gap-3 sm:grid-cols-2">
          <input className="rounded border p-2" type="number" placeholder="Duration" value={form.durationMinutes} onChange={(e) => setForm({ ...form, durationMinutes: e.target.value })} />
          <input className="rounded border p-2" type="number" placeholder="Pass %" value={form.passPercentage} onChange={(e) => setForm({ ...form, passPercentage: e.target.value })} />
        </div>
        <div className="grid gap-3 sm:grid-cols-2">
          <input className="rounded border p-2" type="datetime-local" value={form.scheduledStart} onChange={(e) => setForm({ ...form, scheduledStart: e.target.value })} />
          <input className="rounded border p-2" type="datetime-local" value={form.scheduledEnd} onChange={(e) => setForm({ ...form, scheduledEnd: e.target.value })} />
        </div>
        <label className="flex items-center gap-2"><input type="checkbox" checked={form.randomizeQuestions} onChange={(e) => setForm({ ...form, randomizeQuestions: e.target.checked })} /> Randomize questions</label>
        <button className="rounded bg-sky-600 px-4 py-2 text-white">Save</button>
      </form>
    </>
  );
}
