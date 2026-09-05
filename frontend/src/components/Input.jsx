// src/components/Input.jsx
import React from 'react';

const Input = ({ label, type = "text", value, onChange, ...props }) => {
  return (
    <div className="flex flex-col mt-4">
      <label className="font-medium text-gray-700 mb-1">{label}</label>
      <input 
        type={type} 
        value={value || ''} 
        onChange={onChange} 
        className="border rounded-lg h-12 px-4 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 outline-none transition-all"
        {...props}
      />
    </div>
  );
};

export default Input;