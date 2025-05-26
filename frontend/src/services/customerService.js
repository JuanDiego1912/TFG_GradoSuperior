const API_URL = 'http://localhost:8080/api/clientes';

export async function registerCustomer(customerData) {
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                id: customerData.id,
                name: customerData.name,
                last_name: customerData.last_name,
                dni: customerData.dni,
                email: customerData.email,
                phone: customerData.phone,
                password: customerData.password,
                creation_date: customerData.creation_date || Date.now(),
                state: customerData.state || 'ACTIVO',
            })
        });

        if (!response.ok) {
            throw new Error('Error al registrar el cliente');
        }

        return await response.json();
    } catch (error) {
        console.error('Error en el registro del cliente:', error);
        throw error;
    }
}

export async function loginCustomer(email, password) {
  try {
    const response = await fetch(`${API_URL}/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ email, password })
    });

    if (!response.ok) {
      throw new Error("Credenciales inválidas");
    }

    return await response.json();
  } catch (error) {
    console.error("Error en loginCustomer:", error);
    return null;
  }
}

export async function getCustomerById(id) {
  try {
    const response = await fetch(`${API_URL}/${id}`);
    if (!response.ok) throw new Error("Cliente no encontrado");
    return await response.json();
  } catch (error) {
    console.error("Error al obtener cliente:", error);
    return null;
  }
}


