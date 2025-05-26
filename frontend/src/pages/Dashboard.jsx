import { use, useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import axios from 'axios';

function Dashboard() {
    const { user } = useAuth();
    const [cliente, setCliente] = useState(null);
    const [cuentas, setCuentas] = useState([]);

    useEffect(() => {
        if (user) {
            axios.get('http://localhost:8080/api/clientes/email/${user.email}')
                .then(response => {
                    setCliente(response.data);
                    return axios.get(
                        `http://localhost:8080/api/cuentas/cliente/${response.data.id}`
                    );
                })
                .then(response => setCuentas(response.data))
                .catch(console.error);
        }
    }, [user]);

    return (
        <div>
            <h2>Hola, {cliente?.nombre}</h2>
            <h3>Tus cuentas:</h3>
            <ul>
                {cuentas.map(c => (
                    <li key={c.id}>
                        {c.tipo}: ${c.saldo}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default Dashboard;
// This code defines a Dashboard component that fetches and displays user 
// information and account details.