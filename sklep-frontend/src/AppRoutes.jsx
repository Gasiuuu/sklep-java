import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'

import Navbar from './components/Navbar/Navbar.jsx'
import Home from './pages/Home/Home.jsx'
import Login from './pages/Login/Login.jsx'
import Register from './pages/Register/Register.jsx'
import ProfilePage from './pages/ProfilePage/ProfilePage.jsx'
import ProductPage from './pages/ProductPage/ProductPage.jsx'
import UserPage from './pages/UserPage/UserPage.jsx'
import UserService from "./services/UserService.js";
import AuthService from "./services/AuthService.js"



function AppRoutes() {

    const renderLayout = (component) => (

        <div className="App">

            <div className="navbar">
                <Navbar />
            </div>
            <div className="content-wrapper">
                {component}
            </div>

        </div>

    );

    return (<Router>
        <Routes>
            <Route path="/" element={<Navigate to="strona-glowna" />} />
            <Route path="strona-glowna" element={renderLayout(<Home />)} />
            <Route path="logowanie" element={renderLayout(<Login />)} />
            <Route path="rejestracja" element={renderLayout(<Register />)} />
            <Route path="product/:productId" element={renderLayout(<ProductPage />)} />
            {AuthService.isAuthenticated() && (<>
                <Route path="profil" element={renderLayout(<ProfilePage />)} />
            </>)}
            {AuthService.adminOnly() && (
                <>
                    <Route path="/admin/user-page" element={renderLayout(<UserPage />)} />

                </>
            )}
        </Routes>
    </Router>)
}

export default AppRoutes;