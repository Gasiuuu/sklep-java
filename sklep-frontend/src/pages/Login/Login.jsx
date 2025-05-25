import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// import UserService from '../../services/UserService';
import AuthService from '../../services/AuthService';
import './Login.css';

function Login() {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const userData = await AuthService.login(email, password);
            console.log(userData.token);
            if (userData.token) {
                sessionStorage.setItem('token', userData.token);
                sessionStorage.setItem('role', userData.role);
                navigate('/strona-glowna');
                window.location.reload();
            } else {
                setError(userData.error);
            }
        } catch (error) {
            console.error(error);
            setError(error.message || 'An error occurred');
            setTimeout(() => {
                setError('');
            }, 5000);
        }
    };

    return (
        <div className="login-container">
            <h1>Logowanie</h1>
            <form onSubmit={handleSubmit} className="login-form">
                <div className="login-form-group">
                    <input
                        type="email"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="Adres e-mail"
                        required
                    />
                </div>
                <div className="login-form-group">
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Hasło"
                        required
                    />
                </div>
                {error && <p className="login-error-message">{error}</p>}
                <button type="submit" className="login-submit-button">Zaloguj się</button>
            </form>
        </div>
    );
}

export default Login;
