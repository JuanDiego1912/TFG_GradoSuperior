import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Welcome from '../pages/Welcome';
import Login from '../pages/Login';
import Register from '../pages/Register';
import Dashboard from '../pages/Dashboard';
import { useAuth } from '../context/AuthContext';

function PrivateRoute({ children }) {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" />;
}

function AppRouter() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Welcome />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/dashboard" element={
                    <PrivateRoute>
                        <Dashboard />
                    </PrivateRoute>
                } />
            </Routes>
        </BrowserRouter>
    );
}

export default AppRouter;
// This code sets up a React Router for the application, 
// defining routes for the welcome page, login, registration, 
// and a private dashboard that requires authentication. 
// The `PrivateRoute` component checks if a user is authenticated before 
// allowing access to the dashboard. If not authenticated, it redirects to the login page.