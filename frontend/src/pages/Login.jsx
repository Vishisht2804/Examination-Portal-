import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    const username = form.username.trim();
    const password = form.password.trim();
    if (!username || !password) {
      setError('Username and password are required');
      return;
    }
    try {
      const profile = await login(username, password);
      if (profile.role === 'ADMIN') navigate('/admin');
      if (profile.role === 'TEACHER') navigate('/teacher');
      if (profile.role === 'STUDENT') navigate('/student');
    } catch (err) {
      const msg = err?.response?.data?.message;
      if (msg) {
        setError(msg);
      } else {
        setError('Cannot reach server. Check backend on http://localhost:8081');
      }
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center p-4">
      <form className="w-full max-w-md rounded-xl bg-white p-6 shadow" onSubmit={submit}>
        <h1 className="mb-5 text-2xl font-bold">OEMS Login</h1>
        <input className="mb-3 w-full rounded border p-3" placeholder="Username" value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} />
        <input className="mb-3 w-full rounded border p-3" type="password" placeholder="Password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        {error && <p className="mb-3 text-sm text-red-600">{error}</p>}
        <button className="w-full rounded bg-sky-600 py-3 font-semibold text-white">Login</button>
      </form>
    </div>
  );
}
