import { useEffect, useState } from "react";
import { useAuth } from '../context/AuthContext';
import axios from 'axios';
import AddAccountForm from "./AddAccountForm";
import "../styles/Dashboard.css";

function Dashboard() {
    const { user } = useAuth();
    const [cliente, setCliente] = useState(null);
    const [cuentas, setCuentas] = useState([]);
    const [error, setError] = useState(null);

    const fetchAccounts = (id) => {
        axios
            .get(`http://localhost:8080/api/cuentas/cliente/${id}`)
            .then((res) => setCuentas(res.data))
            .catch(console.error);
    };

   useEffect(() => {
    if (user) {
        axios
            .get(`http://localhost:8080/api/clientes/email/${user.email}`)
            .then((res) => {
                setCliente(res.data);
                fetchAccounts(res.data.id);
            })
            .catch(console.error);
        }
    }, [user]);

    const totalSaldo = cuentas.reduce((acc, cuenta) => acc + cuenta.balance, 0);

    const fechaFormateada = (timestamp) => {
        if (!timestamp) return "";
        const date = new Date(timestamp);
        return date.toLocaleDateString() + " " + date.toLocaleTimeString();
    };

    const saldoTotal = cuentas.reduce((total, cuenta) => total + cuenta.balance, 0);

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="dashboard-container">
            <h2>Bienvenido, {cliente?.nombre}</h2>

            <section className="cuentas-section">
                <h3>Tus cuentas</h3>
                {cuentas.length > 0 ? (
                    <ul>
                        {cuentas.map((cuenta) => (
                            <li key={cuenta.id} className="cuenta-card">
                                <strong>{cuenta.type}</strong> - N.º {cuenta.accountNumber}
                                <br />
                                Saldo: ${cuenta.balance.toFixed(2)}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No tienes cuentas todavía.</p>
                )}

                {cuentas.length > 0 && (
                    <p className="total-saldo">
                        <strong>Saldo total:</strong> ${totalSaldo.toFixed(2)}
                    </p>
                )}
            </section>

            <AddAccountForm
                customerId={cliente?.id}
            onAccountAdded={() => fetchAccounts(cliente.id)}
            />
        </div>
    );
}

export default Dashboard;
// This code defines a Dashboard component that fetches and displays user 
// information and account details.