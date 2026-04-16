import { useEffect, useState } from 'react';
import {
  addStudentToCourse,
  createCourse,
  deleteCourse,
  listCourses,
  listUsers,
  removeTeacherFromCourse,
  removeStudentFromCourse,
  updateCourse
} from '../../api/admin';
import Navbar from '../../components/Navbar';

export default function CoursesPage() {
  const [courses, setCourses] = useState([]);
  const [users, setUsers] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [students, setStudents] = useState([]);
  const [form, setForm] = useState({ name: '', description: '', teacherId: '' });
  const [editingId, setEditingId] = useState('');
  const [editForm, setEditForm] = useState({ name: '', description: '', teacherId: '', active: true });
  const [selectedCourseId, setSelectedCourseId] = useState('');
  const [studentToAdd, setStudentToAdd] = useState('');

  const load = () => listCourses().then((r) => setCourses(r.data));
  useEffect(() => {
    load();
    listUsers().then((r) => {
      const all = r.data || [];
      setUsers(all);
      setTeachers(all.filter((u) => u.role === 'TEACHER'));
      setStudents(all.filter((u) => u.role === 'STUDENT'));
    });
  }, []);

  const submit = async (e) => {
    e.preventDefault();
    await createCourse(form);
    setForm({ name: '', description: '', teacherId: '' });
    load();
  };

  const startEdit = (course) => {
    setEditingId(course.id);
    setEditForm({
      name: course.name,
      description: course.description || '',
      teacherId: course.teacherId || '',
      active: Boolean(course.active)
    });
  };

  const saveEdit = async () => {
    await updateCourse(editingId, editForm);
    setEditingId('');
    load();
  };

  const selectedCourse = courses.find((course) => course.id === selectedCourseId);
  const selectedStudentIds = new Set(selectedCourse?.studentIds || []);
  const enrolledStudents = students.filter((student) => selectedStudentIds.has(student.id));
  const availableStudents = students.filter((student) => !selectedStudentIds.has(student.id));

  const addStudent = async () => {
    if (!selectedCourseId || !studentToAdd) {
      return;
    }
    await addStudentToCourse(selectedCourseId, studentToAdd);
    setStudentToAdd('');
    load();
  };

  const removeStudent = async (studentId) => {
    await removeStudentFromCourse(selectedCourseId, studentId);
    load();
  };

  return (
    <>
      <Navbar />
      <div className="page-shell grid gap-5 lg:grid-cols-4">
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
          <ul className="space-y-2">
            {courses.map((c) => (
              <li key={c.id} className="rounded border p-3">
                {editingId === c.id ? (
                  <div className="space-y-2">
                    <input className="w-full rounded border p-2" value={editForm.name} onChange={(e) => setEditForm({ ...editForm, name: e.target.value })} />
                    <textarea className="w-full rounded border p-2" value={editForm.description} onChange={(e) => setEditForm({ ...editForm, description: e.target.value })} />
                    <select className="w-full rounded border p-2" value={editForm.teacherId} onChange={(e) => setEditForm({ ...editForm, teacherId: e.target.value })}>
                      <option value="">Select teacher</option>
                      {teachers.map((t) => <option key={t.id} value={t.id}>{t.fullName}</option>)}
                    </select>
                    <label className="flex items-center gap-2 text-sm">
                      <input type="checkbox" checked={editForm.active} onChange={(e) => setEditForm({ ...editForm, active: e.target.checked })} /> Active
                    </label>
                    <div className="space-x-2">
                      <button type="button" onClick={saveEdit} className="rounded bg-sky-600 px-3 py-1 text-white">Save</button>
                      <button type="button" onClick={() => setEditingId('')} className="rounded bg-slate-200 px-3 py-1">Cancel</button>
                    </div>
                  </div>
                ) : (
                  <>
                    <div className="font-semibold text-slate-900">{c.name}</div>
                    <div className="text-sm text-slate-600">Teacher: {c.teacherName || 'Unassigned'}</div>
                    <div className="text-sm text-slate-600">Students: {(c.studentIds || []).length}</div>
                    <div className="mt-2 flex gap-2">
                      <button type="button" className="rounded bg-sky-100 px-3 py-1 text-sky-700" onClick={() => startEdit(c)}>Edit</button>
                      <button type="button" className="rounded bg-amber-100 px-3 py-1 text-amber-700" onClick={() => removeTeacherFromCourse(c.id).then(load)}>Remove Teacher</button>
                      <button type="button" className="rounded bg-red-100 px-3 py-1 text-red-700" onClick={() => deleteCourse(c.id).then(load)}>Delete</button>
                      <button type="button" className="rounded bg-lime-100 px-3 py-1 text-lime-700" onClick={() => setSelectedCourseId(c.id)}>Manage Students</button>
                    </div>
                  </>
                )}
              </li>
            ))}
          </ul>
        </div>

        <div className="rounded-xl bg-white p-4 shadow">
          <h2 className="mb-3 text-xl font-bold">Course Students</h2>
          <select className="mb-2 w-full rounded border p-2" value={selectedCourseId} onChange={(e) => setSelectedCourseId(e.target.value)}>
            <option value="">Select course</option>
            {courses.map((course) => <option key={course.id} value={course.id}>{course.name}</option>)}
          </select>

          {selectedCourse && (
            <>
              <div className="mb-2 text-sm text-slate-600">Teacher: {selectedCourse.teacherName || 'Unassigned'}</div>
              <div className="mb-2 flex gap-2">
                <select className="w-full rounded border p-2" value={studentToAdd} onChange={(e) => setStudentToAdd(e.target.value)}>
                  <option value="">Add student</option>
                  {availableStudents.map((student) => <option key={student.id} value={student.id}>{student.fullName}</option>)}
                </select>
                <button type="button" onClick={addStudent} className="rounded bg-sky-600 px-3 py-2 text-white">Add</button>
              </div>

              <ul className="space-y-2">
                {enrolledStudents.map((student) => (
                  <li key={student.id} className="flex items-center justify-between rounded border p-2 text-sm">
                    <span>{student.fullName}</span>
                    <button type="button" className="text-red-600" onClick={() => removeStudent(student.id)}>Remove</button>
                  </li>
                ))}
              </ul>
              {enrolledStudents.length === 0 && <p className="text-sm text-slate-600">No students enrolled.</p>}
            </>
          )}
        </div>
      </div>
    </>
  );
}
