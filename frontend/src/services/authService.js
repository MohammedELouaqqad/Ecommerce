// src/services/authService.js
import api from './api';

// (On garde la fonction registerUser créée précédemment)
export const registerUser = async (userData) => {
  try {
    const response = await api.post('/auth/register', userData);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de l'inscription:", error);
    throw error;
  }
};

// NOUVELLE FONCTION : Login
export const loginUser = async (credentials) => {
  try {
    const response = await api.post('/auth/authenticate', credentials);
    return response.data; // Retourne { token, user }
  } catch (error) {
    console.error("Erreur de connexion:", error);
    throw error;
  }
};