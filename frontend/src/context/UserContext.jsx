// src/context/UserContext.jsx
import { createContext, useState } from 'react';

export const UserContext = createContext();

export const UserProvider = ({ children }) => {
  // On déplace la logique de state et de localStorage ici
  const [cartProducts, setCartProducts] = useState(JSON.parse(localStorage.getItem("CartProducts")) || []);
  const [userConnecte, setUserConnecte] = useState(JSON.parse(localStorage.getItem("userConnecte")) || {});

  // On centralise la fonction de déconnexion ici ! (Best practice)
  const logout = () => {
    localStorage.clear();
    setCartProducts([]);
    setUserConnecte({});
  };

  return (
    <UserContext.Provider value={{ cartProducts, setCartProducts, userConnecte, setUserConnecte, logout }}>
      {children}
    </UserContext.Provider>
  );
};