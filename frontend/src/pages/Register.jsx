import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerCustomer } from "../services/customerService";
import "../styles/Register.css";

export default function Register() {
    const [formData, setFormData] = useState({
        id: "",
        name: "",
        last_name: "",
        dni: "",
        email: "",
        phone: "",
        password: "",
        creation_date: Date.now(),
        state: "ACTIVO",
    });
    const [ error, setError ] = useState(null);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        try {
            const response = await registerCustomer(formData);
            if (response) {
                navigate("/login");
            } else {
                setError("No se pudo registrar el cliente");
            }
        } catch (err) {
            setError("Error al registrar. Verifica los datos e inténtalo de nuevo.");
        }
    };
    
    return (
        <div className="register-container">
            <form onSubmit={handleSubmit} className="register-form">
                <h2 className="register-title">Registro de Cliente</h2>
                {error && <p className="register-error">{error}</p>}

                <div className="form-group">
                    <label htmlFor="id" className="form-label">ID</label>
                    <input
                        id="id"
                        name="id"
                        type="text"
                        value={formData.id}
                        onChange={handleChange}
                        className="form-input"
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="name" className="form-label">Nombre</label>
                    <input
                        id="name"
                        name="name"
                        type="text"
                        value={formData.name}
                        onChange={handleChange}
                        className="form-input"
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="last_name" className="form-label">Apellido</label>
                    <input
                        id="last_name"
                        name="last_name"
                        type="text"
                        value={formData.last_name}
                        onChange={handleChange}
                        className="form-input"
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="dni" className="form-label">DNI</label>
                    <input
                        id="dni"
                        name="dni"
                        type="text"
                        value={formData.dni}
                        onChange={handleChange}
                        className="form-input"
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="email" className="form-label">Correo electrónico</label>
                    <input
                        id="email"
                        name="email"
                        type="email"
                        value={formData.email}
                        onChange={handleChange}
                        className="form-input"
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="phone" className="form-label">Teléfono</label>
                    <input
                        id="phone"
                        name="phone"
                        type="text"
                        value={formData.phone}
                        onChange={handleChange}
                        className="form-input"
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="password" className="form-label">Contraseña</label>
                    <input
                        id="password"
                        name="password"
                        type="password"
                        value={formData.password}
                        onChange={handleChange}
                        className="form-input"
                        required
                    />
                </div>

                <button type="submit" className="submit-button">
                    Registrarse
                </button>
            </form>
        </div>
    );
}