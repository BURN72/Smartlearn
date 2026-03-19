import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import API from '../../services/api'
import Navbar from '../../components/layout/Navbar'
import { useAuth } from '../../context/AuthContext'

export default function StudentDashboard() {
  const { user } = useAuth()
  const [enrollments, setEnrollments] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    API.get('/enrollments/my')
      .then(res => setEnrollments(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false))
  }, [])

  const completed = enrollments.filter(e => e.progress >= 100)
  const inProgress = enrollments.filter(e => e.progress > 0 && e.progress < 100)

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-6xl mx-auto px-4 py-10">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-800">Bonjour, {user?.name} 👋</h1>
          <p className="text-gray-500 mt-1">Voici votre progression</p>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-4 mb-10">
          <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100 text-center">
            <p className="text-3xl font-bold text-blue-600">{enrollments.length}</p>
            <p className="text-gray-500 text-sm mt-1">Cours inscrits</p>
          </div>
          <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100 text-center">
            <p className="text-3xl font-bold text-orange-500">{inProgress.length}</p>
            <p className="text-gray-500 text-sm mt-1">En cours</p>
          </div>
          <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100 text-center">
            <p className="text-3xl font-bold text-green-500">{completed.length}</p>
            <p className="text-gray-500 text-sm mt-1">Terminés</p>
          </div>
        </div>

        {/* Cours en cours */}
        <h2 className="text-lg font-semibold text-gray-700 mb-4">Mes cours</h2>

        {loading ? (
          <div className="text-center py-10 text-gray-400">Chargement...</div>
        ) : enrollments.length === 0 ? (
          <div className="text-center py-10">
            <p className="text-gray-400 mb-4">Vous n'êtes inscrit à aucun cours</p>
            <Link to="/courses" className="bg-blue-600 text-white px-6 py-2.5 rounded-lg font-medium hover:bg-blue-700 transition">
              Parcourir le catalogue
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {enrollments.map(e => (
              <div key={e.id} className="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
                <div className="flex items-start justify-between mb-3">
                  <h3 className="font-semibold text-gray-800">{e.courseName}</h3>
                  <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                    e.status === 'ACTIF' ? 'bg-green-50 text-green-600' :
                    e.status === 'TERMINE' ? 'bg-blue-50 text-blue-600' :
                    'bg-yellow-50 text-yellow-600'
                  }`}>
                    {e.status}
                  </span>
                </div>
                <div className="mb-2">
                  <div className="flex justify-between text-sm text-gray-500 mb-1">
                    <span>Progression</span>
                    <span>{e.progress}%</span>
                  </div>
                  <div className="w-full bg-gray-100 rounded-full h-2">
                    <div
                      className="bg-blue-500 h-2 rounded-full transition-all"
                      style={{ width: `${e.progress}%` }}
                    />
                  </div>
                </div>
                <Link
                  to={`/courses/${e.courseId}`}
                  className="text-sm text-blue-600 font-medium hover:underline"
                >
                  Continuer →
                </Link>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}