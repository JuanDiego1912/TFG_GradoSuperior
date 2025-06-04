import { useState } from "react";
import axios from "axios";
import { createTransaction } from "../services/transactionService";
import "../styles/TransferForm.css";

export default function TransferForm({ cuentas, onTransferSuccess }) {
  const [originId, setOriginId] = useState("");
  const [destinoNumero, setDestinoNumero] = useState("");
  const [monto, setMonto] = useState("");
  const [mensaje, setMensaje] = useState("");
  const [loading, setLoading] = useState(false);

  const handleTransfer = async (e) => {
    e.preventDefault();
    setMensaje("");
    setLoading(true);

    try {
        const { data: cuentaDestino } = await axios.get(
            `http://localhost:8080/api/cuentas/porNumero`,
            { params: { numero: destinoNumero } }
        );

        if (parseInt(originId) === cuentaDestino.id) {
            setMensaje("La cuenta destino no puede ser la misma que la origen.");
            return;
        }

        if (parseFloat(monto) <= 0) {
            setMensaje("El monto debe ser mayor a 0.");
            return;
        }

        const cuentaOrigen = cuentas.find((c) => c.id === parseInt(originId));
        if (cuentaOrigen && parseFloat(monto) > cuentaOrigen.balance) {
            setMensaje("Saldo insuficiente en la cuenta de origen.");
            return;
        }

        const nuevaTransaccion = {
            originAccountId: parseInt(originId),
            destinationAccountId: cuentaDestino.id,
            amount: parseFloat(monto),
            type: "TRANSFER",
            state: "PEND",
        };

        await createTransaction(nuevaTransaccion);

        setMensaje("Transferencia realizada correctamente.");
        setOriginId("");
        setDestinoNumero("");
        setMonto("");
        if (onTransferSuccess) onTransferSuccess();

        } catch (err) {
            console.error(err);
            setMensaje(err.message || "Error al procesar la transferencia.");
        } finally {
            setLoading(false);
        }
    };

  return (
    <form className="transfer-form" onSubmit={handleTransfer}>
        <h4>Transferencia</h4>

        <label>Desde tu cuenta:</label>
        <select
            value={originId}
            onChange={(e) => setOriginId(e.target.value)}
            required
        >
            <option value="">Selecciona una cuenta</option>
            {cuentas.map((cuenta) => (
                <option key={cuenta.id} value={cuenta.id}>
                    {cuenta.accountNumber} - ${typeof cuenta.balance === 'number' ? 
                    cuenta.balance.toFixed(2) : "0.00"}
                </option>
                ))}
        </select>

        <label>N.º cuenta destino:</label>
        <input
            type="text"
            value={destinoNumero}
            onChange={(e) => setDestinoNumero(e.target.value)}
            required
        />

        <label>Monto:</label>
        <input
            type="number"
            step="0.01"
            value={monto}
            onChange={(e) => setMonto(e.target.value)}
            required
        />

        <button type="submit" disabled={loading}>
            {loading ? "Procesando..." : "Transferir"}
        </button>

        {mensaje && (
            <p
                className="mensaje-transferencia"
                style={{ color: mensaje.includes("Error") ? "red" : "green" }}
            >
                {mensaje}
            </p>
        )}
    </form>
  );
}