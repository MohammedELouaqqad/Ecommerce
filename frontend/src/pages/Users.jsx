// src/pages/Users.jsx
import { useContext, useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";
import Input from "../components/Input"; // On réutilise notre composant !
import { UserContext } from "../context/UserContext";
import { getAllUsers, registerUser, deleteUserApi } from "../services/userService";

function Users() {
  const [formaData, setFormaData] = useState({ fullName: "", email: "", role: "Customer", password: "" });
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const { userConnecte } = useContext(UserContext);

  // 1. Chargement initial
  useEffect(() => {
    const loadUsers = async () => {
      try {
        const data = await getAllUsers();
        setUsers(data);
      } catch (error) {
        console.log("Failed to fetch users");
      } finally {
        setLoading(false);
      }
    };
    loadUsers();
  }, []);

  // 2. Ajout d'utilisateur
  const handleAddUser = async (e) => {
    e.preventDefault();
    try {
      await registerUser(formaData);
      // On réinitialise le formulaire
      setFormaData({ fullName: "", email: "", role: "Customer", password: "" });
      // On recharge la liste pour voir le nouvel utilisateur
      const updatedUsers = await getAllUsers();
      setUsers(updatedUsers);
    } catch (error) {
      alert("Erreur lors de l'ajout de l'utilisateur.");
    }
  };

  // 3. Suppression sécurisée (Bug corrigé !)
  const handleDelete = async (id) => {
    try {
      await deleteUserApi(id); // 1. On attend que l'API confirme
      setUsers(users.filter(user => user.id !== id)); // 2. On met à jour l'UI
    } catch (error) {
      alert("Erreur lors de la suppression de l'utilisateur.");
    }
  };

  // Handler générique pour les inputs
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormaData(prev => ({ ...prev, [name]: value }));
  };

  if (loading) {
    return (
      <div className="flex w-full min-h-screen bg-background">
        <Sidebar />
        <div className="flex-1 flex justify-center items-center">
          <p className="text-gray-500 text-xl animate-pulse">Loading users...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col md:flex-row w-full min-h-screen bg-background">
      <Sidebar />
      
      <div className="p-4 md:p-10 w-full">
        <h1 className="text-2xl font-bold text-gray-800 mb-8">User Management</h1>
        
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          
          {/* Colonne 1 : Formulaire d'ajout */}
          <div className="lg:col-span-1 bg-white shadow-md rounded-lg p-6 h-fit">
            <h2 className="text-xl font-medium text-gray-700 mb-6">Add New User</h2>
            <form onSubmit={handleAddUser}>
              {/* Utilisation de notre composant réutilisable ! */}
              <Input label="Name" name="fullName" value={formaData.fullName} onChange={handleInputChange} placeholder="Enter full name" />
              <Input label="Email" name="email" type="email" value={formaData.email} onChange={handleInputChange} placeholder="Enter email" />
              
              <div className="flex flex-col mt-4">
                <label className="font-medium text-gray-700 mb-1">Role</label>
                <select 
                  name="role" 
                  value={formaData.role} 
                  onChange={handleInputChange} 
                  className="border rounded-lg h-12 px-4 border-gray-300 focus:border-blue-500 outline-none bg-white"
                >
                  <option value="Customer">Customer</option>
                  <option value="Admin">Admin</option>
                </select>
              </div>

              <Input label="Password" name="password" type="password" value={formaData.password} onChange={handleInputChange} placeholder="**********" />
              
              <button 
                type="submit" 
                className="w-full mt-8 p-3 text-white font-bold rounded-lg bg-success hover:bg-green-600 transition-colors cursor-pointer"
              >
                Add User
              </button>
            </form>
          </div>

          {/* Colonne 2 : Tableau des utilisateurs */}
          <div className="lg:col-span-2 bg-white shadow-md rounded-lg p-4 overflow-x-auto">
            <table className="w-full text-center border-collapse">
              <thead>
                <tr className="h-14 text-sm md:text-lg font-bold bg-gray-100 text-gray-600">
                  <th className="px-4">NAME</th>
                  <th className="px-4">EMAIL</th>
                  <th className="px-4">ROLE</th>
                  <th className="px-4">ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id} className="h-20 border-b border-gray-100 hover:bg-gray-50">
                    <td className="font-bold text-gray-800">{user.fullName}</td>
                    <td className="text-gray-600">{user.email}</td>
                    <td className="p-2">
                      <span className={`px-3 py-1 rounded-full text-sm font-medium ${user.role === 'Admin' ? 'bg-purple-100 text-purple-700' : 'bg-blue-100 text-blue-700'}`}>
                        {user.role}
                      </span>
                    </td>
                    <td className="p-4">
                      <button 
                        onClick={() => handleDelete(user.id)} 
                        className="text-white font-medium bg-danger hover:bg-danger-hover px-4 py-2 rounded-lg transition-colors"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            
            {users.length === 0 && (
              <div className="text-center py-10">
                <p className="text-gray-500 text-lg">No users found.</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Users;