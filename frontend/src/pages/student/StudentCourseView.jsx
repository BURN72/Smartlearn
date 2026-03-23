import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import API from '../../services/api'
import Navbar from '../../components/layout/Navbar'
import { useAuth } from '../../context/AuthContext'

export default function StudentCourseView() {
  const { id: courseId } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const [course, setCourse] = useState(null)
  const [progress, setProgress] = useState([])
  const [loading, setLoading] = useState(true)
  const [marking, setMarking] = useState(false)
  const [showQuizMessage, setShowQuizMessage] = useState(false)

  // État pour la navigation des leçons
  const [currentModuleIndex, setCurrentModuleIndex] = useState(0)
  const [currentLessonIndex, setCurrentLessonIndex] = useState(0)

  useEffect(() => {
    const loadData = async () => {
      try {
        const [courseRes, progressRes] = await Promise.all([
          API.get(`/courses/${courseId}`),
          API.get(`/progress/course/${courseId}`)
        ])
        setCourse(courseRes.data)
        setProgress(progressRes.data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [courseId])

  const isLessonCompleted = (lessonId) =>
    progress.some(p => p.lessonId === lessonId && p.isCompleted)

  const getCurrentLesson = () => {
    if (!course?.modules || course.modules.length === 0) return null
    const module = course.modules[currentModuleIndex]
    if (!module?.lessons || module.lessons.length === 0) return null
    return module.lessons[currentLessonIndex]
  }

  const goToNextLesson = () => {
    if (!course?.modules) return

    let nextModule = currentModuleIndex
    let nextLesson = currentLessonIndex + 1

    // Si ce n'est pas la dernière leçon, naviguer à la suivante
    if (nextLesson < course.modules[nextModule].lessons.length) {
      setCurrentLessonIndex(nextLesson)
    } else if (nextModule < course.modules.length - 1) {
      // Si c'est la dernière leçon du module, aller au premier lesson du prochain module
      nextModule++
      nextLesson = 0
      setCurrentModuleIndex(nextModule)
      setCurrentLessonIndex(0)
    }
  }

  const goToPreviousLesson = () => {
    if (currentLessonIndex > 0) {
      setCurrentLessonIndex(currentLessonIndex - 1)
    } else if (currentModuleIndex > 0) {
      const prevModule = currentModuleIndex - 1
      const prevLessonIndex = course.modules[prevModule].lessons.length - 1
      setCurrentModuleIndex(prevModule)
      setCurrentLessonIndex(prevLessonIndex)
    }
  }

  const handleMarkComplete = async () => {
    const lesson = getCurrentLesson()
    if (!lesson) return

    setMarking(true)
    try {
      await API.post(`/progress/mark-complete/${lesson.id}`)
      const resProgress = await API.get(`/progress/course/${courseId}`)
      setProgress(resProgress.data)

      // Vérifier si c'est la dernière leçon du module
      const isLastLessonInModule = currentLessonIndex === course.modules[currentModuleIndex].lessons.length - 1
      if (isLastLessonInModule && currentModuleIndex < course.modules.length - 1) {
        setShowQuizMessage(true)
      }
    } catch (err) {
      console.error(err)
    } finally {
      setMarking(false)
    }
  }

  const handleCompleteModule = () => {
    // Naviguer vers le quiz du module
    navigate(`/learn/${courseId}/quiz?module=${currentModuleIndex}`)
  }

  const handleCancelEnrollment = async () => {
    if (!confirm('Êtes-vous sûr de vouloir annuler votre inscription ?')) return

    try {
      // Récupérer l'ID d'inscription depuis /enrollments/me
      const enrollmentsRes = await API.get('/enrollments/me')
      const enrollment = enrollmentsRes.data.find(e => e.courseId === parseInt(courseId))
      
      if (enrollment) {
        await API.post(`/enrollments/${enrollment.id}/cancel`)
        navigate('/dashboard')
      }
    } catch (err) {
      console.error(err)
    }
  }

  const totalLessons = course?.modules?.reduce((acc, m) => acc + (m.lessons?.length || 0), 0) || 0
  const completedLessons = progress.filter(p => p.isCompleted).length
  const progressPct = totalLessons > 0 ? Math.round((completedLessons / totalLessons) * 100) : 0

  const currentLesson = getCurrentLesson()
  const currentModule = course?.modules?.[currentModuleIndex]
  const isFirstLesson = currentModuleIndex === 0 && currentLessonIndex === 0
  const isLastLesson = currentModuleIndex === course?.modules?.length - 1 && 
                       currentLessonIndex === course?.modules[currentModuleIndex]?.lessons?.length - 1
  const lessonCompleted = currentLesson && isLessonCompleted(currentLesson.id)

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

        {/* En-tête */}
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

        {/* Affichage de la leçon actuelle */}
        {currentLesson && currentModule && (
          <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
            
            {/* Contenu principal - Leçon */}
            <div className="lg:col-span-3">
              <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
                
                {/* En-tête de la leçon */}
                <div className="px-6 py-4 border-b border-gray-100 bg-gray-50">
                  <div className="flex items-center justify-between mb-2">
                    <div>
                      <p className="text-sm text-gray-500">Module {currentModuleIndex + 1} - Leçon {currentLessonIndex + 1}</p>
                      <h2 className="text-xl font-bold text-gray-800">{currentLesson.title}</h2>
                    </div>
                    <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                      lessonCompleted ? 'bg-green-50 text-green-600' : 'bg-blue-50 text-blue-600'
                    }`}>
                      {currentLesson.type}
                    </span>
                  </div>
                  {currentLesson.duration > 0 && (
                    <p className="text-xs text-gray-400">⏱ {currentLesson.duration} minutes</p>
                  )}
                </div>

                {/* Contenu de la leçon */}
                <div className="px-6 py-8">
                  {currentLesson.type === 'VIDEO' ? (
                    <div className="mb-6">
                      {currentLesson.videoUrl ? (
                        <iframe
                          width="100%"
                          height="400"
                          src={currentLesson.videoUrl}
                          title={currentLesson.title}
                          frameBorder="0"
                          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                          allowFullScreen
                          className="rounded-lg mb-6"
                        />
                      ) : (
                        <div className="w-full h-96 bg-gray-200 rounded-lg flex items-center justify-center mb-6">
                          <p className="text-gray-500">Vidéo non disponible</p>
                        </div>
                      )}
                    </div>
                  ) : currentLesson.type === 'PDF' ? (
                    <div className="mb-6">
                      {currentLesson.pdfUrl ? (
                        <iframe
                          src={currentLesson.pdfUrl}
                          width="100%"
                          height="600"
                          className="rounded-lg mb-6"
                        />
                      ) : (
                        <div className="w-full h-96 bg-gray-200 rounded-lg flex items-center justify-center mb-6">
                          <p className="text-gray-500">PDF non disponible</p>
                        </div>
                      )}
                    </div>
                  ) : (
                    <div className="mb-6 prose prose-sm max-w-none">
                      <p className="text-gray-700 whitespace-pre-wrap">{currentLesson.content}</p>
                    </div>
                  )}

                  {/* Bouton marquer comme complétée */}
                  {!lessonCompleted && (
                    <button
                      onClick={handleMarkComplete}
                      disabled={marking}
                      className="w-full bg-green-600 text-white py-3 rounded-lg font-medium hover:bg-green-700 transition disabled:opacity-50 mb-6"
                    >
                      {marking ? 'Enregistrement...' : '✓ Marquer comme complétée'}
                    </button>
                  )}
                  {lessonCompleted && (
                    <div className="w-full bg-green-50 text-green-700 py-3 rounded-lg font-medium text-center mb-6">
                      ✓ Leçon complétée
                    </div>
                  )}
                </div>

                {/* Navigation entre les leçons */}
                <div className="px-6 py-6 bg-gray-50 border-t border-gray-100 flex gap-3">
                  <button
                    onClick={goToPreviousLesson}
                    disabled={isFirstLesson}
                    className="flex-1 border border-gray-300 text-gray-700 py-2.5 rounded-lg font-medium hover:bg-gray-100 transition disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    ← Leçon précédente
                  </button>
                  
                  {isLastLesson ? (
                    <button
                      onClick={() => navigate('/dashboard')}
                      className="flex-1 bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 transition"
                    >
                      Retour au dashboard
                    </button>
                  ) : (
                    <button
                      onClick={goToNextLesson}
                      disabled={isLastLesson}
                      className="flex-1 bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 transition disabled:opacity-50"
                    >
                      Leçon suivante →
                    </button>
                  )}
                </div>
              </div>
            </div>

            {/* Sidebar - Plan du cours */}
            <div className="lg:col-span-1">
              <div className="bg-white rounded-xl border border-gray-100 p-4 sticky top-6 max-h-96 overflow-y-auto">
                <h3 className="font-semibold text-gray-800 mb-4">Plan du cours</h3>
                
                <div className="space-y-2">
                  {course.modules?.map((module, mIdx) => (
                    <div key={module.id} className="space-y-1">
                      <p className={`text-xs font-semibold px-2 py-1 rounded ${
                        mIdx === currentModuleIndex ? 'bg-blue-50 text-blue-600' : 'text-gray-600'
                      }`}>
                        Module {mIdx + 1}
                      </p>
                      <div className="space-y-0.5">
                        {module.lessons?.map((lesson, lIdx) => {
                          const isCompleted = isLessonCompleted(lesson.id)
                          const isActive = mIdx === currentModuleIndex && lIdx === currentLessonIndex
                          return (
                            <button
                              key={lesson.id}
                              onClick={() => {
                                setCurrentModuleIndex(mIdx)
                                setCurrentLessonIndex(lIdx)
                              }}
                              className={`w-full text-left text-xs px-3 py-1.5 rounded transition ${
                                isActive ? 'bg-blue-100 text-blue-600 font-medium' :
                                isCompleted ? 'text-green-600 line-through' :
                                'text-gray-600 hover:bg-gray-50'
                              }`}
                            >
                              {isCompleted ? '✔ ' : '○ '} {lIdx + 1}. {lesson.title}
                            </button>
                          )
                        })}
                      </div>
                    </div>
                  ))}
                </div>

                {/* Bouton pour annuler l'inscription */}
                <button
                  onClick={handleCancelEnrollment}
                  className="w-full bg-red-50 text-red-600 py-2 rounded-lg text-xs font-medium hover:bg-red-100 transition mt-6"
                >
                  Annuler l'inscription
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Modal pour quiz */}
        {showQuizMessage && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-xl p-8 max-w-md">
              <h3 className="text-xl font-bold text-gray-800 mb-2">Fin du module</h3>
              <p className="text-gray-600 mb-6">
                Vous avez complété toutes les leçons de ce module. Validez le quiz du module avant de continuer.
              </p>
              <div className="flex gap-3">
                <button
                  onClick={() => setShowQuizMessage(false)}
                  className="flex-1 border border-gray-300 text-gray-700 py-2.5 rounded-lg font-medium hover:bg-gray-50 transition"
                >
                  Continuer plus tard
                </button>
                <button
                  onClick={handleCompleteModule}
                  className="flex-1 bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 transition"
                >
                  Faire le quiz
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}