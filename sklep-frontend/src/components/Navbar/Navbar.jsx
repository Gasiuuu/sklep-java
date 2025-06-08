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
                    <Link to="/admin/allCustomersOrders">
                        <button className="navbar-btn">Zamówienia klientów</button>
                    </Link>

                    <Link to="/admin/user-page">
                        <button className="navbar-btn">Panel użytkowników</button>
                    </Link>

                    <Link to="/admin/addProduct">
                        <button className="navbar-btn">Dodaj produkt</button>
                    </Link>

                    <Link to="/admin/admin-product-page">
                        <button className="navbar-btn">Panel produktów</button>
                    </Link>
                </>}

                {AuthService.isAuthenticated() && <>
                    <Link to="/basket">
                        <button className="navbar-btn">Koszyk</button>
                    </Link>
                    <Link to="/myOrders">
                        <button className="navbar-btn">Moje Zamówienia</button>
                    </Link>
                    <Link to="/profil">
                        <button className="navbar-btn">Profil</button>
                    </Link>
                    <button onClick={handleLogout} className="navbar-btn">Wyloguj się</button>
                </>}
            </div>
        </nav>
    );
}

export default Navbar;
