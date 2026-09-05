// src/services/userService.js
import api from './api';

// Récupérer tous les utilisateurs (le token est géré automatiquement par api.js !)
export const getAllUsers = async () => {
  try {
    const response = await api.get('/auth/admin/allUsers');
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la récupération des users:", error);
    throw error;
  }
};

// Ajouter un utilisateur
export const registerUser = async (userData) => {
  try {
    const response = await api.post('/auth/register', userData);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de l'ajout de l'utilisateur:", error);
    throw error;
  }
};

// Supprimer un utilisateur
export const deleteUserApi = async (id) => {
  try {
    const response = await api.delete(`/auth/admin/deleteUser/${id}`);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la suppression:", error);
    throw error;
  }
};