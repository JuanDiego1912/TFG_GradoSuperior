import { Link } from 'react-router-dom';

function Welcome() {
    return (
        <div>
            <h1>Bienvenido a un Banco simulado</h1>
            <p>Noticias bancarias del día...</p>
            <Link to="/login">Iniciar sesión</Link> | <Link to="/register">Registrarse</Link>
        </div>
    );
}

export default Welcome;
// This code defines a simple Welcome page for a simulated banking application.