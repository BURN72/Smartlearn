import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import API from '../../services/api'
import Navbar from '../../components/layout/Navbar'
import { useAuth } from '../../context/AuthContext'

export default function StudentCourseView() {
  const { id } = useParams()
  const { user } = useAuth()
  const [course, setCourse] = useState(null)
  const [progress, setProgress] = useState([])
  const [loading, setLoading] = useState(true)
  const [marking, setMarking] = useState(null)

  useEffect(() => {
    Promise.all([
      API.get(`/courses/${id}`),
      API.get(`/progress/course/${id}`) // ✅ GET /api/progress/course/{courseId}
    ]).then(([courseRes, progressRes]) => {
      setCourse(courseRes.data)
      setProgress(progressRes.data)
    }).catch(err => console.error(err))
    .finally(() => setLoading(false))
  }, [id])

  const isCompleted = (lessonId) =>
    progress.some(p => p.lessonId === lessonId && p.isCompleted)

  const handleMarkComplete = async (lessonId) => {
    setMarking(lessonId)
    try {
      await API.post(`/progress/mark-complete/${lessonId}`) // ✅ POST /api/progress/mark-complete/{lessonId}
      const res = await API.get(`/progress/course/${id}`)
      setProgress(res.data)
    } catch (err) {
      console.error(err)
    } finally { setMarking(null) }
  }

  const totalLessons = course?.modules?.reduce((acc, m) => acc + (m.lessons?.length || 0), 0) || 0
  const completedLessons = progress.filter(p => p.isCompleted).length
  const progressPct = totalLessons > 0 ? Math.round((completedLessons / totalLessons) * 100) : 0

  if (loading) return <div className="min-h-screen bg-gray-50"><Navbar /><div className="text-center py-20 text-gray-400">Chargement...</div></div>

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-5xl mx-auto px-4 py-10">

        <div className="flex items-center gap-3 mb-6">
          <Link to="/dashboard" className="text-blue-600 hover:underline text-sm">← Mon dashboard</Link>
          <span className="text-gray-300">/</span>
          <h1 className="text-2xl font-bold text-gray-800">{course?.title}</h1>
        </div>

        {/* Progression globale */}
        <div className="bg-white rounded-xl border border-gray-100 p-6 mb-8">
          <div className="flex items-center justify-between mb-3">
            <h2 className="font-semibold text-gray-700">Ma progression</h2>
            <span className="text-2xl font-bold text-blue-600">{progressPct}%</span>
          </div>
          <div className="w-full bg-gray-100 rounded-full h-3 mb-2">
            <div className="bg-blue-500 h-3 rounded-full transition-all" style={{ width: `${progressPct}%` }}/>
          </div>
          <p className="text-sm text-gray-400">{completedLessons} / {totalLessons} leçons complétées</p>

          {progressPct === 100 && (
            <div className="mt-4 bg-green-50 text-green-700 px-4 py-3 rounded-lg text-sm font-medium">
              🎉 Félicitations ! Vous avez complété ce cours. Votre certificat a été généré.
            </div>
          )}
        </div>

        {/* Modules et leçons */}
        <div className="space-y-4">
          {course?.modules?.map((module, i) => (
            <div key={module.id} className="bg-white rounded-xl border border-gray-100 overflow-hidden">
              <div className="px-5 py-4 border-b border-gray-50 bg-gray-50">
                <h3 className="font-semibold text-gray-800">Module {i + 1} : {module.title}</h3>
                {module.description && <p className="text-sm text-gray-500 mt-1">{module.description}</p>}
              </div>

              <div className="divide-y divide-gray-50">
                {module.lessons?.map((lesson, j) => (
                  <div key={lesson.id} className="flex items-center justify-between px-5 py-4">
                    <div className="flex items-center gap-3 flex-1">
                      <div className={`w-6 h-6 rounded-full flex items-center justify-center flex-shrink-0 ${
                        isCompleted(lesson.id) ? 'bg-green-500' : 'bg-gray-200'
                      }`}>
                        {isCompleted(lesson.id) && (
                          <span className="text-white text-xs">✓</span>
                        )}
                      </div>
                      <div>
                        <p className={`font-medium text-sm ${isCompleted(lesson.id) ? 'text-gray-400 line-through' : 'text-gray-800'}`}>
                          {j + 1}. {lesson.title}
                        </p>
                        <div className="flex gap-2 mt-0.5">
                          <span className="text-xs bg-gray-100 text-gray-500 px-2 py-0.5 rounded-full">{lesson.type}</span>
                          {lesson.durationMinutes > 0 && (
                            <span className="text-xs text-gray-400">{lesson.durationMinutes} min</span>
                          )}
                        </div>
                      </div>
                    </div>

                    {!isCompleted(lesson.id) ? (
                      <button onClick={() => handleMarkComplete(lesson.id)} disabled={marking === lesson.id}
                        className="bg-blue-50 text-blue-600 px-3 py-1.5 rounded-lg text-sm font-medium hover:bg-blue-100 transition disabled:opacity-50 flex-shrink-0">
                        {marking === lesson.id ? '...' : 'Marquer terminé'}
                      </button>
                    ) : (
                      <span className="text-green-500 text-sm font-medium flex-shrink-0">✓ Terminé</span>
                    )}
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}