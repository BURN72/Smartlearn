import axios from 'axios'

const API = axios.create({
  baseURL: 'http://localhost:8080/api',
})

API.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

API.interceptors.response.use(
  (response) => response,
  async (error) => {
    const status = error.response?.status
    const originalRequest = error.config

    // 401 = token expiré → rafraîchir
    if (status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const res = await axios.post(
            'http://localhost:8080/api/auth/refresh',
            { refreshToken }
          )
          localStorage.setItem('accessToken', res.data.accessToken)
          if (res.data.refreshToken) {
            localStorage.setItem('refreshToken', res.data.refreshToken)
          }
          originalRequest.headers.Authorization = `Bearer ${res.data.accessToken}`
          return API(originalRequest)
        } catch {
          localStorage.clear()
          window.location.href = '/login'
        }
      } else {
        localStorage.clear()
        window.location.href = '/login'
      }
    }

    // 403 = pas les droits → ne pas déconnecter, laisser la page gérer
    return Promise.reject(error)
  }
)

export default API