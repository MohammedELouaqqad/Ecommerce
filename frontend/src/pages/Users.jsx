import { useContext, useEffect, useState } from "react";
import Sidebar from "../Components/Sidebar";
import axios from "axios";
import { UserContext } from "../App";



function Users(){

    const [formaData,setFormaData] = useState({fullName:"",email:"",role:"Customer",password:""})
    const [users,setUsers]=useState([])

    const token = localStorage.getItem("token");

    const {userConnecte} = useContext(UserContext)

    function handleAddUser(e){
        e.preventDefault()
     
        const fetchAddUser = async ()=>{
            try{
                const response = await axios.post("http://localhost:8080/api/auth/register",formaData)
                if(response.status==200){
                    console.log(response)
                }
                console.log(response)
            }catch(error){
                console.log(error)
            }
        } 
        fetchAddUser()
    }

    function deleteUser(id){
        const newArray = users.filter((user)=> user.id!==id)
        setUsers(newArray)
        const fetchDeleteUser = async ()=>{
            try{    
                const response = await axios.delete(`http://localhost:8080/api/auth/admin/deleteUser/${id}`)
                console.log(response)

            }catch(error){
                console.log(error)
            }
        }
        fetchDeleteUser()
    }



    useEffect(()=>{
        const fetchAllUsers = async()=>{
            try{
                const response = await axios.get("http://localhost:8080/api/auth/admin/allUsers",
                    {
                        headers:{
                            Authorization:`Bearer ${token}`
                        }
                    }
                )
                if(response.status==200){
                    setUsers(response.data)
                }
                
                console.log(response)
            }catch(error){
                console.log(error)
            }
        }
        fetchAllUsers()
    },[])


    return(
        <div className="flex flex-col md:flex-row w-full">
            <Sidebar />
            <div className="border border-blue-400 p-4 md:p-10 w-full">
                <h1 className="text-2xl font-bold">User Management</h1>
                <h2 className="text-xl mt-10 font-medium">Add New User</h2>
                <form onSubmit={handleAddUser}>
                    <div className="flex flex-col mt-10">
                        <label>Name</label>
                        <input value={formaData.fullName} onChange={(e)=>setFormaData({...formaData,fullName:e.target.value})} className="border border-gray-200 p-4 rounded-lg" placeholder="Enter your Name"/>
                    </div>
                    <div className="flex flex-col mt-6">
                        <label>Email</label>
                        <input  value={formaData.email} onChange={(e)=>setFormaData({...formaData,email:e.target.value})} className="border border-gray-200 p-4 rounded-lg"  placeholder="Enter your Email"/>
                    </div>
                    <div className="flex flex-col mt-6">
                        <label>Role</label>
                        <select  value={formaData.role} onChange={(e)=>setFormaData({...formaData,role:e.target.value})} className="border border-gray-200 p-4 rounded-lg" >
                            <option>Select a Role</option>
                            <option>Customer</option>
                            <option>Admin</option>
                        </select>
                    </div>
                    <div className="flex flex-col mt-6">
                        <label>Password</label>
                        <input  value={formaData.password} onChange={(e)=>setFormaData({...formaData,password:e.target.value})} className="border border-gray-200 p-4 rounded-lg"   placeholder="**********" type="password"/>
                    </div>
                    <button className="hover:bg-green-300 text-lg font-medium cursor-pointer p-3 rounded-lg w-40 mt-10 text-white bg-green-400">Add User</button>
                </form>
                <table className="w-103 md:w-full mt-10 shadow-xl">
                    <thead>
                        <tr className="text-center h-14 md:text-lg font-bold bg-gray-200">
                            <td className="pl-10">NAME</td>
                            <td>EMAIL</td>
                            <td>ROLE</td>
                            <td>ACTIONS</td>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((user)=>{
                            return(
                                <tr className="h-20 border border-gray-200 text-center">
                                    <td className="font-bold text-lg">{user.fullName}</td>
                                    <td >{user.email}</td>
                                    <td>
                                        <select disabled value={user.role} className="border border-gray-200 p-2 rounded-lg" >
                                            <option>Customer</option>
                                            <option>Admin</option>
                                        </select>
                                    </td>
                                    <td><button onClick={()=>deleteUser(user.id)} type="button" className="hover:bg-red-400 cursor-pointer text-white font-bold bg-red-600 p-2 md:w-30 rounded-lg">Delete</button></td>
                                </tr>                                 
                            )
                        })}
                    </tbody>                 
                </table>
            </div>
        </div>
    )


}



export default Users;