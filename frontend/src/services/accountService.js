import axios from 'axios';

const API_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

/**
 * Crea una nueva cuenta bancaria
 * @param {number} customerId - ID del cliente
 * @param {string} type - Tipo de cuenta (por defecto 'SAVINGS')
 * @param {number} balance - Saldo inicial (por defecto 0.0)
 */
export const createAccount = async (customerId, type = "SAVINGS", balance = 0.0) => {
  try {
    const response = await axios.post(API_URL, {
      customerId,
      accountType: type,
      balance: parseFloat(balance)
    });
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data || 'Error al crear la cuenta');
  }
};

/**
 * Obtiene todas las cuentas de un cliente por su ID
 * @param {number} customerId 
 */
export const getAccountsByCustomerId = async (customerId) => {
  try {
    const response = await axios.get(`${API_URL}/cliente/${customerId}`);
    return response.data;
  } catch (error) {
    throw new Error('Error al obtener las cuentas del cliente');
  }
};

/**
 * Obtiene una cuenta por su ID
 * @param {number} accountId 
 */
export const getAccountById = async (accountId) => {
  try {
    const response = await axios.get(`${API_URL}/${accountId}`);
    return response.data;
  } catch (error) {
    throw new Error('Error al obtener la cuenta');
  }
};

/**
 * Actualiza el saldo de una cuenta específica
 * @param {number} accountId 
 * @param {number} newBalance 
 */
export const updateAccountBalance = async (accountId, newBalance) => {
  try {
    const account = await getAccountById(accountId);

    const payload = {
      id: account.id,
      balance: newBalance,
      accountType: account.accountType,     // necesario para evitar NullPointerException
      customerId: account.customerId,       // backend espera customerId
      accountNumber: account.accountNumber  // necesario para mantener integridad
    };

    const response = await axios.put(API_URL, payload);
    return response.data;
  } catch (error) {
    throw new Error("Error actualizando el saldo de la cuenta: " + (error.response?.data || error.message));
  }
};

/**
 * Realiza un depósito (aumenta el saldo) en una cuenta
 * @param {number} accountId 
 * @param {number} amount 
 */
export const depositToAccount = async (accountId, amount) => {
  const account = await getAccountById(accountId);
  const newBalance = account.balance + parseFloat(amount);
  return await updateAccountBalance(accountId, newBalance);
};

/**
 * Elimina una cuenta específica de un cliente
 * @param {number} clienteId 
 * @param {number} cuentaId 
 */
export const deleteAccountForClient = async (clienteId, cuentaId) => {
  try {
    const response = await axios.delete(`${API_URL}/cliente/${clienteId}/cuenta/${cuentaId}`);
    
    if (response.status === 200) {
      return true;
    } else {
      throw new Error("Respuesta inesperada del servidor");
    }
  } catch (error) {
    console.error("Detalle del error al eliminar cuenta:", error.response?.data || error.message);
    throw new Error(error.response?.data || "Error al eliminar la cuenta");
  }
};