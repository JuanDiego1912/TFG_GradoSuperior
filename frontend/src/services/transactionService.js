const API_URL = 'http://localhost:8080/api/transacciones';

export const getTransactionsByAccountId = async (accountId) => {
    const response = await fetch(`${API_URL}/cuenta/${accountId}`);
    if (!response.ok) throw new Error('Error al obtener transacciones');
    return await response.json();
}