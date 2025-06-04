import { useEffect, useState } from "react";
import {
  getTransactionsBySourceAccount,
  getTransactionsByDestinationAccount,
  getTransactionsBetweenDates,
} from "../services/transactionService";
import "../styles/TransactionHistory.css";

export default function TransactionHistory({ accountId, accounts = []}) {
    const [transactions, setTransactions] = useState([]);
    const [filterType, setFilterType] = useState("source");
    const [desde, setDesde] = useState("");
    const [hasta, setHasta] = useState("");
    const [error, setError] = useState(null);

    const fetchTransactions = async () => {
        try {
        setError(null);
        let data = [];
        if (filterType === "source") {
            data = await getTransactionsBySourceAccount(accountId);
        } else if (filterType === "destination") {
            data = await getTransactionsByDestinationAccount(accountId);
        } else if (filterType === "range" && desde && hasta) {
            data = await getTransactionsBetweenDates(accountId, desde, hasta);
        }
        setTransactions(data);
        } catch (err) {
        setError("No se pudieron obtener las transacciones.");
        setTransactions([]);
        }
    };

    useEffect(() => {
        if (accountId) fetchTransactions();
    }, [filterType]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (filterType === "range") {
        fetchTransactions();
        }
    };

    return (
        <div className="transaction-history">
        <h4>Historial de transacciones</h4>
        <form onSubmit={handleSubmit} className="transaction-filter-form">
            <label>Filtrar por:</label>
            <select value={filterType} onChange={(e) => setFilterType(e.target.value)}>
            <option value="source">Cuenta Origen</option>
            <option value="destination">Cuenta Destino</option>
            <option value="range">Rango de Fechas</option>
            </select>

            {filterType === "range" && (
            <>
                <label>Desde:</label>
                <input type="date" value={desde} onChange={(e) => setDesde(e.target.value)} required />
                <label>Hasta:</label>
                <input type="date" value={hasta} onChange={(e) => setHasta(e.target.value)} required />
                <button type="submit">Buscar</button>
            </>
            )}
        </form>

        {error && <p className="error-message">{error}</p>}

        <ul className="transaction-list">
            {transactions.map((tx) => {
                const originAccount = accounts.find(acc => acc.id === tx.originAccountId);
                const destinationAccount = accounts.find(acc => acc.id === tx.destinationAccountId);

                return (
                    <li key={tx.id} className="transaction-item">
                        <strong>{tx.type}</strong>
                        <span>Monto: ${tx.amount.toFixed(2)}</span>
                        <span>Origen: {originAccount?.accountNumber || tx.originAccountId}</span>
                        <span>Destino: {destinationAccount?.accountNumber || "INEXISTENTE" }</span>
                        <span>Fecha: {new Date(tx.timestamp * 1000).toLocaleString()}</span>
                        <span>Estado: {tx.state}</span>
                    </li>
                );
            })}
        </ul>
        </div>
    );
}
