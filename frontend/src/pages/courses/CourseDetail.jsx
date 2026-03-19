import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import API from '../../services/api'
import Navbar from '../../components/layout/Navbar'
import { useAuth } from '../../context/AuthContext'

export default function CourseDetail() {
  const { id } = useParams()
  const { user } = useAuth()
  const navigate = useNavigate()
  const [course, setCourse] = useState(null)
  const [loading, setLoading] = useState(true)
  const [enrolling, setEnrolling] = useState(false)
  const [message, setMessage] = useState('')

  useEffect(() => {
    API.get(`/courses/${id}`) // ✅ URL correcte → CourseDetailResponse
      .then(res => setCourse(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false))
  }, [id])

  const handleEnroll = async () => {
    if (!user) { navigate('/login'); return }
    setEnrolling(true)
    setMessage('')
    try {
      await API.post('/enrollments', { courseId: parseInt(id) }) // ✅ URL correcte
      setMessage('Inscription réussie ! Rendez-vous dans votre dashboard.')
    } catch (err) {
      setMessage(err.response?.data?.message || 'Erreur lors de l\'inscription')
    } finally {
      setEnrolling(false)
    }
  }

  if (loading) return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="text-center py-20 text-gray-400">Chargement...</div>
    </div>
  )

  if (!course) return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="text-center py-20 text-gray-400">Cours introuvable</div>
    </div>
  )

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-5xl mx-auto px-4 py-10">

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">

          {/* Contenu principal */}
          <div className="lg:col-span-2">
            {course.thumbnailUrl ? (
              <img src={course.thumbnailUrl} alt={course.title} className="w-full h-64 object-cover rounded-xl mb-6"/>
            ) : (
              <div className="w-full h-64 bg-gradient-to-br from-blue-400 to-blue-600 rounded-xl flex items-center justify-center mb-6">
                <span className="text-white text-6xl font-bold">{course.title?.[0]}</span>
              </div>
            )}

            <h1 className="text-3xl font-bold text-gray-800 mb-3">{course.title}</h1>
            <div className="flex gap-3 mb-4">
              <span className="bg-blue-50 text-blue-600 text-sm px-3 py-1 rounded-full">{course.categoryName}</span>
              <span className="bg-gray-100 text-gray-600 text-sm px-3 py-1 rounded-full">{course.level}</span>
            </div>
            <p className="text-gray-600 mb-6">{course.description}</p>

            <div className="flex gap-6 text-sm text-gray-500 mb-8">
              <span>Par <span className="font-medium text-gray-700">{course.instructorName}</span></span>
              <span>{course.totalLessons || 0} leçons</span>
              <span>{course.enrollmentCount || 0} étudiants</span>
            </div>

            {/* Modules */}
            {course.modules && course.modules.length > 0 && (
              <div>
                <h2 className="text-xl font-semibold text-gray-800 mb-4">Contenu du cours</h2>
                <div className="space-y-3">
                  {course.modules.map((module, i) => (
                    <div key={module.id} className="bg-white rounded-xl border border-gray-100 p-4">
                      <h3 className="font-medium text-gray-800 mb-2">
                        Module {i + 1} : {module.title}
                      </h3>
                      {module.lessons && (
                        <ul className="space-y-1">
                          {module.lessons.map(lesson => (
                            <li key={lesson.id} className="text-sm text-gray-500 flex items-center gap-2">
                              <span className="text-blue-400">▸</span>
                              {lesson.title}
                              {lesson.isFree && (
                                <span className="text-xs bg-green-50 text-green-600 px-2 py-0.5 rounded-full">Gratuit</span>
                              )}
                            </li>
                          ))}
                        </ul>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Sidebar inscription */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 sticky top-6">
              <p className="text-3xl font-bold text-blue-600 mb-4">
                {!course.price || course.price === 0 ? 'Gratuit' : `${course.price} XAF`}
              </p>

              {message && (
                <div className={`px-4 py-3 rounded-lg mb-4 text-sm ${
                  message.includes('réussie') ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'
                }`}>{message}</div>
              )}

              <button onClick={handleEnroll} disabled={enrolling}
                className="w-full bg-blue-600 text-white py-3 rounded-lg font-medium hover:bg-blue-700 transition disabled:opacity-50 mb-4">
                {enrolling ? 'Inscription...' : "S'inscrire au cours"}
              </button>

              <div className="space-y-2 text-sm text-gray-500">
                <p>✓ Accès illimité</p>
                <p>✓ Certificat de complétion</p>
                <p>✓ Quiz d'évaluation</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}