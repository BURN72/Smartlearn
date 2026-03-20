import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import API from '../../services/api'

export default function Navbar() {
  const { user, logout, isAdmin, isInstructor } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    const refreshToken = localStorage.getItem('refreshToken')
    try {
      await API.post('/auth/logout', { refreshToken })
    } catch {}
    logout()
    navigate('/login')
  }

  return (
    <nav className="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
      <Link to="/" className="text-xl font-bold text-blue-600">SmartLearn</Link>

      <div className="flex items-center gap-6">
        <Link to="/courses" className="text-gray-600 hover:text-blue-600 text-sm font-medium">
          Catalogue
        </Link>

        {user ? (
          <>
            {isAdmin() && (
              <Link to="/admin" className="text-gray-600 hover:text-blue-600 text-sm font-medium">
                Admin
              </Link>
            )}
            {isInstructor() && (
              <Link to="/instructor" className="text-gray-600 hover:text-blue-600 text-sm font-medium">
                Mes cours
              </Link>
            )}
            <Link to={
              isAdmin() ? '/admin' :
                isInstructor() ? '/instructor' :
                  '/dashboard'
            } className="text-gray-600 hover:text-blue-600 text-sm font-medium">
              Dashboard
            </Link>
            <div className="flex items-center gap-3">
              <span className="text-sm text-gray-700 font-medium">{user.name}</span>
              <button
                onClick={handleLogout}
                className="bg-red-50 text-red-600 px-4 py-1.5 rounded-lg text-sm font-medium hover:bg-red-100 transition"
              >
                Déconnexion
              </button>
            </div>
          </>
        ) : (
          <div className="flex items-center gap-3">
            <Link to="/login" className="text-gray-600 hover:text-blue-600 text-sm font-medium">
              Connexion
            </Link>
            <Link to="/register" className="bg-blue-600 text-white px-4 py-1.5 rounded-lg text-sm font-medium hover:bg-blue-700 transition">
              S'inscrire
            </Link>
          </div>
        )}
      </div>
    </nav>
  )
}