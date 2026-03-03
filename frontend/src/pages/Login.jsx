import axios from "axios"
import { useContext, useState } from "react"
import { useNavigate } from "react-router-dom"
import { UserContext } from "../App"


function Login(){

    const navigate = useNavigate()
    const [formaData,setFormaData] = useState({email:"",role:"Customer",password:""})

     const {userConnecte, setUserConnecte} = useContext(UserContext)

     localStorage.setItem("userConnecte",JSON.stringify(userConnecte))

    function handleAuthenticate(e){
        e.preventDefault()
        try{
            const fetchAuthenticate = async()=>{
                const response = await axios.post("http://localhost:8080/api/auth/authenticate",formaData)
                if(response.status==200){
                    const token = response.data.token
                    localStorage.setItem("token",token)
                    navigate("/Shop")
                }
                setUserConnecte(response.data.user)
            }
            fetchAuthenticate()
        }catch(error){
            console.log(error)
        }
    }


    return(
        <div className="h-screen flex items-center justify-center">
            <form className="bg-blue-200 h-115 rounded-lg p-4 w-110" onSubmit={handleAuthenticate}>
                <h1 className="text-xl font-bold text-center mt-2">LOGIN</h1>
                <div className="flex flex-col mt-4">
                    <label>Email</label>
                    <input  value={formaData.email} onChange={(e)=>setFormaData({...formaData,email:e.target.value})}   className="bg-gray-100 rounded p-3 " placeholder="Enter your Email"/>
                </div>
                <div className="flex flex-col mt-4">
                    <label>Role</label>
                    <select  value={formaData.role} onChange={(e)=>setFormaData({...formaData,role:e.target.value})}   className="bg-gray-100 rounded p-3 ">
                        <option>Customer</option>
                        <option>Admin</option>
                    </select>
                </div>   
                <div className="flex flex-col mt-4 mb-8">
                    <label>Password</label>
                    <input  value={formaData.password} onChange={(e)=>setFormaData({...formaData,password:e.target.value})}   className="bg-gray-100 rounded p-3 " type="password" placeholder="**********"/>
                </div>          
                <div className="flex flex-col">
                    <button className="ml-auto bg-blue-600 w-60 h-12 rounded-lg">Login</button>      
                    <a className="ml-auto mt-3 border-b-1 text-blue-800 border-black-400" href="/register">You have any account?</a>             
                </div>                    
            </form>
        </div>
    )
}



export default Login;