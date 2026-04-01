import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { teacherExamResults } from '../../api/teacher';
import Navbar from '../../components/Navbar';

export default function ExamResults() {
  const { examId } = useParams();
  const [rows, setRows] = useState([]);
  useEffect(() => { teacherExamResults(examId).then((r) => setRows(r.data)); }, [examId]);

  return (
    <>
      <Navbar />
      <div className="page-shell rounded-xl bg-white p-5 shadow">
        <h2 className="mb-3 text-xl font-bold">Student Attempts</h2>
        <table className="w-full text-sm"><thead><tr><th className="text-left">Name</th><th className="text-left">Score</th><th className="text-left">Status</th></tr></thead>
          <tbody>{rows.map((r) => <tr key={r.attemptId} className="border-t"><td>{r.studentName}</td><td>{r.score}/{r.totalMarks}</td><td>{r.status}</td></tr>)}</tbody></table>
      </div>
    </>
  );
}
