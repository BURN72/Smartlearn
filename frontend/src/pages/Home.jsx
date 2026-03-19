import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import API from '../services/api'
import Navbar from '../components/layout/Navbar'

export default function Home() {
  const [courses, setCourses] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      API.get('/courses/published/all'),
      API.get('/categories')
    ]).then(([coursesRes, categoriesRes]) => {
      setCourses(coursesRes.data.slice(0, 6))
      setCategories(categoriesRes.data)
    }).catch(err => console.error(err))
    .finally(() => setLoading(false))
  }, [])

  return (
    <div className="min-h-screen bg-white">
      <Navbar />

      {/* Hero */}
      <section className="bg-gradient-to-br from-blue-600 to-blue-800 text-white py-24 px-4">
        <div className="max-w-4xl mx-auto text-center">
          <h1 className="text-5xl font-bold mb-6">
            Apprenez à votre rythme avec <span className="text-yellow-300">SmartLearn</span>
          </h1>
          <p className="text-xl text-blue-100 mb-10 max-w-2xl mx-auto">
            Des centaines de cours en ligne créés par des experts. Obtenez des certificats reconnus et développez vos compétences dès aujourd'hui.
          </p>
          <div className="flex gap-4 justify-center">
            <Link to="/courses"
              className="bg-white text-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-blue-50 transition text-lg">
              Voir les cours
            </Link>
            <Link to="/register"
              className="bg-yellow-400 text-blue-900 px-8 py-3 rounded-lg font-semibold hover:bg-yellow-300 transition text-lg">
              Commencer gratuitement
            </Link>
          </div>
        </div>
      </section>

      {/* Stats */}
      <section className="bg-blue-50 py-12 px-4">
        <div className="max-w-5xl mx-auto grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          {[
            { value: '500+', label: 'Cours disponibles' },
            { value: '10k+', label: 'Étudiants inscrits' },
            { value: '50+', label: 'Enseignants experts' },
            { value: '95%', label: 'Taux de satisfaction' },
          ].map((stat, i) => (
            <div key={i}>
              <p className="text-3xl font-bold text-blue-600">{stat.value}</p>
              <p className="text-gray-500 mt-1">{stat.label}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Catégories */}
      {categories.length > 0 && (
        <section className="py-16 px-4">
          <div className="max-w-6xl mx-auto">
            <h2 className="text-3xl font-bold text-gray-800 mb-2 text-center">Nos catégories</h2>
            <p className="text-gray-500 text-center mb-10">Explorez nos domaines de formation</p>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {categories.map(cat => (
                <Link key={cat.id} to={`/courses?category=${cat.id}`}
                  className="bg-white border border-gray-100 rounded-xl p-6 text-center hover:shadow-md hover:border-blue-200 transition">
                  <div className="w-12 h-12 bg-blue-50 rounded-full flex items-center justify-center mx-auto mb-3">
                    <span className="text-blue-600 font-bold text-lg">{cat.name[0]}</span>
                  </div>
                  <h3 className="font-semibold text-gray-800">{cat.name}</h3>
                  <p className="text-xs text-gray-400 mt-1">{cat.description}</p>
                </Link>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* Cours en vedette */}
      <section className="py-16 px-4 bg-gray-50">
        <div className="max-w-6xl mx-auto">
          <div className="flex items-center justify-between mb-10">
            <div>
              <h2 className="text-3xl font-bold text-gray-800 mb-2">Cours populaires</h2>
              <p className="text-gray-500">Les formations les plus suivies sur la plateforme</p>
            </div>
            <Link to="/courses" className="text-blue-600 font-medium hover:underline">
              Voir tout →
            </Link>
          </div>

          {loading ? (
            <div className="text-center py-10 text-gray-400">Chargement...</div>
          ) : courses.length === 0 ? (
            <div className="text-center py-10 text-gray-400">Aucun cours disponible pour l'instant</div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {courses.map(course => (
                <Link key={course.id} to={`/courses/${course.id}`}>
                  <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition h-full">
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
                      <div className="flex items-center justify-between">
                        <span className="text-sm text-gray-400">Par {course.instructorName}</span>
                        <span className="font-bold text-blue-600">
                          {!course.price || course.price === 0 ? 'Gratuit' : `${course.price} XAF`}
                        </span>
                      </div>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </div>
      </section>

      {/* CTA */}
      <section className="py-20 px-4 bg-blue-600 text-white text-center">
        <div className="max-w-2xl mx-auto">
          <h2 className="text-3xl font-bold mb-4">Prêt à commencer votre parcours ?</h2>
          <p className="text-blue-100 mb-8">Rejoignez des milliers d'apprenants et transformez votre carrière dès aujourd'hui.</p>
          <div className="flex gap-4 justify-center">
            <Link to="/register"
              className="bg-white text-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-blue-50 transition">
              Créer un compte gratuit
            </Link>
            <Link to="/login"
              className="border border-white text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-700 transition">
              Se connecter
            </Link>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-800 text-gray-400 py-10 px-4">
        <div className="max-w-6xl mx-auto flex flex-col md:flex-row justify-between items-center gap-4">
          <div>
            <h3 className="text-white font-bold text-xl mb-1">SmartLearn</h3>
            <p className="text-sm">Plateforme d'apprentissage en ligne — Université d'Ebolowa</p>
          </div>
          <div className="flex gap-6 text-sm">
            <Link to="/courses" className="hover:text-white transition">Catalogue</Link>
            <Link to="/register" className="hover:text-white transition">S'inscrire</Link>
            <Link to="/login" className="hover:text-white transition">Connexion</Link>
          </div>
          <p className="text-sm">© 2026 SmartLearn. Tous droits réservés.</p>
        </div>
      </footer>
    </div>
  )
}