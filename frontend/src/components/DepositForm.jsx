import { useState } from "react";
import { depositToAccount } from '../services/accountService';
import "../styles/AddAccountForm.css";

export default function DepositForm({ accountId, onDepositSuccess }) {
    const [amount, setAmount] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccess(false);
        setLoading(true);

        try {
            await depositToAccount(accountId, amount);
            setSuccess(true);
            setAmount(0);
            if (onDepositSuccess) onDepositSuccess();
        } catch (err) {
            console.error("Error al depositar:", err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <form className="add-account-form" onSubmit={handleSubmit}>
            <h3>Depositar saldo</h3>

            {error && <p className="error">{error}</p>}
            {success && <p className="success">Depósito exitoso</p>}

            <label htmlFor="amount">Monto a depositar:</label>
            <input
                type="number"
                id="amount"
                min="0.01"
                step="0.01"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                required
            />

            <button type="submit" disabled={loading}>
                {loading ? "Depositando..." : "Depositar"}
            </button>
        </form>
    );
}