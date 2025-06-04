const API_URL = 'http://localhost:8080/api/transacciones';

export const getTransactionsByAccountId = async (accountId) => {
    const response = await fetch(`${API_URL}/cuenta/${accountId}`);
    if (!response.ok) throw new Error('Error al obtener transacciones');
    return await response.json();
};

export const createTransaction = async (transactionData) => {
    const response = await fetch(`${API_URL}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(transactionData),
    });

    const contentType = response.headers.get("content-type");

    if (!response.ok) {
        // Si es JSON, devuelve el mensaje del backend
        if (contentType && contentType.includes("application/json")) {
            const errorData = await response.json();
            throw new Error(errorData.message || "Error al crear la transacción");
        } else {
             // Si no es JSON, solo lanza el texto plano
            const text = await response.text();
            throw new Error(text || "Error desconocido");
        }
    }

    // Solo intenta .json() si el contenido es realmente JSON
    if (contentType && contentType.includes("application/json")) {
        return await response.json();
    } else {
        return await response.text();
    }
};

export async function getTransactionById(id) {
  const response = await fetch(`${API_URL}/${id}`);
  if (!response.ok) {
    throw new Error(`No se pudo obtener la transacción con ID ${id}`);
  }
  return await response.json();
}

export async function getTransactionsBySourceAccount(accountId) {
  const response = await fetch(`${API_URL}/origen/${accountId}`);
  if (!response.ok) {
    throw new Error(`No se encontraron transacciones salientes para la cuenta ${accountId}`);
  }
  return await response.json();
}

export async function getTransactionsByDestinationAccount(accountId) {
  const response = await fetch(`${API_URL}/destino/${accountId}`);
  if (!response.ok) {
    throw new Error(`No se encontraron transacciones entrantes para la cuenta ${accountId}`);
  }
  return await response.json();
}

export async function getTransactionsBetweenDates(accountId, desde, hasta) {
  const params = new URLSearchParams({
    idCuenta: accountId,
    desde,
    hasta,
  });

  const response = await fetch(`${API_URL}/fechas?${params}`);
  if (!response.ok) {
    throw new Error(`No se encontraron transacciones entre las fechas proporcionadas`);
  }
  return await response.json();
}