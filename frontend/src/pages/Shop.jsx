import { useContext, useEffect, useState } from "react";
import Sidebar from "../Components/Sidebar";
import axios from "axios";
import { UserContext } from "../App";
import Products from './Products';



function Shop(){


    const [products,setProducts] = useState([])


    const token = localStorage.getItem("token")

    

     const {cartProducts, setCartProducts} = useContext(UserContext)
    


    

    useEffect(()=>{
        const fetchAllProducts = async()=>{
            try{
                const response = await axios.get("http://localhost:8080/api/customer/allProducts",
                    {
                        headers:{
                            Authorization:`Bearer ${token}`
                        }
                    }
                )
                if(response.status==200){
                    setProducts(response.data)
                }
                console.log(response)
            }catch(error){
                console.log(error)
            }
        }
        fetchAllProducts()
    },[])

    useEffect(()=>{
        localStorage.setItem("CartProducts",JSON.stringify(cartProducts))
    },[cartProducts])



    return(
        <div className="flex flex-col md:flex-row w-full">
            <Sidebar />
            <div className="border border-blue-400 p-2 md:p-10 w-full">
                <h1 className="text-2xl font-bold mt-6">Product Management</h1>
                <div className="w-full flex grid grid-cols-2 gap-3 md:gap-4 xl:grid-cols-4 mt-8 w-20 md:w-full">
                    {products.map((product)=>{
                        return(
                            <div key={product.id} className="shadow-lg rounded-xl p-4 border-2 border-gray-300 w-60">
                                {console.log(product)}
                                <img crossOrigin="anonymous" alt="Image not found" src={`http://localhost:8080/api/customer/download/${product.filename}`} className="m-auto w-full h-40 flex items-center justify-center text-red-600 font-bold border-2 border-gray-200 rounded"/>
                                <h2 className="text-lg font-bold">{product.name}</h2>
                                <p>In Stock {product.countStock}</p>
                                <div className="flex mt-2"><h3 className="font-medium mr-4 text-green-800">${product.price}</h3><del className="font-light">${product.price+200.99}</del></div>
                                <button onClick={()=> {setCartProducts(prev => [...prev,product])} } className="w-40 mt-4 p-2 text-white font-bold rounded-xl bg-green-700 hover:bg-green-800 cursor-pointer">Add to Cart</button>
                            </div> 
                        )
                    })}              
                </div>
            </div>
        </div>
    )


}



export default Shop;