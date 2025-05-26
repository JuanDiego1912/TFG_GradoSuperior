const API_URL = 'http://localhost:8080/api/cuentas';

export const getAccountsByCustomerId = async (customerId) => {
    const response = await fetch(`${API_URL}/cliente/${customerId}`);
    if (!response.ok) throw new Error('Error al obtener cuentas');
    return await response.json();
}