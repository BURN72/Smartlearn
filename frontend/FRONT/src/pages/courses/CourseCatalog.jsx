import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import API from '../../services/api'
import Navbar from '../../components/layout/Navbar'
import { useAuth } from '../../context/AuthContext'

export default function CourseCatalog() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [courses, setCourses] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('')
  const [enrolling, setEnrolling] = useState(null)
  const [message, setMessage] = useState('')
  const [userEnrollments, setUserEnrollments] = useState([])
  const [instructorCourses, setInstructorCourses] = useState([])

  useEffect(() => {
    const loadData = async () => {
      try {
        const [coursesRes, categoriesRes] = await Promise.all([
          API.get('/courses/published/all'),
          API.get('/categories')
        ])
        setCourses(coursesRes.data)
        setCategories(categoriesRes.data)

        // Récupérer les inscriptions si l'utilisateur est connecté
        if (user) {
          if (user?.role === 'ROLE_STUDENT') {
            try {
              const enrollmentsRes = await API.get('/enrollments/me')
              setUserEnrollments(enrollmentsRes.data)
            } catch (err) {
              console.error('Erreur lors du chargement des inscriptions:', err)
            }
          }

          if (user?.role === 'ROLE_INSTRUCTOR') {
            try {
              const myCoursesRes = await API.get('/courses/instructor/my-courses')
              setInstructorCourses(myCoursesRes.data)
            } catch (err) {
              console.error('Erreur lors du chargement des cours instructeur:', err)
            }
          }
        }
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [user])

  const filtered = courses.filter(c => {
    const matchSearch = c.title?.toLowerCase().includes(search.toLowerCase()) ||
      c.description?.toLowerCase().includes(search.toLowerCase())
    const matchCategory = !selectedCategory || c.categoryId === parseInt(selectedCategory)
    return matchSearch && matchCategory
  })

  const isEnrolled = (courseId) => userEnrollments.some(e => e.courseId === courseId)
  const isOwnCourse = (courseId) => instructorCourses.some(c => c.id === courseId)
  const getButtonAction = (course) => {
    if (!user) return 'login'
    if (user?.role === 'ROLE_INSTRUCTOR' && isOwnCourse(course.id)) return 'manage'
    if (user?.role === 'ROLE_STUDENT' && isEnrolled(course.id)) return 'start'
    return 'enroll'
  }

  const handleEnroll = async (courseId) => {
    if (!user) {
      navigate('/login')
      return
    }
    setEnrolling(courseId)
    setMessage('')
    try {
      await API.post('/enrollments', { courseId })
      setMessage('Inscription réussie !')
      const enrollmentsRes = await API.get('/enrollments/me')
      setUserEnrollments(enrollmentsRes.data)
    } catch (err) {
      setMessage(err.response?.data?.message || 'Erreur lors de l\'inscription')
    } finally {
      setEnrolling(null)
    }
  }

  const handleStartCourse = (courseId) => {
    navigate(`/learn/${courseId}`)
  }

  const handleManageCourse = (courseId) => {
    navigate(`/instructor/courses/${courseId}`)
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-6xl mx-auto px-4 py-10">

        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">Catalogue des cours</h1>
          <p className="text-gray-500">Découvrez toutes nos formations disponibles</p>
        </div>

        {message && (
          <div className={`px-4 py-3 rounded-lg mb-6 text-sm ${
            message.includes('réussie') ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'
          }`}>{message}</div>
        )}

        <div className="flex gap-4 mb-8">
          <input type="text" placeholder="Rechercher un cours..."
            value={search} onChange={e => setSearch(e.target.value)}
            className="flex-1 border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"/>
          <select value={selectedCategory} onChange={e => setSelectedCategory(e.target.value)}
            className="border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option value="">Toutes les catégories</option>
            {categories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
        </div>

        {loading ? (
          <div className="text-center py-20 text-gray-400">Chargement...</div>
        ) : filtered.length === 0 ? (
          <div className="text-center py-20 text-gray-400">Aucun cours disponible</div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filtered.map(course => (
              <div key={course.id} className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition">
                {course.thumbnailUrl ? (
                  <img src={course.thumbnailUrl} alt={course.title} className="w-full h-44 object-cover"/>
                ) : (
                  <div className="w-full h-44 bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center">
                    <span className="text-white text-4xl font-bold">{course.title?.[0]}</span>
                  </div>
                )}
                <div className="p-5">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs bg-blue-50 text-blue-600 px-2 py-1 rounded-full font-medium">
                      {course.categoryName}
                    </span>
                    <span className="text-xs text-gray-400">{course.level}</span>
                  </div>
                  <h3 className="font-semibold text-gray-800 mb-1 line-clamp-2">{course.title}</h3>
                  <p className="text-sm text-gray-500 mb-3 line-clamp-2">{course.description}</p>
                  <div className="flex items-center justify-between mb-4">
                    <span className="text-sm text-gray-400">Par {course.instructorName}</span>
                    <span className="font-bold text-blue-600">
                      {!course.price || course.price === 0 ? 'Gratuit' : `${course.price} XAF`}
                    </span>
                  </div>
                  <div className="flex gap-2">
                    <Link to={`/courses/${course.id}`}
                      className="flex-1 text-center border border-blue-600 text-blue-600 py-2 rounded-lg text-sm font-medium hover:bg-blue-50 transition">
                      Voir le cours
                    </Link>
                    {(() => {
                      const action = getButtonAction(course)
                      if (action === 'login') {
                        return (
                          <button onClick={() => navigate('/login')} disabled={enrolling === course.id}
                            className="flex-1 bg-blue-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition disabled:opacity-50">
                            Se connecter
                          </button>
                        )
                      } else if (action === 'manage') {
                        return (
                          <button onClick={() => handleManageCourse(course.id)} disabled={enrolling === course.id}
                            className="flex-1 bg-green-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-green-700 transition disabled:opacity-50">
                            Gérer
                          </button>
                        )
                      } else if (action === 'start') {
                        return (
                          <button onClick={() => handleStartCourse(course.id)} disabled={enrolling === course.id}
                            className="flex-1 bg-green-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-green-700 transition disabled:opacity-50">
                            Commencer
                          </button>
                        )
                      } else {
                        return (
                          <button onClick={() => handleEnroll(course.id)} disabled={enrolling === course.id}
                            className="flex-1 bg-blue-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition disabled:opacity-50">
                            {enrolling === course.id ? '...' : "S'inscrire"}
                          </button>
                        )
                      }
                    })()}
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