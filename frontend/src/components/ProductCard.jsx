// src/components/ProductCard.jsx
import { useState } from "react";

const ProductCard = ({ product, onAddToCart }) => {
  const [isAdded, setIsAdded] = useState(false);

  const handleAdd = () => {
    onAddToCart(product);
    setIsAdded(true);
    setTimeout(() => setIsAdded(false), 1000); // Petit effet visuel pendant 1 sec
  };

  // Calcul du faux prix barré proprement isolé
  const oldPrice = (product.price + 200.99).toFixed(2);

  return (
    <div className="bg-white shadow-md rounded-xl p-4 border border-gray-100 flex flex-col transition-transform hover:scale-105 hover:shadow-lg">
      <img 
        crossOrigin="anonymous" 
        alt={product.name} 
        src={`http://localhost:8080/api/customer/download/${product.filename}`} 
        className="w-full h-40 object-contain rounded-lg mb-4"
      />
      <h2 className="text-lg font-bold text-gray-800">{product.name}</h2>
      <p className={`text-sm mb-2 ${product.countStock > 0 ? 'text-green-600' : 'text-red-500'}`}>
        {product.countStock > 0 ? `In Stock (${product.countStock})` : 'Out of Stock'}
      </p>
      
      <div className="flex items-center mt-1 mb-4">
        <h3 className="font-bold text-lg text-primary mr-3">${product.price}</h3>
        <del className="text-gray-400 font-light text-sm">${oldPrice}</del>
      </div>

      <button 
        onClick={handleAdd} 
        disabled={product.countStock === 0}
        className={`mt-auto w-full p-2 text-white font-bold rounded-lg transition-colors cursor-pointer
          ${isAdded ? 'bg-success' : 'bg-primary hover:bg-primary-hover'} 
          ${product.countStock === 0 ? 'opacity-50 cursor-not-allowed' : ''}`
        }
      >
        {isAdded ? 'Added ✓' : 'Add to Cart'}
      </button>
    </div>
  );
};

export default ProductCard;