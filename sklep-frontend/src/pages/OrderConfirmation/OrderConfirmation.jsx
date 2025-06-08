import React from 'react';
import { Link } from 'react-router-dom';
import './OrderConfirmation.css'
import { FaBasketShopping } from "react-icons/fa6";

function OrderConfirmation() {


    const createDeliverDate = () => {
        const date = new Date();
        date.setDate(date.getDate() + 2);
        const day = date.getDate();
        const month =  date.getMonth() + 1;
        const year = date.getFullYear();

        const deliveryDate = `${day < 10 ? '0' + day : day}-${month < 10 ? '0' + month : month}-${year}`;

        return deliveryDate;
    }

    const deliveryDate = createDeliverDate();



    return (
        <div className="home-container">
            <div className="order-notification">
                <FaBasketShopping className="order-notification-icon" />
                <h1 className="order-not-title">Dziękujemy za zakupy na Alledrogo!</h1>
                <h3 className="order-not-deliveryDate">Przewidywana dostawa: {deliveryDate} </h3>
                <Link to='/myOrders'>
                    <button className="add-btn">Zobacz zamówienia</button>
                </Link>
            </div>

        </div>
    )
}

export default OrderConfirmation;