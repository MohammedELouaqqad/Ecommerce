// src/services/orderService.js
import api from './api';

// (On garde la fonction createOrder qu'on a créée pour le Cart)
export const createOrder = async (orderData) => {
  try {
    const response = await api.post('/customer/addOrder', orderData);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la création de la commande:", error);
    throw error;
  }
};

// 1. Récupérer toutes les commandes
export const getAllOrders = async () => {
  try {
    const response = await api.get('/customer/allOrders');
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la récupération des commandes:", error);
    throw error;
  }
};

// 2. Modifier le statut d'une commande
export const updateOrderStatus = async (orderId, newStatus) => {
  try {
    // On n'envoie que le statut au backend, pas besoin d'envoyer tout l'objet order
    const response = await api.put(`/admin/editOrder/${orderId}`, { status: newStatus });
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la modification de la commande:", error);
    throw error;
  }
};