import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import API from '../../services/api'
import Navbar from '../../components/layout/Navbar'
import { CourseStatus, CourseStatusColors } from '../../constants/courseStatus'

export default function InstructorCourseDetail() {
  const { id } = useParams()
  const [course, setCourse] = useState(null)
  const [loading, setLoading] = useState(true)
  const [showModuleForm, setShowModuleForm] = useState(false)
  const [showLessonForm, setShowLessonForm] = useState(null)
  const [showQuizForm, setShowQuizForm] = useState(null)
  const [moduleForm, setModuleForm] = useState({ title: '', description: '', orderIndex: 1 })
  const [lessonForm, setLessonForm] = useState({
    title: '', type: 'TEXT', content: '',
    videoUrl: '', duration: 0, isFree: false
  })
  const [quizForm, setQuizForm] = useState({
    title: '', description: '', passMark: 70, attempts: 3, timeLimit: 0
  })
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState('')

  useEffect(() => { fetchCourse() }, [id])

  const fetchCourse = () => {
    API.get(`/courses/${id}`)
      .then(res => setCourse(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false))
  }

  const handleAddModule = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const nextOrder = (course.modules?.length || 0) + 1
      await API.post('/modules', { ...moduleForm, orderIndex: nextOrder, courseId: parseInt(id) }) 
      setModuleForm({ title: '', description: '', orderIndex: nextOrder + 1 })
      setShowModuleForm(false)
      fetchCourse()
    } catch (err) {
      setMessage(err.response?.data?.message || 'Erreur')
    } finally { setSaving(false) }
  }

  const handleAddLesson = async (e, moduleId) => {
    e.preventDefault()
    setSaving(true)
    try {
      const module = course.modules.find(m => m.id === moduleId)
      const nextOrder = (module?.lessons?.length || 0) + 1
      await API.post('/lessons', { ...lessonForm, moduleId, orderIndex: nextOrder })
      setMessage('Leçon ajoutée !')
      setLessonForm({ title: '', type: 'TEXT', content: '', videoUrl: '', duration: 0, isFree: false })
      setShowLessonForm(null)
      fetchCourse()
    } catch (err) {
      setMessage(err.response?.data?.message || 'Erreur')
    } finally { setSaving(false) }
  }

  const handleDeleteModule = async (moduleId) => {
    if (!confirm('Supprimer ce module et toutes ses leçons ?')) return
    try {
      await API.delete(`/modules/${moduleId}`)
      fetchCourse()
    } catch (err) { console.error(err) }
  }

  const handleDeleteLesson = async (lessonId) => {
    if (!confirm('Supprimer cette leçon ?')) return
    try {
      await API.delete(`/lessons/${lessonId}`)
      fetchCourse()
    } catch (err) { console.error(err) }
  }

  const handleAddQuiz = async (e, moduleId) => {
    e.preventDefault()
    setSaving(true)
    try {
      await API.post('/quizzes', { 
        ...quizForm, 
        moduleId: parseInt(moduleId)
      })
      setMessage('Quiz créé avec succès !')
      setQuizForm({ title: '', description: '', passMark: 70, attempts: 3, timeLimit: 0 })
      setShowQuizForm(null)
      fetchCourse()
    } catch (err) {
      setMessage(err.response?.data?.message || 'Erreur lors de la création du quiz')
    } finally { setSaving(false) }
  }

  if (loading) return <div className="min-h-screen bg-gray-50"><Navbar /><div className="text-center py-20 text-gray-400">Chargement...</div></div>
  if (!course) return <div className="min-h-screen bg-gray-50"><Navbar /><div className="text-center py-20 text-gray-400">Cours introuvable</div></div>

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-5xl mx-auto px-4 py-10">

        <div className="flex items-center gap-3 mb-8">
          <Link to="/instructor" className="text-blue-600 hover:underline text-sm">← Mes cours</Link>
          <span className="text-gray-300">/</span>
          <h1 className="text-2xl font-bold text-gray-800">{course.title}</h1>
          <span className={`text-xs px-2 py-1 rounded-full font-medium ${
          course.status === CourseStatus.PUBLIE ? 'bg-green-50 text-green-600' :
            course.status === CourseStatus.EN_REVISION ? 'bg-yellow-50 text-yellow-600' :
            'bg-gray-100 text-gray-600'
          }`}>{course.status}</span>
        </div>

        {message && (
          <div className="bg-blue-50 text-blue-600 px-4 py-3 rounded-lg mb-6 text-sm">{message}</div>
        )}

        <div className="flex items-center justify-between mb-6">
          <h2 className="text-lg font-semibold text-gray-700">
            Modules ({course.modules?.length || 0})
          </h2>
          <button onClick={() => setShowModuleForm(!showModuleForm)}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition">
            + Ajouter un module
          </button>
        </div>

        {showModuleForm && (
          <form onSubmit={handleAddModule} className="bg-white rounded-xl border border-gray-100 p-5 mb-6 space-y-3">
            <h3 className="font-medium text-gray-700">Nouveau module</h3>
            <input type="text" required placeholder="Titre du module" value={moduleForm.title}
              onChange={e => setModuleForm({ ...moduleForm, title: e.target.value })}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"/>
            <textarea placeholder="Description (optionnel)" value={moduleForm.description}
              onChange={e => setModuleForm({ ...moduleForm, description: e.target.value })}
              className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500" rows={2}/>
            <div className="flex gap-2">
              <button type="submit" disabled={saving}
                className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 disabled:opacity-50">
                {saving ? '...' : 'Ajouter'}
              </button>
              <button type="button" onClick={() => setShowModuleForm(false)}
                className="border border-gray-300 text-gray-600 px-4 py-2 rounded-lg text-sm font-medium hover:bg-gray-50">
                Annuler
              </button>
            </div>
          </form>
        )}

        <div className="space-y-4">
          {course.modules?.map((module, i) => (
            <div key={module.id} className="bg-white rounded-xl border border-gray-100 overflow-hidden">
              <div className="flex items-center justify-between px-5 py-4 border-b border-gray-50">
                <h3 className="font-semibold text-gray-800">Module {i + 1} : {module.title}</h3>
                <div className="flex gap-2">
                  <button onClick={() => setShowLessonForm(showLessonForm === module.id ? null : module.id)}
                    className="bg-green-50 text-green-600 px-3 py-1.5 rounded-lg text-sm font-medium hover:bg-green-100">
                    + Leçon
                  </button>
                  <button onClick={() => setShowQuizForm(showQuizForm === module.id ? null : module.id)}
                    className="bg-purple-50 text-purple-600 px-3 py-1.5 rounded-lg text-sm font-medium hover:bg-purple-100">
                    + Quiz
                  </button>
                  <button onClick={() => handleDeleteModule(module.id)}
                    className="bg-red-50 text-red-500 px-3 py-1.5 rounded-lg text-sm font-medium hover:bg-red-100">
                    Supprimer
                  </button>
                </div>
              </div>

              {showLessonForm === module.id && (
                <form onSubmit={(e) => handleAddLesson(e, module.id)}
                  className="px-5 py-4 bg-gray-50 border-b border-gray-100 space-y-3">
                  <h4 className="font-medium text-gray-700 text-sm">Nouvelle leçon</h4>
                  <div className="grid grid-cols-2 gap-3">
                    <input type="text" required placeholder="Titre *" value={lessonForm.title}
                      onChange={e => setLessonForm({ ...lessonForm, title: e.target.value })}
                      className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                    <select value={lessonForm.type}
                      onChange={e => setLessonForm({ ...lessonForm, type: e.target.value })}
                      className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                      <option value="TEXT">Texte</option>
                      <option value="VIDEO">Vidéo</option>
                      <option value="PDF">PDF</option>
                    </select>
                    {lessonForm.type === 'VIDEO' && (
                      <input type="text" placeholder="URL vidéo" value={lessonForm.videoUrl}
                        onChange={e => setLessonForm({ ...lessonForm, videoUrl: e.target.value })}
                        className="col-span-2 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                    )}
                    {lessonForm.type === 'TEXT' && (
                      <textarea placeholder="Contenu" value={lessonForm.content} rows={3}
                        onChange={e => setLessonForm({ ...lessonForm, content: e.target.value })}
                        className="col-span-2 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                    )}
                    <input type="number" placeholder="Durée (min)" value={lessonForm.duration}
                      onChange={e => setLessonForm({ ...lessonForm, duration: parseInt(e.target.value) || 0 })}
                      className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                    <label className="flex items-center gap-2 text-sm text-gray-600">
                      <input type="checkbox" checked={lessonForm.isFree}
                        onChange={e => setLessonForm({ ...lessonForm, isFree: e.target.checked })}/>
                      Leçon gratuite
                    </label>
                  </div>
                  <div className="flex gap-2">
                    <button type="submit" disabled={saving}
                      className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-green-700 disabled:opacity-50">
                      {saving ? '...' : 'Ajouter la leçon'}
                    </button>
                    <button type="button" onClick={() => setShowLessonForm(null)}
                      className="border border-gray-300 text-gray-600 px-4 py-2 rounded-lg text-sm font-medium hover:bg-gray-50">
                      Annuler
                    </button>
                  </div>
                </form>
              )}

              {showQuizForm === module.id && (
                <form onSubmit={(e) => handleAddQuiz(e, module.id)}
                  className="px-5 py-4 bg-gray-50 border-b border-gray-100 space-y-3">
                  <h4 className="font-medium text-gray-700 text-sm">Nouveau quiz</h4>
                  <div className="grid grid-cols-2 gap-3">
                    <input type="text" required placeholder="Titre du quiz *" value={quizForm.title}
                      onChange={e => setQuizForm({ ...quizForm, title: e.target.value })}
                      className="col-span-2 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                    <textarea placeholder="Description (optionnel)" value={quizForm.description}
                      onChange={e => setQuizForm({ ...quizForm, description: e.target.value })}
                      className="col-span-2 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" rows={2}/>
                    <input type="number" placeholder="Seuil de réussite (%)" value={quizForm.passMark}
                      onChange={e => setQuizForm({ ...quizForm, passMark: parseInt(e.target.value) || 70 })}
                      min="0" max="100"
                      className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                    <input type="number" placeholder="Tentatives max" value={quizForm.attempts}
                      onChange={e => setQuizForm({ ...quizForm, attempts: parseInt(e.target.value) || 3 })}
                      min="1"
                      className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                    <input type="number" placeholder="Durée limite (min)" value={quizForm.timeLimit}
                      onChange={e => setQuizForm({ ...quizForm, timeLimit: parseInt(e.target.value) || 0 })}
                      min="0"
                      className="col-span-2 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"/>
                  </div>
                  <div className="flex gap-2">
                    <button type="submit" disabled={saving}
                      className="bg-purple-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-purple-700 disabled:opacity-50">
                      {saving ? '...' : 'Créer le quiz'}
                    </button>
                    <button type="button" onClick={() => setShowQuizForm(null)}
                      className="border border-gray-300 text-gray-600 px-4 py-2 rounded-lg text-sm font-medium hover:bg-gray-50">
                      Annuler
                    </button>
                  </div>
                </form>
              )}

              <div className="divide-y divide-gray-50">
                {(!module.lessons || module.lessons.length === 0) && (
                  <p className="px-5 py-3 text-sm text-gray-400 italic">Aucune leçon — cliquez sur "+ Leçon"</p>
                )}
                {module.lessons?.map((lesson, j) => (
                  <div key={lesson.id} className="flex items-center justify-between px-5 py-3">
                    <div className="flex items-center gap-3">
                      <span className="text-gray-400 text-sm">{j + 1}.</span>
                      <span className="text-sm font-medium text-gray-700">{lesson.title}</span>
                      <span className="text-xs bg-gray-100 text-gray-500 px-2 py-0.5 rounded-full">{lesson.type}</span>
                      {lesson.isFree && <span className="text-xs bg-green-50 text-green-600 px-2 py-0.5 rounded-full">Gratuit</span>}
                      {lesson.duration > 0 && <span className="text-xs text-gray-400">{lesson.duration} min</span>}
                    </div>
                    <button onClick={() => handleDeleteLesson(lesson.id)}
                      className="text-red-400 hover:text-red-600 text-sm">Supprimer</button>
                  </div>
                ))}
              </div>
            </div>
          ))}

          {(!course.modules || course.modules.length === 0) && (
            <div className="text-center py-16 bg-white rounded-xl border border-gray-100 text-gray-400">
              Aucun module. Commencez par ajouter un module ci-dessus.
            </div>
          )}
        </div>
      </div>
    </div>
  )
}