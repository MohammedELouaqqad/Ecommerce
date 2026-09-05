// src/services/chatService.js
import api from './api';

export const sendChatMessage = async (messageText) => {
  try {
    // On envoie un objet JSON standard
    const response = await api.post('/customer/chat', { message: messageText });
    return response.data; // Retourne la réponse de l'IA
  } catch (error) {
    console.error("Erreur lors de la communication avec l'IA:", error);
    throw error;
  }
};