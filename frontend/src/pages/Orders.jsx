import { useContext, useEffect, useEffectEvent, useState } from "react";
import Sidebar from "../Components/Sidebar";
import axios from "axios";
import { UserContext } from "../App";



function Orders(){

    const [orders, setOrders] = useState([])

     const token = localStorage.getItem("token")

     const {userConnecte} = useContext(UserContext)

     useEffect(()=>{
        const fetchAllOrders = async()=>{
                try{
                    const response = await axios.get("http://localhost:8080/api/customer/allOrders",
                        {
                            headers:{
                                Authorization:`Bearer ${token}`
                            }
                        } 
                    )
                    if(response.status==200){
                        setOrders(response.data)
                    }
                    console.log(response)
                }catch(error){
                    console.log(error)
                }
        }   
        fetchAllOrders()
     },[])

    async function fetchEditOrder (order ,selected){
        
        
        
        try{
            const response = await axios.put(`http://localhost:8080/api/admin/editOrder/${order.id}`,
                {...order,status:selected},
                {
                    headers:{
                        Authorization:`Bearer ${token}`
                    }
                } 
            )
            if(response.status===200){
                setOrders(orders.map((ordr)=> order.id===ordr.id ? {...ordr,status:selected} : ordr ))
            }
            console.log(response)
        }catch(error){
            console.log(error)
        }
    }   

    console.log(userConnecte)
 

   


    return(
        <div className="flex flex-col md:flex-row w-full">
            <Sidebar />
            <div className="p-2 md:p-10 w-full">
                <h1 className="text-2xl font-bold mt-8">Order Management</h1>
                <table className="w-full mt-10 shadow-xl">
                    <thead>
                        <tr className="text-center h-14 text-sm md:text-lg font-bold bg-gray-200">
                            <td className="pl-10">ORDER ID</td>
                            <td>CUSTOMER</td>
                            <td>TOTAL PRICE</td>
                            <td>STATUS</td>
                        </tr>
                    </thead>
                    <tbody>
                        {orders && orders.map((order)=>{
                            return (
                                <>
                                    {userConnecte.id === order.user.id ?
                                        <tr  className="h-20 border border-gray-200 text-center">
                                            <td className="font-bold md:text-lg p-2">{order.id}</td>
                                            <td >{order.user.fullName}</td>
                                            <td >${order.totalprice}</td>
                                            <td className="p-4 pr-8">
                                                <select disabled={userConnecte.role !== 'Admin'} onChange={(e)=> {fetchEditOrder(order, e.target.value) }} value={order.status} className="border border-gray-200 p-2 w-20 md:w-full m-2 rounded-lg" >
                                                    <option>Pending</option>
                                                    <option>Processing</option>
                                                    <option>Completed</option>
                                                    <option>Canceled</option>
                                                </select>
                                            </td>
                                        </tr>
                                    :
                                    null
                                    }                                
                                </>
                            )
                        })}
                    </tbody>                  
                </table>
            </div>
        </div>
    )


}



export default Orders;