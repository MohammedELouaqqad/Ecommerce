// src/pages/SignUp.jsx
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Input from "../components/Input"; // On réutilise notre composant !
import { registerUser } from "../services/authService"; // Notre service !

function SignUp() {
  const navigate = useNavigate();
  const [formaData, setFormaData] = useState({ fullName: "", email: "", role: "Customer", password: "" });
  const [loading, setLoading] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormaData(prev => ({ ...prev, [name]: value }));
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      // On utilise le service ! Plus d'URL en dur.
      await registerUser(formaData);
      alert("You are Registered with Success");
      navigate("/"); // Redirection vers le Login
    } catch (error) {
      alert("An error occurred during registration.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="h-screen flex items-center justify-center bg-background p-4">
      <form 
        onSubmit={handleRegister} 
        className="bg-white shadow-xl rounded-2xl p-8 w-full max-w-md border border-gray-100"
      >
        <h1 className="text-3xl font-bold text-center text-gray-800 mb-8">Create Account</h1>
        
        <Input label="Full Name" name="fullName" value={formaData.fullName} onChange={handleInputChange} placeholder="John Doe" />
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
          {loading ? "Registering..." : "Sign Up"}
        </button>
        
        <p className="text-center mt-6 text-gray-600">
          You already have an account?{" "}
          <Link to="/" className="text-primary font-bold hover:underline">
            Sign In
          </Link>
        </p>
      </form>
    </div>
  );
}

export default SignUp;