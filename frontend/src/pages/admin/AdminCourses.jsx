import { useState, useEffect } from 'react'
import API from '../../services/api'
import Navbar from '../../components/layout/Navbar'

export default function AdminCourses() {
  const [courses, setCourses] = useState([])
  const [loading, setLoading] = useState(true)
  const [filter, setFilter] = useState('EN_REVISION')

  useEffect(() => {
    fetchCourses()
  }, [filter])

  const fetchCourses = () => {
    setLoading(true)
    API.get(`/admin/courses?status=${filter}`)
      .then(res => setCourses(res.data))
      .catch(() => API.get('/admin/courses/analytics')
        .then(res => setCourses(res.data.filter(c => c.status === filter)))
      )
      .finally(() => setLoading(false))
  }

  const handleValidate = async (courseId, approved) => {
    try {
      await API.patch(`/courses/${courseId}/validate`, { approved })
      fetchCourses()
    } catch (err) {
      console.error(err)
    }
  }

  const statusColors = {
    BROUILLON: 'bg-gray-100 text-gray-600',
    EN_REVISION: 'bg-yellow-100 text-yellow-700',
    PUBLIÉ: 'bg-green-100 text-green-700',
    REJETÉ: 'bg-red-100 text-red-600',
    ARCHIVÉ: 'bg-gray-100 text-gray-500',
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-6xl mx-auto px-4 py-10">

        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-800">Validation des cours</h1>
          <p className="text-gray-500 mt-1">Approuvez ou rejetez les cours soumis par les enseignants</p>
        </div>

        {/* Filtres */}
        <div className="flex gap-2 mb-6">
          {['EN_REVISION', 'PUBLIÉ', 'REJETÉ', 'BROUILLON'].map(s => (
            <button
              key={s}
              onClick={() => setFilter(s)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition ${
                filter === s
                  ? 'bg-blue-600 text-white'
                  : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
              }`}
            >
              {s.replace('_', ' ')}
            </button>
          ))}
        </div>

        {loading ? (
          <div className="text-center py-20 text-gray-400">Chargement...</div>
        ) : courses.length === 0 ? (
          <div className="text-center py-20 text-gray-400">Aucun cours avec ce statut</div>
        ) : (
          <div className="space-y-4">
            {courses.map(course => (
              <div key={course.courseId || course.id} className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                      <h3 className="font-semibold text-gray-800 text-lg">
                        {course.courseTitle || course.title}
                      </h3>
                      <span className={`text-xs px-2 py-1 rounded-full font-medium ${statusColors[course.status]}`}>
                        {course.status}
                      </span>
                    </div>
                    <p className="text-sm text-gray-500 mb-1">
                      Enseignant : <span className="font-medium">{course.instructorName}</span>
                    </p>
                    <p className="text-sm text-gray-500">
                      Prix : <span className="font-medium">
                        {course.price === 0 ? 'Gratuit' : `${course.price} XAF`}
                      </span>
                    </p>
                  </div>

                  {(course.status === 'EN_REVISION') && (
                    <div className="flex gap-2 ml-4">
                      <button
                        onClick={() => handleValidate(course.courseId || course.id, true)}
                        className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-green-700 transition"
                      >
                        Approuver
                      </button>
                      <button
                        onClick={() => handleValidate(course.courseId || course.id, false)}
                        className="bg-red-50 text-red-600 px-4 py-2 rounded-lg text-sm font-medium hover:bg-red-100 transition"
                      >
                        Rejeter
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}