import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import API from '../../services/api'
import Navbar from '../../components/layout/Navbar'
import { useAuth } from '../../context/AuthContext'

export default function InstructorDashboard() {
  const { user } = useAuth()
  const [courses, setCourses] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [form, setForm] = useState({
    title: '',
    description: '',
    price: 0,
    level: 'DEBUTANT',
    categoryId: '',
    thumbnailUrl: ''
  })

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      const [coursesRes, categoriesRes] = await Promise.all([
        API.get('/courses/my-courses'),
        API.get('/categories')
      ])
      setCourses(coursesRes.data)
      setCategories(categoriesRes.data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess('')
    setSaving(true)
    try {
      await API.post('/courses', {
        ...form,
        price: parseFloat(form.price),
        categoryId: parseInt(form.categoryId)
      })
      setSuccess('Cours créé avec succès !')
      setForm({ title: '', description: '', price: 0, level: 'DEBUTANT', categoryId: '', thumbnailUrl: '' })
      setShowForm(false)
      fetchData()
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création')
    } finally {
      setSaving(false)
    }
  }

  const handleSubmitForReview = async (courseId) => {
    try {
      await API.post(`/courses/${courseId}/submit-review`)
      fetchData()
    } catch (err) {
      console.error(err)
    }
  }

  const statusColors = {
    BROUILLON: 'bg-gray-100 text-gray-600',
    EN_REVISION: 'bg-yellow-100 text-yellow-700',
    'PUBLIÉ': 'bg-green-100 text-green-700',
    'REJETÉ': 'bg-red-100 text-red-600',
    'ARCHIVÉ': 'bg-gray-100 text-gray-500',
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-6xl mx-auto px-4 py-10">

        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-gray-800">Mes cours</h1>
            <p className="text-gray-500 mt-1">Bonjour {user?.name}, gérez vos formations</p>
          </div>
          <button
            onClick={() => setShowForm(!showForm)}
            className="bg-blue-600 text-white px-5 py-2.5 rounded-lg font-medium hover:bg-blue-700 transition"
          >
            {showForm ? 'Annuler' : '+ Nouveau cours'}
          </button>
        </div>

        {/* Formulaire création */}
        {showForm && (
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-8">
            <h2 className="text-lg font-semibold text-gray-700 mb-4">Créer un nouveau cours</h2>

            {error && <div className="bg-red-50 text-red-600 px-4 py-3 rounded-lg mb-4 text-sm">{error}</div>}

            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-1">Titre du cours *</label>
                  <input
                    type="text"
                    required
                    value={form.title}
                    onChange={e => setForm({ ...form, title: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Ex: Introduction à Java"
                  />
                </div>

                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                  <textarea
                    rows={3}
                    value={form.description}
                    onChange={e => setForm({ ...form, description: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Décrivez votre cours..."
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Catégorie *</label>
                  <select
                    required
                    value={form.categoryId}
                    onChange={e => setForm({ ...form, categoryId: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="">Sélectionner une catégorie</option>
                    {categories.map(cat => (
                      <option key={cat.id} value={cat.id}>{cat.name}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Niveau</label>
                  <select
                    value={form.level}
                    onChange={e => setForm({ ...form, level: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="DEBUTANT">Débutant</option>
                    <option value="INTERMEDIAIRE">Intermédiaire</option>
                    <option value="AVANCE">Avancé</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Prix (XAF)</label>
                  <input
                    type="number"
                    min="0"
                    value={form.price}
                    onChange={e => setForm({ ...form, price: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="0 pour gratuit"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">URL Miniature</label>
                  <input
                    type="text"
                    value={form.thumbnailUrl}
                    onChange={e => setForm({ ...form, thumbnailUrl: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="https://..."
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={saving}
                className="bg-blue-600 text-white px-6 py-2.5 rounded-lg font-medium hover:bg-blue-700 transition disabled:opacity-50"
              >
                {saving ? 'Création...' : 'Créer le cours'}
              </button>
            </form>
          </div>
        )}

        {success && (
          <div className="bg-green-50 text-green-600 px-4 py-3 rounded-lg mb-6 text-sm">{success}</div>
        )}

        {/* Stats */}
        <div className="grid grid-cols-3 gap-4 mb-8">
          <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100 text-center">
            <p className="text-3xl font-bold text-blue-600">{courses.length}</p>
            <p className="text-gray-500 text-sm mt-1">Total cours</p>
          </div>
          <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100 text-center">
            <p className="text-3xl font-bold text-green-500">
              {courses.filter(c => c.status === 'PUBLIÉ').length}
            </p>
            <p className="text-gray-500 text-sm mt-1">Publiés</p>
          </div>
          <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100 text-center">
            <p className="text-3xl font-bold text-yellow-500">
              {courses.filter(c => c.status === 'EN_REVISION').length}
            </p>
            <p className="text-gray-500 text-sm mt-1">En révision</p>
          </div>
        </div>

        {/* Liste des cours */}
        {loading ? (
          <div className="text-center py-10 text-gray-400">Chargement...</div>
        ) : courses.length === 0 ? (
          <div className="text-center py-16 bg-white rounded-xl border border-gray-100">
            <p className="text-gray-400 mb-4">Vous n'avez pas encore créé de cours</p>
            <button
              onClick={() => setShowForm(true)}
              className="bg-blue-600 text-white px-6 py-2.5 rounded-lg font-medium hover:bg-blue-700"
            >
              Créer mon premier cours
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {courses.map(course => (
              <div key={course.id} className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                      <h3 className="font-semibold text-gray-800 text-lg">{course.title}</h3>
                      <span className={`text-xs px-2 py-1 rounded-full font-medium ${statusColors[course.status] || 'bg-gray-100 text-gray-600'}`}>
                        {course.status}
                      </span>
                    </div>
                    <p className="text-sm text-gray-500 mb-2 line-clamp-2">{course.description}</p>
                    <div className="flex gap-4 text-sm text-gray-400">
                      <span>{course.totalModules || 0} modules</span>
                      <span>{course.totalEnrollments || 0} étudiants</span>
                      <span>{course.price === 0 ? 'Gratuit' : `${course.price} XAF`}</span>
                    </div>
                  </div>

                  <div className="flex gap-2 ml-4">
                    {course.status === 'BROUILLON' && (
                      <button
                        onClick={() => handleSubmitForReview(course.id)}
                        className="bg-yellow-50 text-yellow-700 px-4 py-2 rounded-lg text-sm font-medium hover:bg-yellow-100 transition"
                      >
                        Soumettre
                      </button>
                    )}
                    <Link
                      to={`/instructor/courses/${course.id}`}
                      className="bg-blue-50 text-blue-600 px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-100 transition"
                    >
                      Gérer →
                    </Link>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}