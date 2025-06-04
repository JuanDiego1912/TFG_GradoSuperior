import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import axios from "axios";
import AddAccountForm from "./AddAccountForm";
import DepositForm from "../components/DepositForm";
import UpdateBalanceForm from "../components/UpdateBalanceForm";
import TransferForm from "../components/TransferForm";
import TransactionHistory from "../components/TransactionHistory";
import { deleteAccountForClient } from "../services/accountService";
import "../styles/Dashboard.css";

function Dashboard() {
  const { user } = useAuth();
  const [cliente, setCliente] = useState(null);
  const [cuentas, setCuentas] = useState([]);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [successMessage, setSuccessMessage] = useState(null);
  const [mostrarDeposito, setMostrarDeposito] = useState({});
  const [mostrarActualizarSaldo, setMostrarActualizarSaldo] = useState(false);
  const [mostrarTransferencia, setMostrarTransferencia] = useState(false);

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

  const handleDeleteAccount = async (cuentaId) => {
    const confirmar = window.confirm("¿Estás seguro de que quieres eliminar esta cuenta?");
    if (!confirmar) return;

     try {
      await deleteAccountForClient(cliente.id, cuentaId);
      await fetchAccounts(cliente.id);
      setSuccessMessage("Cuenta eliminada correctamente.");
      setTimeout(() => {
        setSuccessMessage(null);
      }, 3000);
    } catch (err) {
      setError("Error al eliminar la cuenta.");
    }
  };

  return (
    <div className="dashboard-layout">
      <div className="dashboard-left">
        <div className="dashboard-container">
          <h2>Bienvenido, {cliente?.name}</h2>

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

                    <div className="account-actions">
                      <button
                        className="deposit-btn"
                        onClick={() => toggleDeposito(cuenta.id)}
                      >
                        {mostrarDeposito[cuenta.id] ? "Cancelar depósito" : "Añadir saldo"}
                      </button>

                      <button
                        className="delete-btn"
                        onClick={() => handleDeleteAccount(cuenta.id)}
                      >
                        Eliminar cuenta
                      </button>
                    </div>

                    {mostrarDeposito[cuenta.id] && (
                      <DepositForm
                        accountId={cuenta.id}
                        onDepositSuccess={() => fetchAccounts(cliente.id)}
                      />
                    )}

                    <TransactionHistory accountId={cuenta.id} accounts={cuentas}/>
                  </li>
                ))}
              </ul>
            ) : (
              <p>No tienes cuentas todavía.</p>
            )}

            <p className="total-saldo">
              <strong>Saldo total:</strong> ${saldoTotal.toFixed(2)}
            </p>

            {successMessage && (
              <div className="success-message">{successMessage}</div>
            )}

            <button
              className="toggle-form-button"
              onClick={() => setShowForm(!showForm)}
            >
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

          <button
            className="toggle-form-button"
            onClick={() => setMostrarActualizarSaldo(!mostrarActualizarSaldo)}
          >
            {mostrarActualizarSaldo ? "Cancelar" : "Modificar saldo"}
          </button>

          <button
            className="toggle-form-button"
            onClick={() => setMostrarTransferencia(!mostrarTransferencia)}
          >
            {mostrarTransferencia ? "Cancelar transferencia" : "Transferir dinero"}
          </button>

          {mostrarActualizarSaldo && (
            <UpdateBalanceForm
              clienteId={cliente?.id}
              onUpdateSuccess={() => fetchAccounts(cliente.id)}
            />
          )}

          {mostrarTransferencia && (
            <TransferForm
              cuentas={cuentas}
              onTransferSuccess={() => fetchAccounts(cliente.id)}
            />
          )}
        </div>
      </div>
    </div>
  );
}

export default Dashboard;