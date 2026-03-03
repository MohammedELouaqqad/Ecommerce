import { useContext, useEffect, useState } from "react";
import axios from "axios"
import { UserContext } from "../App";
import Sidebar from "../Components/Sidebar";


function Products(){

    const [formaData,setFormaData] = useState({name:"",description:"",price:0,sku:"",sizes:"",countStock:0,filename:""})
    const [products,setProducts] = useState([])

    const [image,setImage] = useState("")

    const [showAddEditPage,setShowAddEditPage] = useState(false)

    const token = localStorage.getItem("token")



   
    const [editId,setEditId]=useState("")

    let fileName=""



    async function handleAddProduct(e){
        e.preventDefault()
        

        if(!editId){

            const fetchImage = async()=>{
                try{
                    console.log(image)
                    const form = new FormData()
                    form.append('image',image)
                    console.log(`Bearer ${token}`)
                    
                    const response = await axios.post("http://localhost:8080/api/customer/upload",
                        form,
                        {
                            headers:{
                                Authorization:`Bearer ${token}`,
                            }
                        }                    
                    )
                    console.log(response)
                    fileName = response.data
  

                }catch(Error){
                    console.log(Error)
                }
            }
            await fetchImage()

            const fetchProducts = async()=>{
                try{
                    
                    console.log({...formaData,filename:fileName})
                    const response = await axios.post("http://localhost:8080/api/admin/addProduct",
                        {...formaData,filename:fileName},
                        {
                            headers:{
                                Authorization:`Bearer ${token}`
                            }
                        }                    
                    )
                    if(response.status ==200){
                        setFormaData({name:"",description:"",price:"",sku:"",sizes:"",filename:""})
                        setShowAddEditPage(false)
                    }
                    console.log(response)
                }catch(error){
                    console.log(error)
                }
            }
            await fetchProducts()   
            
              
        }else{

            if(image){
                const fetchImage = async()=>{
                    try{
                        const form = new FormData()
                        form.append('image',image)
                        
                        const response = await axios.post("http://localhost:8080/api/customer/upload",
                            form,
                            {
                                headers:{
                                    Authorization:`Bearer ${token}`,
                                }
                            }                    
                        )
                        
                        fileName = response.data
                        return response.data

                    }catch(Error){
                        console.log(Error)
                    }
                }
                fileName = image ? await fetchImage() : formaData.filename
                
            }
            console.log(fileName)
            const fetchEditProducts = async()=>{
                try{
                    setImage()
                    console.log({...formaData,filename:fileName})
                    const response = await axios.put(`http://localhost:8080/api/admin/editProduct/${editId}`,
                        {...formaData,filename:fileName},
                        {
                            headers:{
                                Authorization:`Bearer ${token}`
                            }
                        }                    
                    )
                    if(response.status ==200){
                        setFormaData({name:"",description:"",price:"",sku:"",sizes:"",filename:""})
                        setShowAddEditPage(false)
                    }
                    console.log(response)
                }catch(error){
                    console.log(error)
                }
            }
            fetchEditProducts()            
        }
    }

    function deleteProduct(id){
        const newArray = products.filter((product)=> product.id!==id)
        setProducts(newArray)
        const fetchDeleteProduct = async()=>{
            try{
                const response = await axios.delete(`http://localhost:8080/api/admin/deleteProduct/${id}`,
                    {
                        headers:{
                            Authorization:`Bearer ${token}`
                        }
                    }
                )
                console.log(response)
            }catch(error){
                console.log(error)
            }
        }
        fetchDeleteProduct()
    }

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



    return(
        <div className="flex flex-col md:flex-row w-full">
            <Sidebar />
            {!showAddEditPage ?
                <div className="border border-blue-400 p-2 md:p-10 w-full">
                    <div className="flex h-20">
                        <h1 className="text-2xl font-bold mt-6">Product Management</h1>
                        <button onClick={()=>setShowAddEditPage(true)} className="cursor-pointer hover:bg-blue-400 bg-blue-600 rounded-lg w-36 h-14 mt-3 ml-18 ">Add Product</button>
                    </div>
                    <div className="w-full flex mt-8 w-20 md:w-full">
                        <table className="w-full mt-10 shadow-xl">
                            <thead>
                                <tr className="text-center h-14 text-sm md:text-lg font-bold bg-gray-200">
                                    <td className="pl-10">NAME</td>
                                    <td>PRICE</td>
                                    <td>SKU</td>
                                    <td>ACTIONS</td>
                                </tr>   
                            </thead> 
                            <tbody>
                                {products.map((product)=>{
                                    return(
                                        <tr className="h-20 border border-gray-200 text-center">
                                            <td className="font-bold md:text-lg">{product.name}</td>
                                            <td >${product.price}</td>
                                            <td >{product.sku}</td>
                                            <td className="p-3 md:p-4 flex flex-col md:flex-row"><button onClick={()=>{setFormaData(product),setEditId(product.id),setShowAddEditPage(true)}} type="button" className="cursor-pointer text-white font-bold bg-yellow-600 md:p-2 w-full rounded-lg mb-2 md:mr-2">Edit</button><button onClick={()=> deleteProduct(product.id)} type="button" className="cursor-pointer text-white font-bold bg-red-600 md:p-2 w-full rounded-lg mb-2 md:mr-2 ">Delete</button></td>
                                        </tr>  
                                    )
                                })}
                            </tbody>                 
                        </table>          
                    </div>
                </div>            
            :
                <div className="shadow-lg rounded-lg p-8 h-full w-full md:w-200 mt-14 md:ml-14">
                    <h1 className="font-bold text-xl">{editId ? "UPDATE":"ADD"}  Product</h1>
                    <form onSubmit={handleAddProduct}>
                        <div className="flex flex-col mt-8">
                            <label className="font-medium">Product Name</label>
                            <input value={formaData.name} onChange={(e)=>setFormaData({...formaData,name:e.target.value})}  className="border-1 rounded h-12 border-gray-400" type="text" />
                        </div>
                        <div className="flex flex-col mt-4">
                            <label className="font-medium">Description</label>
                            <textarea  value={formaData.description} onChange={(e)=>setFormaData({...formaData,description:e.target.value})}  className="border-1 rounded h-26 border-gray-400" type="text"></textarea>
                        </div>
                        <div className="flex flex-col mt-4">
                            <label className="font-medium">Price</label>
                            <input  value={formaData.price} onChange={(e)=>setFormaData({...formaData,price:e.target.value})}  className="border-1 rounded h-12 border-gray-400" type="number" />
                        </div>
                        <div className="flex flex-col mt-4">
                            <label className="font-medium">Count in Stock</label>
                            <input  value={formaData.countStock} onChange={(e)=>setFormaData({...formaData,countStock:e.target.value})}  className="border-1 rounded h-12 border-gray-400" type="number" />
                        </div>                    
                        <div className="flex flex-col mt-4">
                            <label className="font-medium">SKU</label>
                            <input  value={formaData.sku} onChange={(e)=>setFormaData({...formaData,sku:e.target.value})}  className="border-1 rounded h-12 border-gray-400" type="text" />
                        </div>
                        <div className="flex flex-col mt-4">
                            <label className="font-medium">Sizes</label>
                            <input  value={formaData.sizes} onChange={(e)=>setFormaData({...formaData,sizes:e.target.value})}  className="border-1 rounded h-12 border-gray-400" type="text" />
                        </div>   
                        <div className="flex flex-col mt-4">
                            <label className="font-medium">Upload Image</label>
                            <input onChange={(e)=> { !e.target.files[0] ? setImage(formaData.filename) : setImage(e.target.files[0]) }} className="bg-gray-200 border-2 rounded-lg h-12 text-center p-2 border-gray-400 w-60" type="file" />
                            <img alt="Image not found" src={`http://localhost:8080/api/customer/download/${formaData.filename}`} className="mt-10 m-auto w-full h-40 flex items-center justify-center text-red-600 font-bold border-2 border-gray-200 rounded"/>
                        </div>   
                        <button className="cursor-pointer hover:bg-green-400 bg-green-600 text-white rounded-lg p-3 mt-10 w-70 font-bold">{editId ? "UPDATE":"ADD"} PRODUCT</button>                                                                                                    
                    </form>
                </div>            
            }
        </div>
    )


}



export default Products;