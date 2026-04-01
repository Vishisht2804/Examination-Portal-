import { useEffect, useState } from 'react';
import { createCourse, listCourses, listUsers } from '../../api/admin';
import Navbar from '../../components/Navbar';

export default function CoursesPage() {
  const [courses, setCourses] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [form, setForm] = useState({ name: '', description: '', teacherId: '' });

  const load = () => listCourses().then((r) => setCourses(r.data));
  useEffect(() => {
    load();
    listUsers().then((r) => setTeachers(r.data.filter((u) => u.role === 'TEACHER')));
  }, []);

  const submit = async (e) => {
    e.preventDefault();
    await createCourse({ ...form, teacherId: Number(form.teacherId) });
    setForm({ name: '', description: '', teacherId: '' });
    load();
  };

  return (
    <>
      <Navbar />
      <div className="page-shell grid gap-5 lg:grid-cols-3">
        <form className="rounded-xl bg-white p-4 shadow" onSubmit={submit}>
          <h2 className="mb-3 text-xl font-bold">Create Course</h2>
          <input className="mb-2 w-full rounded border p-2" placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          <textarea className="mb-2 w-full rounded border p-2" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <select className="mb-3 w-full rounded border p-2" value={form.teacherId} onChange={(e) => setForm({ ...form, teacherId: e.target.value })}>
            <option value="">Select teacher</option>
            {teachers.map((t) => <option key={t.id} value={t.id}>{t.fullName}</option>)}
          </select>
          <button className="w-full rounded bg-sky-600 py-2 text-white">Create</button>
        </form>
        <div className="rounded-xl bg-white p-4 shadow lg:col-span-2">
          <h2 className="mb-3 text-xl font-bold">Courses</h2>
          <ul className="space-y-2">{courses.map((c) => <li key={c.id} className="rounded border p-3">{c.name} - {c.teacherName}</li>)}</ul>
        </div>
      </div>
    </>
  );
}
