// src/components/Sidebar.jsx

import { Link } from "react-router-dom";
import { useContext } from "react";
import { UserContext } from "../context/UserContext"; // Assure-toi que le chemin est correct

// Imports des icônes
import { FaUser } from "react-icons/fa6";
import { MdOutlineProductionQuantityLimits } from "react-icons/md";
import { FaRegRectangleList } from "react-icons/fa6";
import { FaBagShopping } from "react-icons/fa6";
import { LuShoppingCart } from "react-icons/lu";
import { IoLogOutOutline } from "react-icons/io5";
import { RiChatAiFill } from "react-icons/ri";

function Sidebar() {
  const { userConnecte, logout } = useContext(UserContext);

  // 1. Définition des liens de base (pour tous les utilisateurs connectés)
  const menuItems = [
    { path: "/Orders", label: "Orders", icon: <FaRegRectangleList className="mr-4 mt-1" /> },
    { path: "/Shop", label: "Shop", icon: <FaBagShopping className="mr-4 mt-1" /> },
    { path: "/Chat", label: "Chat AI", icon: <RiChatAiFill className="mr-4 mt-1" /> },
  ];

  // 2. Ajout dynamique des liens Admin en haut de la liste si l'utilisateur est Admin
  if (userConnecte?.role === "Admin") {
    menuItems.unshift(
      { path: "/Users", label: "Users", icon: <FaUser className="mr-4 mt-1" /> },
      { path: "/Products", label: "Products", icon: <MdOutlineProductionQuantityLimits className="mr-4 mt-1" /> }
    );
  }

  return (
    <div className="h-full md:h-screen bg-black w-full md:w-64 text-white p-4 flex flex-col">
      
      {/* En-tête : Profil et Panier */}
      <div className="flex items-center justify-between mb-8">
        <h1 className="font-bold text-xl truncate">
          {userConnecte?.fullName || "Utilisateur"}
        </h1>
        <Link 
          to="/Cart" 
          className="bg-yellow-500 rounded p-3 text-black hover:bg-yellow-400 transition-colors"
          title="Voir le panier"
        >
          <LuShoppingCart />
        </Link>
      </div>

      <h2 className="font-bold text-lg mb-8 uppercase tracking-wider text-gray-400">
        {userConnecte?.role} Dashboard
      </h2>

      {/* Navigation */}
      <nav className="flex-1">
        <ul>
          {/* 3. Génération dynamique des liens */}
          {menuItems.map((item) => (
            <li key={item.path}>
              <Link
                to={item.path}
                className="flex items-center border border-white/20 rounded-xl p-4 mt-4 text-lg transition-all hover:bg-white hover:text-black"
              >
                {item.icon}
                <span>{item.label}</span>
              </Link>
            </li>
          ))}

          {/* Bouton Déconnexion (séparé pour le style) */}
          <li>
            <button
              onClick={logout} // Appelle directement la fonction logout du Context
              className="cursor-pointer w-full flex items-center border border-red-500/50 text-red-400 rounded-xl p-4 mt-8 text-lg transition-all hover:bg-red-500 hover:text-white hover:border-red-500"
            >
              <IoLogOutOutline className="mr-4 mt-1 text-xl" />
              Logout
            </button>
          </li>
        </ul>
      </nav>
    </div>
  );
}

export default Sidebar;