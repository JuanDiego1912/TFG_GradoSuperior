import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerCustomer } from "../services/customerService";
import "../styles/Register.css";

export default function Register() {
    const [formData, setFormData] = useState({
        name: "",
        lastName: "",
        dni: "",
        email: "",
        phone: "",
        password: "",
        state: "ACTIVE",
    });

    const [ error, setError ] = useState(null);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        if (!/^([0-9]{8}[A-Za-z]|[XYZ][0-9]{7}[A-Za-z])$/.test(formData.dni)) {
            setError("Formato de DNI o NIE inválido");
            return;
        }

        if (!formData.email.includes('@')) {
            setError("Correo electrónico inválido");
            return;
        }
        
        try {
            const response = await registerCustomer(formData);
            if (response) {
                navigate("/login");
            } else {
                setError("No se pudo registrar el cliente");
            }
        } catch (err) {
            console.error("Error al registrar el cliente:", err);
            setError("Error al registrar. Verifica los datos e inténtalo de nuevo.");
        }
    };
    
     return (
        <div className="register-container">
            <form onSubmit={handleSubmit} className="register-form">
                <h2 className="register-title">Registro de Cliente</h2>
                {error && <p className="register-error">{error}</p>}

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
                    <label htmlFor="lastName" className="form-label">Apellido</label>
                    <input
                        id="lastName"
                        name="lastName"
                        type="text"
                        value={formData.lastName}
                        onChange={handleChange}
                        className="form-input"
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="dni" className="form-label">DNI/NIE/Pasaporte</label>
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

                <button type="submit" className="submit-button">Registrarse</button>
            </form>
        </div>
    );
}