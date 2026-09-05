// src/pages/Login.jsx
import { useContext, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Input from "../components/Input"; // Notre composant réutilisable
import { UserContext } from "../context/UserContext";
import { loginUser } from "../services/authService"; // Notre service !

function Login() {
  const navigate = useNavigate();
  const { setUserConnecte } = useContext(UserContext);
  const [formaData, setFormaData] = useState({ email: "", role: "Customer", password: "" });
  const [loading, setLoading] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormaData(prev => ({ ...prev, [name]: value }));
  };

  const handleAuthenticate = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      // 1. On appelle le service
      const data = await loginUser(formaData);
      
      // 2. On met à jour le Contexte (qui s'occupera du localStorage tout seul !)
      setUserConnecte(data.user);
      
      // 3. On stocke le token (généralement séparé de l'objet user)
      localStorage.setItem("token", data.token);
      
      // 4. SEULEMENT MAINTENANT, on peut rediriger vers le Shop !
      navigate("/Shop");
      
    } catch (error) {
      alert("Invalid email or password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="h-screen flex items-center justify-center bg-background p-4">
      <form 
        onSubmit={handleAuthenticate} 
        className="bg-white shadow-xl rounded-2xl p-8 w-full max-w-md border border-gray-100"
      >
        <h1 className="text-3xl font-bold text-center text-gray-800 mb-8">Welcome Back</h1>
        
        <Input label="Email" name="email" type="email" value={formaData.email} onChange={handleInputChange} placeholder="john@example.com" />
        
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
          disabled={loading}
          className="w-full mt-8 p-3 text-white font-bold rounded-lg bg-primary hover:bg-primary-hover transition-colors cursor-pointer disabled:opacity-50"
        >
          {loading ? "Logging in..." : "Login"}
        </button>
        
        <p className="text-center mt-6 text-gray-600">
          Don't have an account?{" "}
          <Link to="/register" className="text-primary font-bold hover:underline">
            Sign Up
          </Link>
        </p>
      </form>
    </div>
  );
}

export default Login;