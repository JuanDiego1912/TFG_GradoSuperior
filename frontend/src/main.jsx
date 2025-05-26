import React from "react";
import ReactDOM from "react-dom/client";
import AppRouter from './routes/AppRouter';
import './styles/index.css';
REVISAR QUE EL AUTHCONTEXT SEA CORRECTO.
import { AuthProvider } from './context/AuthContext';

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <AuthProvider>
      <AppRouter />
    </AuthProvider>
  </React.StrictMode>
);
