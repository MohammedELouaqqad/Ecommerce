import axios from "axios"
import { useContext, useState } from "react"
import { useNavigate } from "react-router-dom"
import { UserContext } from "../App"
import Sidebar from "../Components/Sidebar"
import { BsSendFill } from "react-icons/bs";

function Chat(){


    const [message, setMessage] = useState("") 

    const [ResponseAi, setResponseAi] = useState("") 

    const token = localStorage.getItem("token")

    async function handleMessage(){
        try{
            const response = await axios.post("http://localhost:8080/api/customer/chat",
                JSON.stringify(message),
                {
                    headers:{
                        Authorization:`Bearer ${token}`
                    }
                }
            )
            if(response.status==200){
                setResponseAi(response.data)
                setMessage("")
            }
        }catch(error){
            console.log(error)
        }
    }

    console.log(message)
    return(
        <div className="flex flex-col md:flex-row w-full">
            <Sidebar/>
            <div className="flex flex-col h-screen bg-gray-200 md:w-full p-10 pb-20">
                <h1 className="font-bold text-xl border-2 rounded h-16 w-full flex items-center justify-center bg-yellow-600">Chat with AI Assistant</h1>
                <div className="flex items-start gap-3 mt-4">
                    <div className="break-words mt-10 bg-yellow-100 w-90 md:w-100 rounded-2xl  p-4">{ResponseAi}</div>
                </div>
                <div className="flex mt-auto">
                    <input onChange={(e)=>setMessage(e.target.value)} className="bg-white rounded w-full p-4 " type="text" placeholder="How I can help you today?"/>
                    <button onClick={handleMessage}><BsSendFill className="cursor-pointer ml-4 h-full rounded-full w-14 p-3 bg-yellow-600"/></button>             
                </div>
            </div>
        </div>
    )
}



export default Chat;