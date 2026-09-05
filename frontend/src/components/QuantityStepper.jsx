// src/components/QuantityStepper.jsx
import { IoAddCircle } from "react-icons/io5";
import { LuCircleMinus } from "react-icons/lu";

// Ce composant est "bête", il ne fait qu'afficher et déléguer le clic au parent
const QuantityStepper = ({ quantity, onIncrease, onDecrease }) => {
  return (
    <div className="flex items-center justify-center gap-3">
      <button 
        onClick={onDecrease} 
        className="text-gray-500 hover:text-primary text-xl transition-colors"
        aria-label="Decrease quantity"
      >
        <LuCircleMinus />
      </button>
      <span className="font-bold w-8 text-center">{quantity}</span>
      <button 
        onClick={onIncrease} 
        className="text-gray-500 hover:text-primary text-xl transition-colors"
        aria-label="Increase quantity"
      >
        <IoAddCircle />
      </button>
    </div>
  );
};

export default QuantityStepper;