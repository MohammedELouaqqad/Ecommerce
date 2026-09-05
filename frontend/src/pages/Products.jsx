// src/pages/Products.jsx
import { useEffect, useState } from "react";
import Sidebar from "../components/Sidebar";
import ProductForm from "../components/ProductForm";
import { getAllProducts, deleteProduct as deleteProductApi } from "../services/productService";

function Products() {
  const [formaData, setFormaData] = useState({ name: "", description: "", price: 0, sku: "", sizes: "", countStock: 0, filename: "" });
  const [products, setProducts] = useState([]);
  const [showAddEditPage, setShowAddEditPage] = useState(false);
  const [editId, setEditId] = useState("");

  // Clean API call!
  const fetchProducts = async () => {
    try {
      const data = await getAllProducts();
      setProducts(data);
    } catch (error) {
      console.log("Failed to fetch products");
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const handleDelete = async (id) => {
    try {
      await deleteProductApi(id); // Wait for API to delete
      setProducts(products.filter(p => p.id !== id)); // Update UI
    } catch (error) {
      alert("Error deleting product");
    }
  };

  const handleEdit = (product) => {
    setFormaData(product);
    setEditId(product.id);
    setShowAddEditPage(true);
  };

  return (
    <div className="flex flex-col md:flex-row w-full bg-gray-50 min-h-screen">
      <Sidebar />
      
      {!showAddEditPage ? (
        <div className="border border-blue-100 p-2 md:p-10 w-full bg-white shadow-sm rounded-lg m-4">
          <div className="flex flex-col md:flex-row justify-between items-center mb-8">
            <h1 className="text-2xl font-bold text-gray-800">Product Management</h1>
            <button 
              onClick={() => { 
                setFormaData({ name: "", description: "", price: 0, sku: "", sizes: "", countStock: 0, filename: "" }); 
                setEditId(""); 
                setShowAddEditPage(true); 
              }} 
              className="cursor-pointer hover:bg-blue-500 bg-blue-600 text-white rounded-lg px-6 py-3 mt-4 md:mt-0 font-medium transition-colors"
            >
              + Add Product
            </button>
          </div>
          
          <div className="overflow-x-auto">
            <table className="w-full mt-4 text-left border-collapse">
              <thead>
                <tr className="h-14 text-sm md:text-lg font-bold bg-gray-100 text-gray-600">
                  <th className="pl-10 pr-4">NAME</th>
                  <th className="px-4">PRICE</th>
                  <th className="px-4">SKU</th>
                  <th className="px-4 text-center">ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {products.map((product) => (
                  <tr key={product.id} className="h-20 border-b border-gray-100 hover:bg-gray-50">
                    <td className="pl-10 pr-4 font-medium text-gray-800">{product.name}</td>
                    <td className="px-4 text-gray-600">${product.price}</td>
                    <td className="px-4 text-gray-600">{product.sku}</td>
                    <td className="px-4 flex flex-col md:flex-row gap-2 items-center justify-center mt-6 md:mt-0">
                      <button 
                        onClick={() => handleEdit(product)} 
                        className="cursor-pointer text-white font-medium bg-yellow-500 hover:bg-yellow-600 px-4 py-2 w-full md:w-auto rounded-lg transition-colors"
                      >
                        Edit
                      </button>
                      <button 
                        onClick={() => handleDelete(product.id)} 
                        className="cursor-pointer text-white font-medium bg-red-500 hover:bg-red-600 px-4 py-2 w-full md:w-auto rounded-lg transition-colors"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        <ProductForm 
          formaData={formaData} 
          setFormaData={setFormaData}
          editId={editId}
          setEditId={setEditId}
          setShowAddEditPage={setShowAddEditPage}
          fetchProducts={fetchProducts}
        />
      )}
    </div>
  );
}

export default Products;