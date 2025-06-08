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
import AddProduct from "./pages/AddProduct/AddProduct.jsx";
import AdminProductPage from "./pages/AdminProductPage/AdminProductPage.jsx";
import MyOrders from "./pages/MyOrders/MyOrders.jsx";
import Basket from "./pages/Basket/Basket.jsx";
import OrderConfirmation from "./pages/OrderConfirmation/OrderConfirmation.jsx";
import AllCustomersOrders from "./pages/AllCustomersOrders/AllCustomersOrders.jsx";



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
                <Route path="/basket" element={renderLayout(<Basket />)} />
                <Route path="/myOrders" element={renderLayout(<MyOrders />)} />
                <Route path="/order-confirmation" element={renderLayout(<OrderConfirmation />)} />
            </>)}
            {AuthService.adminOnly() && (
                <>
                    <Route path="/admin/user-page" element={renderLayout(<UserPage />)} />
                    <Route path="/admin/addProduct" element={renderLayout(<AddProduct />)} />
                    <Route path="/admin/admin-product-page" element={renderLayout(<AdminProductPage />)} />
                    <Route path="/admin/allCustomersOrders" element={renderLayout(<AllCustomersOrders />)} />
                </>
            )}
        </Routes>
    </Router>)
}

export default AppRoutes;