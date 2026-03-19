import { useState, useEffect } from 'react'
import API from '../../services/api'
import Navbar from '../../components/layout/Navbar'

export default function AdminUsers() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('')

  useEffect(() => { fetchUsers() }, [filter])

  const fetchUsers = () => {
    setLoading(true)
    const url = filter ? `/admin/users/role/${filter}` : '/admin/users' // ✅ URLs correctes
    API.get(url)
      .then(res => setUsers(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false))
  }

  const handleToggleStatus = async (userId) => {
    try {
      await API.post(`/admin/users/${userId}/toggle-status`) // ✅ URL correcte
      fetchUsers()
    } catch (err) { console.error(err) }
  }

  const roleColors = {
    ROLE_ADMIN: 'bg-red-50 text-red-600',
    ROLE_INSTRUCTOR: 'bg-purple-50 text-purple-600',
    ROLE_STUDENT: 'bg-blue-50 text-blue-600',
  }

  const roleLabels = {
    ROLE_ADMIN: 'Admin',
    ROLE_INSTRUCTOR: 'Enseignant',
    ROLE_STUDENT: 'Étudiant',
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-6xl mx-auto px-4 py-10">

        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-800">Gestion des utilisateurs</h1>
          <p className="text-gray-500 mt-1">Gérez les comptes de la plateforme</p>
        </div>

        <div className="flex gap-2 mb-6">
          {[
            { val: '', label: 'Tous' },
            { val: 'ROLE_STUDENT', label: 'Étudiants' },
            { val: 'ROLE_INSTRUCTOR', label: 'Enseignants' },
            { val: 'ROLE_ADMIN', label: 'Admins' },
          ].map(f => (
            <button key={f.val} onClick={() => setFilter(f.val)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
                filter === f.val ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
              }`}>
              {f.label}
            </button>
          ))}
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="text-left px-6 py-3 text-sm font-medium text-gray-500">Nom</th>
                <th className="text-left px-6 py-3 text-sm font-medium text-gray-500">Email</th>
                <th className="text-left px-6 py-3 text-sm font-medium text-gray-500">Rôle</th>
                <th className="text-left px-6 py-3 text-sm font-medium text-gray-500">Statut</th>
                <th className="text-left px-6 py-3 text-sm font-medium text-gray-500">Cours</th>
                <th className="text-right px-6 py-3 text-sm font-medium text-gray-500">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr><td colSpan="6" className="text-center py-10 text-gray-400">Chargement...</td></tr>
              ) : users.length === 0 ? (
                <tr><td colSpan="6" className="text-center py-10 text-gray-400">Aucun utilisateur</td></tr>
              ) : users.map(u => (
                <tr key={u.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 font-medium text-gray-800">{u.name}</td>
                  <td className="px-6 py-4 text-gray-500 text-sm">{u.email}</td>
                  <td className="px-6 py-4">
                    <span className={`text-xs px-2 py-1 rounded-full font-medium ${roleColors[u.role] || 'bg-gray-100 text-gray-600'}`}>
                      {roleLabels[u.role] || u.role}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`text-xs px-2 py-1 rounded-full font-medium ${u.active ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'}`}>
                      {u.active ? 'Actif' : 'Désactivé'}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-gray-500 text-sm">
                    {u.role === 'ROLE_INSTRUCTOR' ? `${u.coursesCreated} créés` :
                     u.role === 'ROLE_STUDENT' ? `${u.coursesEnrolled} inscrits` : '—'}
                  </td>
                  <td className="px-6 py-4 text-right">
                    <button onClick={() => handleToggleStatus(u.id)}
                      className={`text-sm font-medium ${u.active ? 'text-red-500 hover:text-red-700' : 'text-green-500 hover:text-green-700'}`}>
                      {u.active ? 'Désactiver' : 'Activer'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}