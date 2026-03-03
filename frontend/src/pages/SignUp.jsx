import axios from "axios"
import { useContext, useState } from "react"
import { useNavigate } from "react-router-dom"
import { UserContext } from "../App"


function SignUp(){

    const navigate = useNavigate()
    const [formaData,setFormaData] = useState({fullName:"",email:"",role:"Customer",password:""})

     const {userConnecte, setUserConnecte} = useContext(UserContext)

     localStorage.setItem("userConnecte",JSON.stringify(userConnecte))

    function handleRegister(e){
        e.preventDefault()
        try{
            const fetchRegister = async()=>{
                const response = await axios.post("http://localhost:8080/api/auth/register",formaData)
                if(response.status==200){
                    alert("You are Register with Success")
                    // const token = response.data.token
                    // localStorage.setItem("token",token)
                    navigate("/")
                }
                setUserConnecte(response.data.user)
            }
            fetchRegister()
        }catch(error){
            console.log(error)
        }
    }


    return(
        <div className="h-screen flex items-center justify-center">
            <form className="bg-blue-200 h-135 rounded-lg p-4 w-110" onSubmit={handleRegister}>
                <h1 className="text-xl font-bold text-center mt-2">Register</h1>
                <div className="flex flex-col mt-6">
                    <label>FullName</label>
                    <input  value={formaData.fullName} onChange={(e)=>setFormaData({...formaData,fullName:e.target.value})}   className="bg-gray-100 rounded p-3 " placeholder="Enter your Email"/>
                </div>
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
                    <button className="ml-auto bg-blue-600 w-60 h-12 rounded-lg">SignUp</button>      
                    <a className="ml-auto mt-3 border-b-1 text-blue-800 border-black-400" href="/">You have already an account?</a>             
                </div>                    
            </form>
        </div>
    )
}



export default SignUp;