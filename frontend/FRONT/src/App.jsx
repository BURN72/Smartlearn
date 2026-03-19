import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'

import Login from './pages/auth/Login'
import Register from './pages/auth/Register'
import CourseCatalog from './pages/courses/CourseCatalog'
import CourseDetail from './pages/courses/CourseDetail'
import StudentDashboard from './pages/dashboard/StudentDashboard'
import InstructorDashboard from './pages/dashboard/InstructorDashboard'
import AdminDashboard from './pages/admin/AdminDashboard'
import AdminCategories from './pages/admin/AdminCategories'
import AdminCourses from './pages/admin/AdminCourses'
import AdminUsers from './pages/admin/AdminUsers'
import Home from './pages/Home' 

const PrivateRoute = ({ children, roles }) => {
  const { user, loading } = useAuth()
  if (loading) return (
    <div className="min-h-screen flex items-center justify-center text-gray-400">
      Chargement...
    </div>
  )
  if (!user) return <Navigate to="/login" />
  if (roles && !roles.includes(user.role)) return <Navigate to="/dashboard" />
  return children
}

function AppRoutes() {
  const { user } = useAuth()

  return (
    <Routes>
      {/* Redirection accueil */}
      <Route path="/" element={<Home />} />

      {/* Auth */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* Public */}
      <Route path="/courses" element={<CourseCatalog />} />
      <Route path="/courses/:id" element={<CourseDetail />} />

      {/* Étudiant */}
      <Route path="/dashboard" element={
        <PrivateRoute roles={['ROLE_STUDENT']}>
          <StudentDashboard />
        </PrivateRoute>
      } />

      {/* Enseignant */}
      <Route path="/instructor" element={
        <PrivateRoute roles={['ROLE_INSTRUCTOR']}>
          <InstructorDashboard />
        </PrivateRoute>
      } />

      {/* Admin */}
      <Route path="/admin" element={
        <PrivateRoute roles={['ROLE_ADMIN']}>
          <AdminDashboard />
        </PrivateRoute>
      } />
      <Route path="/admin/categories" element={
        <PrivateRoute roles={['ROLE_ADMIN']}>
          <AdminCategories />
        </PrivateRoute>
      } />
      <Route path="/admin/courses" element={
        <PrivateRoute roles={['ROLE_ADMIN']}>
          <AdminCourses />
        </PrivateRoute>
      } />
      <Route path="/admin/users" element={
        <PrivateRoute roles={['ROLE_ADMIN']}>
          <AdminUsers />
        </PrivateRoute>
      } />

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </AuthProvider>
  )
}