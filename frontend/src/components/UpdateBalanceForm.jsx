import { useState, useEffect } from "react";
import { updateAccountBalance, getAccountsByCustomerId } from "../services/accountService";
import "../styles/UpdateBalanceForm.css";

function UpdateBalanceForm({ clienteId, onUpdateSuccess }) {
  const [cuentas, setCuentas] = useState([]);
  const [selectedAccountId, setSelectedAccountId] = useState("");
  const [newBalance, setNewBalance] = useState("");
  const [mensaje, setMensaje] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (clienteId) {
      getAccountsByCustomerId(clienteId)
        .then(setCuentas)
        .catch(() => setError("No se pudieron cargar las cuentas."));
    }
  }, [clienteId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMensaje(null);
    setError(null);
    setLoading(true);

    if (!selectedAccountId || isNaN(newBalance)) {
      setError("Selecciona una cuenta e ingresa un saldo válido.");
      setLoading(false);
      return;
    }

    try {
      await updateAccountBalance(parseInt(selectedAccountId), parseFloat(newBalance));
      setMensaje("Saldo actualizado correctamente.");
      setNewBalance("");
      onUpdateSuccess?.();
    } catch (err) {
      setError("Error al actualizar el saldo.");
    } finally {
      setLoading(false);
      setTimeout(() => setMensaje(null), 3000);
    }
  };

  return (
    <form className="update-balance-form" onSubmit={handleSubmit}>
      <h3>Modificar saldo</h3>

      {mensaje && <p className="success">{mensaje}</p>}
      {error && <p className="error">{error}</p>}

      <label>Seleccionar cuenta:</label>
      <select
        value={selectedAccountId}
        onChange={(e) => setSelectedAccountId(e.target.value)}
        required
      >
        <option value="">-- Selecciona una cuenta --</option>
        {cuentas.map((cuenta) => (
          <option key={cuenta.id} value={cuenta.id}>
            {cuenta.accountNumber} ({cuenta.type})
          </option>
        ))}
      </select>

      <label>Nuevo saldo:</label>
      <input
        type="number"
        step="0.01"
        min="0"
        value={newBalance}
        onChange={(e) => setNewBalance(e.target.value)}
        required
      />

      <button type="submit" disabled={loading}>
        {loading ? "Actualizando..." : "Actualizar saldo"}
      </button>
    </form>
  );
}

export default UpdateBalanceForm;