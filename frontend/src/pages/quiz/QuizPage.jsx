import { useState, useEffect } from 'react'
import { useParams, useNavigate, useSearchParams } from 'react-router-dom'
import API from '../../services/api'
import Navbar from '../../components/layout/Navbar'

export default function QuizPage() {
  const { courseId } = useParams()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const moduleIndex = parseInt(searchParams.get('module') || '0')
  
  const [course, setCourse] = useState(null)
  const [quizzes, setQuizzes] = useState([])
  const [selectedQuiz, setSelectedQuiz] = useState(null)
  const [attempt, setAttempt] = useState(null)
  const [answers, setAnswers] = useState({})
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    const loadData = async () => {
      try {
        const [courseRes, quizzesRes] = await Promise.all([
          API.get(`/courses/${courseId}`),
          API.get(`/quizzes/course/${courseId}`)
        ])
        setCourse(courseRes.data)
        setQuizzes(quizzesRes.data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [courseId])

  const moduleQuizzes = quizzes.filter(q => q.moduleId === course?.modules?.[moduleIndex]?.id)

  const handleStartQuiz = async (quizId) => {
    try {
      const res = await API.post(`/quiz-attempts/start/${quizId}`)
      setAttempt(res.data)
      const quizRes = await API.get(`/quizzes/${quizId}`)
      setSelectedQuiz(quizRes.data)
      setAnswers({})
      setResult(null)
    } catch (err) {
      console.error(err)
    }
  }

  const handleSubmit = async () => {
    setSubmitting(true)
    try {
      const studentAnswers = Object.entries(answers).map(([questionId, answer]) => ({
        questionId: parseInt(questionId),
        answer
      }))
      const res = await API.post(`/quiz-attempts/${attempt.id}/submit`, {
        answers: studentAnswers
      })
      setResult(res.data)
      setAttempt(null)
    } catch (err) {
      console.error(err)
    } finally { setSubmitting(false) }
  }

  const handleGoBack = () => {
    navigate(`/learn/${courseId}`)
  }

  const handleCompleteAndContinue = () => {
    // Marquer le module comme complété et aller à la leçon suivante
    navigate(`/learn/${courseId}`)
  }

  if (loading) return <div className="min-h-screen bg-gray-50"><Navbar /><div className="text-center py-20 text-gray-400">Chargement...</div></div>

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-3xl mx-auto px-4 py-10">

        {/* Résultat */}
        {result && (
          <div className={`rounded-xl p-8 text-center mb-8 ${result.passed ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'}`}>
            <p className="text-5xl font-bold mb-3">{result.passed ? '🎉' : '😔'}</p>
            <h2 className="text-2xl font-bold mb-2">{result.passed ? 'Quiz réussi !' : 'Quiz échoué'}</h2>
            <p className="text-lg text-gray-600 mb-4">Score : <span className="font-bold">{result.score}%</span></p>
            {result.passed ? (
              <button onClick={handleCompleteAndContinue}
                className="bg-green-600 text-white px-6 py-2.5 rounded-lg font-medium hover:bg-green-700">
                Continuer le cours →
              </button>
            ) : result.attemptsRemaining > 0 ? (
              <button onClick={() => { setResult(null); setSelectedQuiz(null) }}
                className="bg-blue-600 text-white px-6 py-2.5 rounded-lg font-medium hover:bg-blue-700">
                Réessayer ({result.attemptsRemaining} tentative{result.attemptsRemaining > 1 ? 's' : ''} restante{result.attemptsRemaining > 1 ? 's' : ''})
              </button>
            ) : (
              <button onClick={handleGoBack}
                className="bg-blue-600 text-white px-6 py-2.5 rounded-lg font-medium hover:bg-blue-700">
                Retour au cours
              </button>
            )}
          </div>
        )}

        {/* Quiz en cours */}
        {attempt && selectedQuiz && !result && (
          <div>
            <div className="flex items-center justify-between mb-6">
              <h1 className="text-2xl font-bold text-gray-800">{selectedQuiz.title}</h1>
              <span className="text-sm text-gray-500">
                {Object.keys(answers).length} / {selectedQuiz.questions?.length} réponses
              </span>
            </div>

            <div className="space-y-6">
              {selectedQuiz.questions?.map((question, i) => (
                <div key={question.id} className="bg-white rounded-xl border border-gray-100 p-6">
                  <p className="font-semibold text-gray-800 mb-4">
                    {i + 1}. {question.text}
                    <span className="ml-2 text-sm text-gray-400">({question.points} pts)</span>
                  </p>

                  {question.type === 'QCM' && question.choices?.map(choice => (
                    <label key={choice.id} className="flex items-center gap-3 mb-2 cursor-pointer">
                      <input type="radio" name={`q${question.id}`} value={choice.text}
                        checked={answers[question.id] === choice.text}
                        onChange={() => setAnswers({ ...answers, [question.id]: choice.text })}
                        className="text-blue-600"/>
                      <span className="text-gray-700">{choice.text}</span>
                    </label>
                  ))}

                  {question.type === 'VRAI_FAUX' && (
                    <div className="flex gap-4">
                      {['Vrai', 'Faux'].map(opt => (
                        <label key={opt} className="flex items-center gap-2 cursor-pointer">
                          <input type="radio" name={`q${question.id}`} value={opt}
                            checked={answers[question.id] === opt}
                            onChange={() => setAnswers({ ...answers, [question.id]: opt })}
                            className="text-blue-600"/>
                          <span>{opt}</span>
                        </label>
                      ))}
                    </div>
                  )}

                  {question.type === 'TEXTE_LIBRE' && (
                    <textarea value={answers[question.id] || ''} rows={3}
                      onChange={e => setAnswers({ ...answers, [question.id]: e.target.value })}
                      className="w-full border border-gray-300 rounded-lg px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500"
                      placeholder="Votre réponse..."/>
                  )}
                </div>
              ))}
            </div>

            <div className="flex gap-4 mt-8">
              <button onClick={handleSubmit} disabled={submitting}
                className="bg-blue-600 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-700 disabled:opacity-50">
                {submitting ? 'Correction en cours...' : 'Soumettre le quiz'}
              </button>
              <button onClick={() => { setAttempt(null); setSelectedQuiz(null) }}
                className="border border-gray-300 text-gray-600 px-6 py-3 rounded-lg font-medium hover:bg-gray-50">
                Abandonner
              </button>
            </div>
          </div>
        )}

        {/* Liste des quiz */}
        {!attempt && !result && (
          <div>
            <div className="mb-6">
              <h1 className="text-2xl font-bold text-gray-800 mb-2">
                Quiz du Module {moduleIndex + 1}
              </h1>
              {course?.modules?.[moduleIndex] && (
                <p className="text-gray-600">{course.modules[moduleIndex].title}</p>
              )}
            </div>

            {moduleQuizzes.length === 0 ? (
              <div className="text-center py-16 bg-white rounded-xl border border-gray-100">
                <p className="text-gray-400 mb-4">Aucun quiz disponible pour ce module</p>
                <button onClick={handleGoBack}
                  className="bg-blue-600 text-white px-6 py-2.5 rounded-lg font-medium hover:bg-blue-700">
                  Retour au cours
                </button>
              </div>
            ) : (
              <div className="space-y-4">
                {moduleQuizzes.map(quiz => (
                  <div key={quiz.id} className="bg-white rounded-xl border border-gray-100 p-6">
                    <div className="flex items-start justify-between">
                      <div>
                        <h3 className="font-semibold text-gray-800 text-lg mb-1">{quiz.title}</h3>
                        <div className="flex gap-4 text-sm text-gray-400">
                          <span>Seuil de réussite : {quiz.passMark}%</span>
                          <span>Tentatives max : {quiz.maxAttempts}</span>
                          {quiz.timeLimitMinutes > 0 && <span>Durée : {quiz.timeLimitMinutes} min</span>}
                        </div>
                      </div>
                      <button onClick={() => handleStartQuiz(quiz.id)}
                        className="bg-blue-600 text-white px-5 py-2.5 rounded-lg font-medium hover:bg-blue-700 transition">
                        Commencer
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}