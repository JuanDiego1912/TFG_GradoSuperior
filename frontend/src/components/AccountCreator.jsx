import React, { useState } from 'react';
import { createAccount } from '../services/accountService';

const AccountCreator = ({ customerId, onAccountCreated }) => {
    const [accountType, setAccountType] = useState('CURRENT');
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');

    const handleCreate = async () => {
        setLoading(true);
        setMessage('');

        try {
            const response = await createAccount(customerId, accountType);
            setMessage(response);

            if (onAccountCreated) {
                onAccountCreated();
            }
        } catch (error) {
            setMessage(`Error: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ border: '1px solid #ccc', padding: '1rem', borderRadius: '8px', maxWidth: '300px' }}>
            <h3>Crear nueva cuenta</h3>

            <label htmlFor="tipoCuenta">Tipo de cuenta:</label>
            <select
                id="tipoCuenta"
                value={accountType}
                onChange={(e) => setAccountType(e.target.value)}
                disabled={loading}
                style={{ display: 'block', marginBottom: '1rem', width: '100%' }}
            >
                <option value="CURRENT">Corriente</option>
                <option value="SAVINGS">Ahorros</option>
            </select>

            <button onClick={handleCreate} disabled={loading} style={{ width: '100%' }}>
                {loading ? 'Creando...' : 'Crear cuenta'}
            </button>

            {message && (
                <p style={{ marginTop: '1rem', color: message.startsWith('Error') ? 'red' : 'green' }}>
                    {message}
                </p>
            )}
        </div>
    );
};

export default AccountCreator;