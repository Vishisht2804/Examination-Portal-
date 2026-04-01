import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getDashboardLink = () => {
    switch (user?.role) {
      case 'ADMIN':
        return '/admin';
      case 'TEACHER':
        return '/teacher';
      case 'STUDENT':
        return '/student';
      default:
        return '/login';
    }
  };

  const getNavLinks = () => {
    switch (user?.role) {
      case 'ADMIN':
        return [
          { label: 'Dashboard', href: '/admin' },
          { label: 'Users', href: '/admin/users' },
          { label: 'Courses', href: '/admin/courses' },
          { label: 'Results', href: '/admin/results' },
        ];
      case 'TEACHER':
        return [
          { label: 'Dashboard', href: '/teacher' },
          { label: 'Exams', href: '/teacher/exams' },
        ];
      case 'STUDENT':
        return [
          { label: 'Dashboard', href: '/student' },
          { label: 'Available Exams', href: '/student/exams' },
          { label: 'My Attempts', href: '/student/attempt-history' },
        ];
      default:
        return [];
    }
  };

  return (
    <nav className="sticky top-0 z-30 border-b border-slate-200 bg-white shadow-sm">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between py-4">
          <div className="flex items-center gap-2">
            <div className="text-2xl">📝</div>
            <h1 className="text-xl font-bold text-slate-900">OEMS</h1>
          </div>

          {user && (
            <div className="flex items-center gap-6">
              <div className="hidden md:flex gap-4">
                {getNavLinks().map((link) => (
                  <a
                    key={link.href}
                    href={link.href}
                    className="text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors"
                  >
                    {link.label}
                  </a>
                ))}
              </div>
              <div className="flex items-center gap-3 border-l border-slate-200 pl-6">
                <div className="text-right">
                  <p className="text-sm font-semibold text-slate-900">{user.fullName}</p>
                  <p className="text-xs text-slate-500">{user.role}</p>
                </div>
                <button
                  onClick={handleLogout}
                  className="rounded-lg bg-slate-100 px-3 py-1.5 text-sm font-semibold text-slate-700 hover:bg-slate-200 transition-colors"
                >
                  Logout
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}
