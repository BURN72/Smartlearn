import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Login from './pages/auth/Login'
import Register from './pages/auth/Register'
import CourseCatalog from './pages/courses/CourseCatalog'
import StudentDashboard from './pages/dashboard/StudentDashboard'
import AdminDashboard from './pages/admin/AdminDashboard'
import AdminCategories from './pages/admin/AdminCategories'
import AdminCourses from './pages/admin/AdminCourses' 
import InstructorDashboard from './pages/dashboard/InstructorDashboard'


const PrivateRoute = ({ children, roles }) => {
  const { user, loading } = useAuth()
  if (loading) return <div className="min-h-screen flex items-center justify-center text-gray-400">Chargement...</div>
  if (!user) return <Navigate to="/login" />
  if (roles && !roles.includes(user.role)) return <Navigate to="/dashboard" />
  return children
}

function AppRoutes() {
  const { user } = useAuth()

  return (
    <Routes>
      <Route path="/" element={<Navigate to={user ? '/dashboard' : '/courses'} />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/courses" element={<CourseCatalog />} />

      <Route path="/dashboard" element={
        <PrivateRoute>
          <StudentDashboard />
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

      <Route path="/admin" element={
        <PrivateRoute roles={['ROLE_ADMIN']}>
          <AdminDashboard />
        </PrivateRoute>
      } />

      <Route path="/instructor" element={
        <PrivateRoute roles={['ROLE_INSTRUCTOR']}>
          <InstructorDashboard />
        </PrivateRoute>
      } />

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