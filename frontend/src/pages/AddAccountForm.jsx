import { useState } from "react";
import { createAccount } from "../services/accountService";
import "../styles/AddAccountForm.css";

export default function AddAccountForm({ customerId, onAccountAdded }) {
    const [tipo, setTipo] = useState("SAVINGS");
    const [saldo, setSaldo] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccess(false);
        setLoading(true);

        try {
            await createAccount(customerId, tipo, saldo);
            setSuccess(true);
            setSaldo(0);
            setTipo("SAVINGS");
            if (onAccountAdded) onAccountAdded();
        } catch (err) {
            console.error("Error al crear cuenta:", err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <form className="add-account-form" onSubmit={handleSubmit}>
            <h3>Añadir nueva cuenta</h3>

            {error && <p className="error">{error}</p>}
            {success && <p className="success">Cuenta creada exitosamente</p>}

            <label htmlFor="tipo">Tipo de cuenta:</label>
            <select
                id="tipo"
                value={tipo}
                onChange={(e) => setTipo(e.target.value)}
            >
                <option value="SAVINGS">Ahorros</option>
                <option value="CURRENT">Corriente</option>
                <option value="PAYROLL">Nómina</option>
            </select>

            <label htmlFor="saldo">Saldo inicial:</label>
            <input
                type="number"
                id="saldo"
                min="0"
                step="0.01"
                value={saldo}
                onChange={(e) => setSaldo(e.target.value)}
                required
            />

            <button type="submit" disabled={loading}>
                {loading ? "Creando..." : "Crear cuenta"}
            </button>
        </form>
    );
}