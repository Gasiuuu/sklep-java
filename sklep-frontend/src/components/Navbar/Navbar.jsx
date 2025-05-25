import React from 'react';
import './Navbar.css';
import {Link, useNavigate} from "react-router-dom";
import UserService from "../../services/UserService.js"
import AuthService from "../../services/AuthService.js"

function Navbar() {
    const navigate = useNavigate();

    const handleLogout=()=>{
        console.log("Logout")
        AuthService.logout()
        navigate('/strona-glowna');

    }
    return (
        <nav className="navbar">

            <Link className="logo-link" to="/">
                <div className="navbar-logo">
                    alledrogo
                </div>
            </Link>

            <div className="navbar-links">
                {!AuthService.isAuthenticated() && <><Link to="/logowanie">
                    <button className="navbar-btn">Zaloguj się</button>
                </Link>

                    <Link to="/rejestracja">
                        <button className="navbar-btn">Zarejestruj się</button>
                    </Link></>}

                {AuthService.isAdmin() && <>
                        <button className="navbar-btn">Zamówienia klientów</button>
                        <button className="navbar-btn">Panel użytkowników</button>
                        <button className="navbar-btn">Dodaj produkt</button>
                        <button className="navbar-btn">Panel produktów</button>
                </>}

                {AuthService.isAuthenticated() && <>
                        <button className="navbar-btn">Koszyk</button>
                        <button className="navbar-btn">Moje Zamówienia</button>
                        <button className="navbar-btn">Profil</button>
                    <button onClick={handleLogout} className="navbar-btn">Wyloguj się</button>
                </>}
            </div>
        </nav>
    );
}

export default Navbar;
