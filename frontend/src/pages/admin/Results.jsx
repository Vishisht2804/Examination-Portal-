import { useEffect, useState } from 'react';
import { examResults, listExams } from '../../api/admin';
import Navbar from '../../components/Navbar';

export default function AdminResults() {
  const [exams, setExams] = useState([]);
  const [selected, setSelected] = useState('');
  const [rows, setRows] = useState([]);

  useEffect(() => { listExams().then((r) => setExams(r.data)); }, []);

  useEffect(() => {
    if (!selected) return;
    examResults(selected).then((r) => setRows(r.data));
  }, [selected]);

  return (
    <>
      <Navbar />
      <div className="page-shell rounded-xl bg-white p-5 shadow">
        <h2 className="mb-3 text-xl font-bold">Exam Results</h2>
        <select className="mb-4 rounded border p-2" value={selected} onChange={(e) => setSelected(e.target.value)}>
          <option value="">Select exam</option>
          {exams.map((e) => <option key={e.id} value={e.id}>{e.title}</option>)}
        </select>
        <table className="w-full text-sm"><thead><tr><th className="text-left">Student</th><th className="text-left">Score</th><th className="text-left">Status</th></tr></thead>
          <tbody>{rows.map((r) => <tr className="border-t" key={r.attemptId}><td>{r.studentName}</td><td>{r.score}/{r.totalMarks}</td><td>{r.passed ? 'Passed' : 'Failed'}</td></tr>)}</tbody></table>
      </div>
    </>
  );
}
