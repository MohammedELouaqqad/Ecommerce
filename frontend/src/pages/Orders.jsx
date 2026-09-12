// src/pages/Orders.jsx
import { useContext, useEffect, useMemo, useState } from "react";
import Sidebar from "../components/Sidebar";
import { UserContext } from "../context/UserContext";
import { getAllOrders, updateOrderStatus } from "../services/orderService";

function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const { userConnecte } = useContext(UserContext);

  // 1. Chargement initial via le service
  useEffect(() => {
    const loadOrders = async () => {
      try {
        const data = await getAllOrders();
        setOrders(data);
      } catch (error) {
        console.log("Failed to fetch orders");
      } finally {
        setLoading(false);
      }
    };
    loadOrders();
  }, []);

  // 2. Mise à jour du statut via le service
  const handleStatusChange = async (orderId, newStatus) => {
    // Mise à jour optimiste de l'UI pour une expérience ultra rapide
    setOrders(prevOrders => 
      prevOrders.map(ordr => ordr.id === orderId ? { ...ordr, status: newStatus } : ordr)
    );

    try {
      await updateOrderStatus(orderId, newStatus);
    } catch (error) {
      alert("Erreur lors de la mise à jour du statut.");
      // En cas d'erreur, on pourrait recharger les données, mais on simplifie ici
    }
  };

  // 3. BEST PRACTICE : On filtre AVANT de mapper !
  // Si l'utilisateur est Admin, il voit tout. Sinon, il ne voit que ses commandes.
  const visibleOrders = useMemo(() => {
    if (userConnecte?.role === 'Admin') {
      return orders;
    }
    return orders.filter(order => order.user?.id === userConnecte?.id);
  }, [orders, userConnecte?.id, userConnecte?.role]);


  if (loading) {
    return (
      <div className="flex w-full min-h-screen bg-background">
        <Sidebar />
        <div className="flex-1 flex justify-center items-center">
          <p className="text-gray-500 text-xl animate-pulse">Loading orders...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col md:flex-row w-full min-h-screen bg-background">
      <Sidebar />
      <div className="p-4 md:p-10 w-full">
        <h1 className="text-2xl font-bold text-gray-800 mt-8 mb-6">Order Management</h1>
        
        <div className="overflow-x-auto bg-white shadow-md rounded-lg p-4">
          <table className="w-full text-center border-collapse">
            <thead>
              <tr className="h-14 text-sm md:text-lg font-bold bg-gray-100 text-gray-600">
                <th className="px-4">ORDER ID</th>
                <th className="px-4">CUSTOMER</th>
                <th className="px-4">TOTAL PRICE</th>
                <th className="px-4">STATUS</th>
              </tr>
            </thead>
            <tbody>
              {visibleOrders.map((order) => (
                // La KEY est mise directement sur le <tr> !
                <tr key={order.id} className="h-20 border-b border-gray-100 hover:bg-gray-50">
                  <td className="font-bold text-gray-800 p-2">#{order.id}</td>
                  <td className="text-gray-600">{order.user?.fullName || "Unknown"}</td>
                  <td className="text-gray-600">${order.totalprice}</td>
                  <td className="p-4">
                    <select 
                      disabled={userConnecte?.role !== 'Admin'} 
                      onChange={(e) => handleStatusChange(order.id, e.target.value)} 
                      value={order.status} 
                      className={`border border-gray-300 p-2 w-full rounded-lg focus:outline-none focus:ring-2 focus:ring-primary cursor-pointer ${userConnecte?.role !== 'Admin' ? 'bg-gray-100 cursor-not-allowed text-gray-500' : 'bg-white'}`}
                    >
                      <option value="Pending">Pending</option>
                      <option value="Processing">Processing</option>
                      <option value="Completed">Completed</option>
                      <option value="Canceled">Canceled</option>
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {visibleOrders.length === 0 && (
            <div className="text-center py-10">
              <p className="text-gray-500 text-lg">You have no orders yet.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default Orders;