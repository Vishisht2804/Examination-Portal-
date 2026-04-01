import { useEffect, useState } from 'react';

function format(seconds) {
  const mm = String(Math.floor(seconds / 60)).padStart(2, '0');
  const ss = String(seconds % 60).padStart(2, '0');
  return `${mm}:${ss}`;
}

export default function Timer({ deadline, onTimeUp }) {
  const [remaining, setRemaining] = useState(0);

  useEffect(() => {
    const tick = () => {
      const sec = Math.max(0, Math.floor((new Date(deadline).getTime() - Date.now()) / 1000));
      setRemaining(sec);
      if (sec === 0 && onTimeUp) onTimeUp();
    };
    tick();
    const id = setInterval(tick, 1000);
    return () => clearInterval(id);
  }, [deadline]);

  return <span className={remaining < 300 ? 'font-bold text-red-600' : 'font-semibold text-slate-700'}>{format(remaining)}</span>;
}
