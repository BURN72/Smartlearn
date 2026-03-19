import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import API from '../../services/api'
import Navbar from '../../components/layout/Navbar'

export default function AdminDashboard() {
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    API.get('/admin/dashboard/stats') // ✅ URL correcte
      .then(res => setStats(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="text-center py-20 text-gray-400">Chargement...</div>
    </div>
  )

  const cards = [
    { label: 'Utilisateurs', value: stats?.totalUsers, color: 'blue' },
    { label: 'Étudiants', value: stats?.totalStudents, color: 'indigo' },
    { label: 'Enseignants', value: stats?.totalInstructors, color: 'purple' },
    { label: 'Cours publiés', value: stats?.publishedCourses, color: 'green' },
    { label: 'Inscriptions', value: stats?.totalEnrollments, color: 'orange' },
    { label: 'Certificats', value: stats?.totalCertificates, color: 'yellow' },
    { label: 'Tentatives Quiz', value: stats?.totalQuizAttempts, color: 'red' },
    { label: 'Revenu total', value: `${stats?.totalRevenue?.toFixed(0) || 0} XAF`, color: 'teal' },
  ]

  const colorMap = {
    blue: 'text-blue-600', indigo: 'text-indigo-600', purple: 'text-purple-600',
    green: 'text-green-600', orange: 'text-orange-600', yellow: 'text-yellow-600',
    red: 'text-red-600', teal: 'text-teal-600',
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-6xl mx-auto px-4 py-10">

        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-800">Dashboard Admin</h1>
          <p className="text-gray-500 mt-1">Vue d'ensemble de la plateforme</p>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          {cards.map((card, i) => (
            <div key={i} className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
              <p className={`text-2xl font-bold ${colorMap[card.color]}`}>{card.value ?? '—'}</p>
              <p className="text-gray-500 text-sm mt-1">{card.label}</p>
            </div>
          ))}
        </div>

        <div className="grid grid-cols-2 gap-4 mb-8">
          <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
            <h3 className="font-semibold text-gray-700 mb-3">Taux de complétion moyen</h3>
            <p className="text-3xl font-bold text-blue-600">{stats?.averageCourseCompletion?.toFixed(1) || 0}%</p>
          </div>
          <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
            <h3 className="font-semibold text-gray-700 mb-3">Taux de réussite Quiz</h3>
            <p className="text-3xl font-bold text-green-600">{stats?.averageQuizPassRate?.toFixed(1) || 0}%</p>
          </div>
        </div>

        <div className="flex gap-4">
          <Link to="/admin/categories" className="bg-blue-600 text-white px-5 py-2.5 rounded-lg font-medium hover:bg-blue-700 transition">
            Gérer les catégories
          </Link>
          <Link to="/admin/courses" className="bg-purple-600 text-white px-5 py-2.5 rounded-lg font-medium hover:bg-purple-700 transition">
            Valider les cours
          </Link>
          <Link to="/admin/users" className="bg-gray-700 text-white px-5 py-2.5 rounded-lg font-medium hover:bg-gray-800 transition">
            Gérer les utilisateurs
          </Link>
        </div>
      </div>
    </div>
  )
}