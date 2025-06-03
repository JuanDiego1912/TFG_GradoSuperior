import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "../styles/Navbar.css";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();             
    navigate("/login"); 
  };

  return (
    <nav className="navbar">
      <div className="nav-brand">MiBanco</div>
      <div className="nav-links">
        <Link to="/">Inicio</Link>
        {!user ? (
          <>
            <Link to="/login">Iniciar Sesión</Link>
            <Link to="/register">Registrarse</Link>
          </>
        ) : (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <button onClick={handleLogout} className="logout-button">
              Cerrar sesión
            </button>
          </>
        )}
      </div>
    </nav>
  );
}