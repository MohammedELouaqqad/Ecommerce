// src/services/productService.js
import api from './api'; // On importe l'instance axios configurée

// 1. Récupérer tous les produits
export const getAllProducts = async () => {
  try {
    const response = await api.get('/customer/allProducts');
    return response.data; // On retourne directement les données
  } catch (error) {
    console.error("Erreur lors de la récupération des produits:", error);
    throw error; // On relance l'erreur pour que le composant puisse la gérer (ex: afficher un message)
  }
};

// 2. Ajouter un produit
export const addProduct = async (productData) => {
  try {
    const response = await api.post('/admin/addProduct', productData);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de l'ajout du produit:", error);
    throw error;
  }
};

// 3. Modifier un produit
export const updateProduct = async (id, productData) => {
  try {
    const response = await api.put(`/admin/editProduct/${id}`, productData);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la modification du produit:", error);
    throw error;
  }
};

// 4. Supprimer un produit
export const deleteProduct = async (id) => {
  try {
    const response = await api.delete(`/admin/deleteProduct/${id}`);
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la suppression du produit:", error);
    throw error;
  }
};

// 5. Uploader une image
export const uploadImage = async (imageFile) => {
  try {
    const formData = new FormData();
    formData.append('image', imageFile);
    
    // Remarque : On précise le header Content-Type pour l'upload de fichier
    const response = await api.post('/customer/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    return response.data; // Retourne le nom du fichier (fileName)
  } catch (error) {
    console.error("Erreur lors de l'upload de l'image:", error);
    throw error;
  }
};