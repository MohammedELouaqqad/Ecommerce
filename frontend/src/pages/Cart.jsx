// src/pages/Cart.jsx
import { useContext, useEffect, useMemo, useState } from "react";
import { UserContext } from "../context/UserContext";
import Sidebar from "../components/Sidebar";
import QuantityStepper from "../components/QuantityStepper"; // Notre nouveau composant
import { useNavigate } from "react-router-dom";
import { createOrder } from "../services/orderService"; // Notre nouveau service

function Cart() {
  const { cartProducts, setCartProducts, userConnecte } = useContext(UserContext);
  const [orderItems, setOrderItems] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    setOrderItems(
      cartProducts.map((prod) => ({
        name: prod.name,
        price: prod.price,
        quantite: 1,
        totalPrice: prod.price,
        product: { id: prod.id },
      }))
    );
  }, [cartProducts]);

  // Calcul optimisé
  const totalPriceOrder = useMemo(() => {
    return orderItems.reduce((acc, curr) => acc + curr.totalPrice, 0);
  }, [orderItems]);

  // Handlers purs qui mettent à jour le state local
  const handleAddQuantite = (id) => {
    setOrderItems((prev) =>
      prev.map((item) =>
        item.product.id === id
          ? { ...item, quantite: item.quantite + 1, totalPrice: item.price * (item.quantite + 1) }
          : item
      )
    );
  };

  const handleMinusQuantite = (id) => {
    setOrderItems((prev) =>
      prev.map((item) =>
        item.product.id === id && item.quantite > 1
          ? { ...item, quantite: item.quantite - 1, totalPrice: item.price * (item.quantite - 1) }
          : item
      )
    );
  };

  const deleteOrderItem = (id) => {
    const newItems = orderItems.filter((item) => item.product.id !== id);
    setOrderItems(newItems);
    
    const newCartProducts = cartProducts.filter((prod) => prod.id !== id);
    setCartProducts(newCartProducts);
    localStorage.setItem("CartProducts", JSON.stringify(newCartProducts));
  };

  // Utilisation du Service propre !
  const handleConfirmOrder = async () => {
    const orderData = {
      status: "Processing",
      user: { id: userConnecte.id },
      totalprice: totalPriceOrder,
      orderItems: orderItems,
    };

    try {
      await createOrder(orderData); // Appel au service
      setCartProducts([]); // Vide le panier du Context
      localStorage.removeItem("CartProducts"); // Vide le localStorage
      navigate("/Orders");
    } catch (error) {
      alert("Erreur lors de la validation de la commande.");
    }
  };

  return (
    <div className="flex flex-col md:flex-row w-full min-h-screen bg-background">
      <Sidebar />
      <div className="h-full flex flex-col mt-8 w-full p-6">
        <h1 className="text-2xl font-bold text-gray-800 mb-6">Shopping Cart</h1>
        
        <div className="overflow-x-auto bg-white shadow-md rounded-lg p-4">
          <table className="w-full text-center border-collapse">
            <thead>
              <tr className="h-14 text-sm md:text-lg font-bold bg-gray-100 text-gray-600">
                <th className="px-4">NAME</th>
                <th className="px-4">PRICE</th>
                <th className="px-4">QUANTITY</th>
                <th className="px-4">TOTAL PRICE</th>
                <th className="px-4">ACTION</th>
              </tr>
            </thead>
            <tbody>
              {orderItems.map((item) => (
                <tr key={item.product.id} className="h-20 border-b border-gray-100 hover:bg-gray-50">
                  <td className="font-medium text-gray-800">{item.name}</td>
                  <td className="text-gray-600">${item.price}</td>
                  <td>
                    {/* Utilisation du composant réutilisable ! */}
                    <QuantityStepper 
                      quantity={item.quantite} 
                      onIncrease={() => handleAddQuantite(item.product.id)} 
                      onDecrease={() => handleMinusQuantite(item.product.id)} 
                    />
                  </td>
                  <td className="font-bold text-gray-800">${item.totalPrice}</td>
                  <td className="p-4">
                    <button
                      onClick={() => deleteOrderItem(item.product.id)}
                      className="cursor-pointer text-white font-medium bg-danger hover:bg-danger-hover px-4 py-2 w-full md:w-auto rounded-lg transition-colors"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {orderItems.length === 0 ? (
          <div className="flex justify-center mt-10">
            <h2 className="text-white font-bold bg-danger rounded-lg p-4">
              No Available Orders
            </h2>
          </div>
        ) : (
          <div className="flex flex-col md:flex-row justify-between items-center mt-8 p-4 bg-white shadow-md rounded-lg">
            <div className="text-xl font-bold text-gray-800 mb-4 md:mb-0">
              Total: <span className="text-primary">${totalPriceOrder}</span>
            </div>
            <button
              onClick={handleConfirmOrder}
              className="bg-primary hover:bg-primary-hover text-white font-bold rounded-lg w-full md:w-60 h-12 transition-colors"
            >
              Confirm Order
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default Cart;