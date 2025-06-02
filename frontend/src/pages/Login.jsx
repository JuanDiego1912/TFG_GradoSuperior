import { useState } from "react";   
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { loginCustomer as loginService } from "../services/customerService";
import "../styles/Login.css";

export default function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        try {
            const customer = await loginService(email, password);
            if (!customer || !customer.id) {
                throw new Error("Credenciales inválidas");
            }

            login(customer);
            navigate("/dashboard");
        } catch (err) {
            console.error("Error en el inicio de sesión:", err);
            setError(err.message || "Error desconocido");
        }
    };

    return (
        <div className="login-container">
            <form onSubmit={handleSubmit} className="login-form">
                <h2 className="login-title">Iniciar Sesión</h2>
                {error && <p className="error-message">{error}</p>}

                <div className="form-group">
                    <label htmlFor="email" className="form-label">Correo Electrónico</label>
                    <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="form-input"
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="password" className="form-label">Contraseña</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="form-input"
                        required
                    />
                </div>

                <button type="submit" className="login-button">Iniciar Sesión</button>
            </form>
        </div>
    );
}
// This code defines a simple login form for a simulated banking application.
// It uses React hooks to manage the email state and handle form submission.
// When the form is submitted, it calls the `Login` function from the `AuthContext`