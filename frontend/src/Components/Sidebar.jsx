import { FaUser } from "react-icons/fa6";
import { MdOutlineProductionQuantityLimits } from "react-icons/md"
import { FaRegRectangleList } from "react-icons/fa6";
import { FaBagShopping } from "react-icons/fa6";
import { LuShoppingCart } from "react-icons/lu";
import { useContext } from "react";
import { UserContext } from "../App";
import { IoLogOutOutline } from "react-icons/io5";
import { useNavigate } from "react-router-dom";
import { RiChatAiFill } from "react-icons/ri";


function Sidebar(){

    const {userConnecte} = useContext(UserContext)
    const navigate = useNavigate()
   //console.log(userConnecte)

   const token = localStorage.getItem('token')
   const {cartProducts, setCartProducts} = useContext(UserContext)


   function handleLogout(){
        localStorage.removeItem('CartProducts')
        setCartProducts([])
        localStorage.removeItem('token')
        localStorage.removeItem('userConnecte')
        navigate('/')
   }
    
    return (
        <div className="h-full md:h-screen bg-black w-full md:w-110 text-white p-4">
            <div className="flex items-center">
                <h1 className="font-bold text-2xl text-white">{userConnecte.fullName}</h1>
                <a href="\Cart" className="  bg-yellow-500 rounded cursor-pointer p-4 ml-auto"><LuShoppingCart/></a>
            </div>
            <h2 className="font-bold text-xl mt-8 mb-16">{userConnecte.role} Dashboard</h2>
            <ul>
                {userConnecte && userConnecte.role ==="Admin"
                ?
                    <> 
                        <li><a href={userConnecte.role ==="Admin" ? "/Users" : "/"} className="flex border border-white rounded-xl p-4 mt-6 text-lg"><FaUser className="mr-4 mt-1"/>Users</a></li>
                        <li><a href={userConnecte.role ==="Admin" ? "/Products" : "/"}  className="flex border border-white rounded-xl p-4 mt-6 text-lg"><MdOutlineProductionQuantityLimits  className="mr-4 mt-1"/>Products</a></li>    
                    </>           
                :
                <></>
                }
                <li><a href={userConnecte ? "/Orders" : "/"}  className="flex border border-white rounded-xl p-4 mt-6 text-lg"><FaRegRectangleList className="mr-4 mt-1"/>Orders</a></li> 
                <li><a href={userConnecte ? "/Shop" : "/"}  className="flex border border-white rounded-xl p-4 mt-6 text-lg"><FaBagShopping className="mr-4 mt-1"/>Shop</a></li>
                <li><a href={userConnecte ? "/Chat" : "/"}  className="flex border border-white rounded-xl p-4 mt-6 text-lg"><RiChatAiFill className="mr-4 mt-1"/>Chat AI</a></li>
                <li><button onClick={handleLogout} className="cursor-pointer w-full flex border border-white rounded-xl p-4 mt-6 text-lg"><IoLogOutOutline className="mr-4 mt-1 text-xl"/>Logout</button></li>
            </ul>
        </div>
    )
}



export default Sidebar;