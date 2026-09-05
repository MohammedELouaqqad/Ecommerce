// src/App.jsx
import './App.css'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { UserProvider } from './context/UserContext' // On importe le Provider
import ProtectedRoute from './components/ProtectedRoute' // On importe le gardien

import Products from './pages/Products'
import Users from './pages/Users'
import Shop from './pages/Shop'
import Orders from './pages/Orders'
import Login from './pages/Login'
import Cart from './pages/Cart'
import SignUp from './pages/SignUp'
import Chat from './pages/Chat'

function App() {
  return (
    <UserProvider> {/* Le Provider englobe toute l'application */}
      <Router>
        <Routes>
          {/* Routes publiques */}
          <Route path='/' element={<Login />} />
          <Route path='/register' element={<SignUp />} />

          {/* Routes privées (utilisateur connecté) */}
          <Route path='/Shop' element={<ProtectedRoute><Shop /></ProtectedRoute>} />
          <Route path='/Orders' element={<ProtectedRoute><Orders /></ProtectedRoute>} />
          <Route path='/Cart' element={<ProtectedRoute><Cart /></ProtectedRoute>} />
          <Route path='/Chat' element={<ProtectedRoute><Chat /></ProtectedRoute>} />

          {/* Routes Admin (nécessitent le rôle "Admin") */}
          <Route path='/Users' element={<ProtectedRoute requiredRole="Admin"><Users /></ProtectedRoute>} />
          <Route path='/Products' element={<ProtectedRoute requiredRole="Admin"><Products /></ProtectedRoute>} />
        </Routes>
      </Router>
    </UserProvider>
  )
}

export default App;