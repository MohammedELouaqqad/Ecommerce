// src/components/ProductForm.jsx
import { useState } from 'react';
import Input from './Input'; // Our reusable input
import { addProduct, updateProduct, uploadImage } from '../services/productService';

const ProductForm = ({ formaData, setFormaData, editId, setEditId, setShowAddEditPage, fetchProducts }) => {
  const [image, setImage] = useState(null);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormaData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      let finalFormData = { ...formaData };

      // 1. If a new image is selected, upload it first
      if (image) {
        const fileName = await uploadImage(image);
        finalFormData.filename = fileName;
      }

      // 2. Add or Update the product
      if (editId) {
        await updateProduct(editId, finalFormData);
      } else {
        await addProduct(finalFormData);
      }

      // 3. Reset state and go back to table
      setFormaData({ name: "", description: "", price: 0, sku: "", sizes: "", countStock: 0, filename: "" });
      setEditId("");
      setShowAddEditPage(false);
      fetchProducts(); // Refresh the table
      
    } catch (error) {
      alert("An error occurred while saving the product.");
      console.error(error);
    }
  };

  return (
    <div className="shadow-lg rounded-lg p-8 h-full w-full md:w-200 mt-14 md:ml-14 bg-white">
      <h1 className="font-bold text-xl mb-6">{editId ? "UPDATE" : "ADD"} Product</h1>
      <form onSubmit={handleSubmit}>
        {/* Using our clean reusable Input component! */}
        <Input label="Product Name" name="name" value={formaData.name} onChange={handleInputChange} />
        
        <div className="flex flex-col mt-4">
          <label className="font-medium text-gray-700 mb-1">Description</label>
          <textarea 
            name="description" 
            value={formaData.description} 
            onChange={handleInputChange} 
            className="border rounded-lg h-26 px-4 py-2 border-gray-300 focus:border-blue-500 outline-none"
          />
        </div>

        <Input label="Price" name="price" type="number" value={formaData.price} onChange={handleInputChange} />
        <Input label="Count in Stock" name="countStock" type="number" value={formaData.countStock} onChange={handleInputChange} />
        <Input label="SKU" name="sku" value={formaData.sku} onChange={handleInputChange} />
        <Input label="Sizes" name="sizes" value={formaData.sizes} onChange={handleInputChange} />

        <div className="flex flex-col mt-4">
          <label className="font-medium text-gray-700 mb-1">Upload Image</label>
          <input 
            onChange={(e) => setImage(e.target.files[0])} 
            className="bg-gray-100 border-2 rounded-lg h-12 text-center p-2 border-gray-300 w-60 cursor-pointer file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100" 
            type="file" 
          />
          {formaData.filename && (
            <img 
              alt="Product preview" 
              src={`http://localhost:8080/api/customer/download/${formaData.filename}`} 
              className="mt-5 m-auto w-32 h-32 object-cover border-2 border-gray-200 rounded-lg"
            />
          )}
        </div>

        <button 
          type="submit" 
          className="cursor-pointer hover:bg-green-500 bg-green-600 text-white rounded-lg p-3 mt-10 w-70 font-bold transition-colors"
        >
          {editId ? "UPDATE" : "ADD"} PRODUCT
        </button>
      </form>
    </div>
  );
};

export default ProductForm;