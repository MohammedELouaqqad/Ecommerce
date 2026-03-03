import { useContext, useEffect, useState } from "react";
import { UserContext } from "../App";
import Sidebar from "../Components/Sidebar";
import { IoAddCircle } from "react-icons/io5";
import { LuCircleMinus } from "react-icons/lu";
import axios from "axios";
import { useNavigate } from "react-router-dom"


function Cart(){
    const {cartProducts} = useContext(UserContext)
    const {userConnecte} = useContext(UserContext)
    

    const [orderItems,setOrderItems]=useState([])


    var totalPriceOrder = orderItems.reduce((acc,curr)=>acc+curr.totalPrice,0);


    // if(totalPriceOrder!==0){
        const order ={
            status:"Processing",
            user:{id:userConnecte.id},
            totalprice:totalPriceOrder,
            orderItems:orderItems,
        }
        console.log(order)
    // }

    
    const navigate = useNavigate()
 

    useEffect(()=>{
            setOrderItems(cartProducts.map(prod=>({name:prod.name,price:prod.price,quantite:1,totalPrice:prod.price,product:{id:prod.id}})))
    },[])


    function handleAddQuantite(id){
        setOrderItems(orderItems.map(product => product.product.id===id ? {...product,quantite:product.quantite+ 1,totalPrice:product.price*(product.quantite+1)} : product ))
    }

    function handleMunisQuantite(id){
        setOrderItems(orderItems.map(product => product.product.id===id ? {...product,quantite:product.quantite-1,totalPrice:product.price*(product.quantite-1)} : product ))
    }

    const token = localStorage.getItem("token")
  

    const fetchAddOrder = async()=>{
        try{
            const response = await axios.post("http://localhost:8080/api/customer/addOrder",
                order,
                {
                    headers:{
                        Authorization:`Bearer ${token}`
                    }
                } 
            )
            if(response.status==200){
                localStorage.removeItem('CartProducts')
                navigate("/Orders")
            }
            console.log(response)
        }catch(error){
            console.log(error)
        }
    }


    function deleteOrderItem(id){
        var newOrderItem = orderItems.filter((ordr)=> ordr.id !== id)
        localStorage.setItem('CartProducts',JSON.stringify(newOrderItem))
    }

   

    return (
        <div className="flex flex-col md:flex-row w-full">
            <Sidebar/>
            <div className="h-full flex flex-col mt-8 w-full md:w-full p-6 ">
                <table className="w-full mt-10 shadow-xl ">
                    <thead>
                        <tr className=" text-center h-14 text-sm md:text-lg font-bold bg-gray-200">
                            <td className="">NAME</td>
                            <td>PRICE</td>
                            <td>Quantite</td>
                            <td>Total Price</td>
                            <td>Action</td>
                        </tr>   
                    </thead> 
                    <tbody>
                        {orderItems && orderItems.map(product=>{
                            return(
                                <tr className="h-20 border border-gray-200 text-center">
                                    <td className="font-bold md:text-lg">{product.name}</td>
                                    <td >${product.price}</td>
                                    <td ><button onClick={()=>handleAddQuantite(product.product.id)} className="cursor-pointer p-2"><IoAddCircle/></button> [{product.quantite}] <button onClick={()=>handleMunisQuantite(product.product.id)} className="cursor-pointer p-2"> <LuCircleMinus/> </button></td>
                                    <td>${product.totalPrice}</td>
                                    <td className="p-4"><button onClick={()=> deleteOrderItem(product.id)} type="button" className="cursor-pointer text-white font-bold bg-red-600 md:p-2 w-full rounded-lg mb-2 ">Delete</button></td>
                                </tr> 
                            )
                        })}
                    </tbody>                 
                </table>   
                {orderItems.length === 0 ?
                    <div className="flex justify-center">
                        <h2 className="h-14 items-center mt-10 flex justify-center text-white font-bold bg-red-600 rounded-lg p-2 w-50">No Available Order</h2>
                    </div>
                    
                :
                    <button onClick={fetchAddOrder} className="mt-20 ml-80 bg-purple-700 rounded-lg w-60 h-12 hover:bg-purple-500">Confirm Order</button>
                }
            </div> 
        </div>
    )

}


export default Cart;