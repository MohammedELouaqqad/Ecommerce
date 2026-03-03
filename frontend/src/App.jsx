import { createContext, use, useState } from 'react'
import './App.css'
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom'
import Products from './pages/Products'
import Users from './pages/Users'
import Shop from './pages/Shop'
import Orders from './pages/Orders'
import Login from './pages/Login'
import Cart from './pages/Cart'
import SignUp from './pages/SignUp'
import Chat from './pages/Chat'

export const UserContext = createContext();

function App() {


  const [cartProducts, setCartProducts] = useState( JSON.parse(localStorage.getItem("CartProducts")) || [])

  const [userConnecte, setUserConnecte] = useState(JSON.parse(localStorage.getItem("userConnecte")) || {})

  const token = localStorage.getItem('token')

 
  return (
    <>
      <UserContext.Provider value={{cartProducts,setCartProducts,userConnecte , setUserConnecte}}>
        <Router>
          <Routes>
            <Route path='/' element={<Login/>}/>
            <Route path='/register' element={<SignUp/>}/>
            {userConnecte && userConnecte.role==='Admin' 
            ?
              <>
                <Route path='/Users' element={userConnecte.role==='Admin'?<Users/> : <Login/>}/>
                <Route path='/Products' element={userConnecte.role==='Admin'?<Products/> : <Login/>}/>

              </>
            :
              <>
              </>
            }

            <Route path='/Shop' element={userConnecte ? <Shop/> :<Login />}/>
            <Route path='/Orders' element={userConnecte ? <Orders/> :<Login />}/>
            <Route path='/Cart' element={userConnecte ? <Cart/> :<Login />}/>
            <Route path='/Chat' element={userConnecte ? <Chat/> :<Login />}/>
          </Routes>
        </Router>
      </UserContext.Provider>
    </>
  )
}

export default App
