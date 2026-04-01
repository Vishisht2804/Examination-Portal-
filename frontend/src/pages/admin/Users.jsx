import { useEffect, useState } from 'react';
import { createUser, deactivateUser, listUsers } from '../../api/admin';
import Navbar from '../../components/Navbar';

export default function UsersPage() {
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState({ fullName: '', username: '', email: '', password: '', role: 'STUDENT' });

  const load = () => listUsers().then((r) => setUsers(r.data));
  useEffect(() => { load(); }, []);

  const submit = async (e) => {
    e.preventDefault();
    await createUser(form);
    setForm({ fullName: '', username: '', email: '', password: '', role: 'STUDENT' });
    load();
  };

  return (
    <>
      <Navbar />
      <div className="page-shell grid gap-5 lg:grid-cols-3">
        <form className="rounded-xl bg-white p-4 shadow lg:col-span-1" onSubmit={submit}>
          <h2 className="mb-3 text-xl font-bold">Create User</h2>
          {['fullName', 'username', 'email', 'password'].map((k) => (
            <input key={k} className="mb-2 w-full rounded border p-2" placeholder={k} value={form[k]} onChange={(e) => setForm({ ...form, [k]: e.target.value })} />
          ))}
          <select className="mb-3 w-full rounded border p-2" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
            <option>STUDENT</option><option>TEACHER</option><option>ADMIN</option>
          </select>
          <button className="w-full rounded bg-sky-600 py-2 text-white">Create</button>
        </form>
        <div className="rounded-xl bg-white p-4 shadow lg:col-span-2">
          <h2 className="mb-3 text-xl font-bold">Users</h2>
          <table className="w-full text-sm"><thead><tr className="text-left"><th>Name</th><th>Role</th><th>Status</th><th></th></tr></thead><tbody>
            {users.map((u) => (
              <tr key={u.id} className="border-t"><td>{u.fullName}</td><td>{u.role}</td><td>{u.active ? 'Active' : 'Inactive'}</td>
                <td>{u.active && <button className="text-red-600" onClick={() => deactivateUser(u.id).then(load)}>Deactivate</button>}</td></tr>
            ))}
          </tbody></table>
        </div>
      </div>
    </>
  );
}
