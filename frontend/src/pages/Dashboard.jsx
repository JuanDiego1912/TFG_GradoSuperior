import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import axios from "axios";
import AddAccountForm from "./AddAccountForm";
import DepositForm from "../components/DepositForm";
import "../styles/Dashboard.css";

function Dashboard() {
  const { user } = useAuth();
  const [cliente, setCliente] = useState(null);
  const [cuentas, setCuentas] = useState([]);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [mostrarDeposito, setMostrarDeposito] = useState({});

  const fetchAccounts = async (clienteId) => {
    try {
      const res = await axios.get(`http://localhost:8080/api/cuentas/cliente/${clienteId}`);
      setCuentas(res.data);
    } catch {
      setError("Error al cargar cuentas.");
    }
  };

  useEffect(() => {
    const fetchCliente = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/api/clientes/email/${user.email}`);
        setCliente(res.data);
        fetchAccounts(res.data.id);
      } catch {
        setError("Error al obtener datos del cliente.");
      }
    };

    if (user?.email) {
      fetchCliente();
    }
  }, [user]);

  const saldoTotal = cuentas.reduce((acc, cuenta) => acc + cuenta.balance, 0);

  const toggleDeposito = (id) => {
    setMostrarDeposito((prev) => ({
      ...prev,
      [id]: !prev[id],
    }));
  };

  return (
    <div className="dashboard-layout">
      <div className="dashboard-left">
        <div className="dashboard-container">
          <h2>Bienvenido, {cliente?.nombre}</h2>

          <section className="cuentas-section">
            <h3>Tus cuentas</h3>
            {cuentas.length > 0 ? (
              <ul className="accounts-list">
                {cuentas.map((cuenta) => (
                  <li key={cuenta.id} className="account-item">
                    <strong>{cuenta.type}</strong>
                    <br />
                    N.º {cuenta.accountNumber}
                    <br />
                    Saldo: ${cuenta.balance.toFixed(2)}

                    <br />
                    <button
                        className="deposit-btn"
                        onClick={() => toggleDeposito(cuenta.id)}
                    >
                        {mostrarDeposito[cuenta.id] ? "Cancelar depósito" : "Añadir saldo"}
                    </button>

                    {mostrarDeposito[cuenta.id] && (
                      <DepositForm
                        accountId={cuenta.id}
                        onDepositSuccess={() => fetchAccounts(cliente.id)}
                      />
                    )}
                  </li>
                ))}
              </ul>
            ) : (
              <p>No tienes cuentas todavía.</p>
            )}

            <p className="total-saldo">
              <strong>Saldo total:</strong> ${saldoTotal.toFixed(2)}
            </p>

            <button className="toggle-form-button" onClick={() => setShowForm(!showForm)}>
              {showForm ? "Cancelar" : "Añadir nueva cuenta"}
            </button>

            {showForm && cliente?.id && (
              <AddAccountForm
                customerId={cliente.id}
                onAccountAdded={() => fetchAccounts(cliente.id)}
              />
            )}
          </section>

          {error && <div className="error-message">{error}</div>}
        </div>
      </div>

      <div className="dashboard-right">
        <div className="empty-space">
          <h3>Área adicional</h3>
          <p>Espacio reservado para movimientos, estadísticas u otras funciones.</p>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;