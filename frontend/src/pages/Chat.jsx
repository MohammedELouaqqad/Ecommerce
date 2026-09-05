// src/pages/Chat.jsx
import { useState, useEffect, useRef } from "react";
import Sidebar from "../components/Sidebar";
import { BsSendFill } from "react-icons/bs";
import { sendChatMessage } from "../services/chatService"; // Notre service !

function Chat() {
  // State de l'input
  const [input, setInput] = useState("");
  // State de l'historique du chat (tableau d'objets)
  const [messages, setMessages] = useState([]);
  // State de chargement
  const [isLoading, setIsLoading] = useState(false);

  // Référence pour faire défiler le chat vers le bas automatiquement
  const messagesEndRef = useRef(null);

  // Faire défiler vers le bas à chaque nouveau message
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isLoading]); // Déclenche le scroll quand les messages ou le loading changent

  const handleSendMessage = async (e) => {
    // Permet d'utiliser la touche "Entrée" du clavier
    if (e) e.preventDefault(); 
    
    // Si le message est vide ou qu'on est déjà en train de charger, on ne fait rien
    if (!input.trim() || isLoading) return;

    const userMessage = input;
    
    // 1. On ajoute le message de l'utilisateur à l'historique
    setMessages(prev => [...prev, { sender: "user", text: userMessage }]);
    setInput(""); // On vide l'input
    setIsLoading(true); // On lance le chargement

    try {
      // 2. On appelle le service
      const aiResponse = await sendChatMessage(userMessage);
      
      // 3. On ajoute la réponse de l'IA à l'historique
      // (Adapte "aiResponse" si ton backend renvoie un objet, ex: aiResponse.message)
      setMessages(prev => [...prev, { sender: "ai", text: aiResponse }]);
      
    } catch (error) {
      setMessages(prev => [...prev, { sender: "ai", text: "Sorry, I couldn't process your request." }]);
    } finally {
      setIsLoading(false); // On arrête le chargement
    }
  };

  return (
    <div className="flex flex-col md:flex-row w-full min-h-screen bg-background">
      <Sidebar />
      
      <div className="flex flex-col h-screen md:w-full p-4 md:p-8">
        <h1 className="font-bold text-xl border-2 border-primary text-primary rounded-xl h-16 w-full flex items-center justify-center bg-primary/10">
          Chat with AI Assistant
        </h1>
        
        {/* Zone des messages (défilement) */}
        <div className="flex-1 overflow-y-auto bg-white shadow-inner rounded-2xl p-6 mt-4 flex flex-col gap-4">
          {messages.map((msg, index) => (
            <div key={index} className={`flex ${msg.sender === "user" ? "justify-end" : "justify-start"}`}>
              <div className={`break-words max-w-[80%] md:max-w-[60%] rounded-2xl p-4 ${
                msg.sender === "user" 
                  ? "bg-primary text-white rounded-br-none" 
                  : "bg-gray-100 text-gray-800 rounded-bl-none"
              }`}>
                {msg.text}
              </div>
            </div>
          ))}

          {/* Indicateur de saisie de l'IA */}
          {isLoading && (
            <div className="flex justify-start">
              <div className="bg-gray-100 text-gray-800 rounded-2xl rounded-bl-none p-4 italic">
                AI is typing...
              </div>
            </div>
          )}
          
          {/* Div invisible pour le scroll automatique */}
          <div ref={messagesEndRef} />
        </div>

        {/* Zone de saisie (Formulaire) */}
        <form onSubmit={handleSendMessage} className="flex mt-4 gap-4">
          <input 
            value={input} 
            onChange={(e) => setInput(e.target.value)} 
            className="bg-white shadow-md rounded-full flex-1 p-4 px-6 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-primary" 
            type="text" 
            placeholder="How can I help you today?"
            disabled={isLoading}
          />
          <button 
            type="submit" 
            disabled={isLoading || !input.trim()}
            className="cursor-pointer rounded-full w-14 p-3 bg-primary text-white hover:bg-primary-hover transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
          >
            <BsSendFill className="text-xl" />
          </button>             
        </form>
      </div>
    </div>
  );
}

export default Chat;