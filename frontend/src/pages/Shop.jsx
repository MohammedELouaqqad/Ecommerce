// src/pages/Shop.jsx
import { useContext, useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";
import ProductCard from "../components/ProductCard"; // Notre nouveau composant
import { UserContext } from "../context/UserContext";
import { getAllProducts } from "../services/productService"; // Notre service !

function Shop() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true); // Bonne pratique : état de chargement
  const { cartProducts, setCartProducts } = useContext(UserContext);

  // 1. Récupération des données via le service
  useEffect(() => {
    const loadProducts = async () => {
      try {
        const data = await getAllProducts();
        setProducts(data);
      } catch (error) {
        console.log("Failed to fetch products");
      } finally {
        setLoading(false);
      }
    };
    loadProducts();
  }, []);

  // 2. Synchronisation du panier avec le localStorage
  useEffect(() => {
    localStorage.setItem("CartProducts", JSON.stringify(cartProducts));
  }, [cartProducts]);

  // 3. Fonction pour ajouter au panier (passée au composant enfant)
  const handleAddToCart = (productToAdd) => {
    setCartProducts((prevCart) => {
      // Optionnel (Best Practice) : Vérifier si le produit est déjà dans le panier
      const existingProduct = prevCart.find(p => p.id === productToAdd.id);
      if (existingProduct) {
        // S'il y est, on ne l'ajoute pas deux fois (la gestion de la quantité se fera dans Cart.jsx)
        return prevCart;
      }
      return [...prevCart, productToAdd];
    });
  };

  if (loading) {
    return (
      <div className="flex w-full min-h-screen bg-background">
        <Sidebar />
        <div className="flex-1 flex justify-center items-center">
          <p className="text-gray-500 text-xl animate-pulse">Loading products...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col md:flex-row w-full min-h-screen bg-background">
      <Sidebar />
      
      <div className="p-4 md:p-10 w-full">
        <h1 className="text-3xl font-bold text-gray-800 mb-8">Our Products</h1>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {products.map((product) => (
            // On passe les "props" au composant
            <ProductCard 
              key={product.id} 
              product={product} 
              onAddToCart={handleAddToCart} 
            />
          ))}
        </div>

        {products.length === 0 && !loading && (
          <div className="text-center mt-20">
            <p className="text-gray-500 text-xl">No products available at the moment.</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default Shop;