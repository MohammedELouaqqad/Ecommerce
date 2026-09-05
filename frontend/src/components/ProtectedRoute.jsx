// src/components/ProtectedRoute.jsx
import { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { UserContext } from '../context/UserContext';

// "requiredRole" est optionnel. Si on ne le passe pas, il suffit d'être connecté.
const ProtectedRoute = ({ children, requiredRole }) => {
  const { userConnecte } = useContext(UserContext);

  // 1. Si l'utilisateur n'est pas connecté -> Redirection vers Login
  if (!userConnecte || !userConnecte.role) {
    return <Navigate to="/" replace />;
  }

  // 2. Si un rôle est requis (ex: "Admin") et que l'utilisateur ne l'a pas -> Redirection vers Shop
  if (requiredRole && userConnecte.role !== requiredRole) {
    return <Navigate to="/Shop" replace />;
  }

  // 3. Sinon, tout va bien, on affiche la page demandée
  return children;
};

export default ProtectedRoute;