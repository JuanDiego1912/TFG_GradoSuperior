const API_URL = 'http://localhost:8080/api/clientes';

export async function registerCustomer(customerData) {
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: customerData.name,
                lastName: customerData.lastName,
                dni: customerData.dni,
                email: customerData.email,
                phone: customerData.phone,
                password: customerData.password,
                state: customerData.state || 'ACTIVE',
            })
        });

        console.log(response);

        const contentType = response.headers.get('Content-Type');

        if (!response.ok) {
          const contentType = response.headers.get('Content-Type') || '';
          const errorMessage = contentType.includes("application/json")
            ? (await response.json()).message || 'Error al registrar'
            : await response.text();
          throw new Error(errorMessage);
        }

        return contentType.includes('application/json')
            ? await response.json()
            : await response.text();

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

    const contentType = response.headers.get("Content-Type") || "";
    const isJson = contentType.includes("application/json");

    const data = isJson ? await response.json() : await response.text();

    if (!response.ok) {
      const errorMessage = isJson
        ? data.message || "Credenciales inválidas"
        : data;
      throw new Error(errorMessage);
    }

    return data; // `data` ya contiene el `Customer`
  } catch (error) {
    console.error("Error en loginCustomer:", error);
    throw error;
  }
}

