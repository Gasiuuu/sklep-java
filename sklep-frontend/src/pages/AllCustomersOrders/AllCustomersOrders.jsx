import React, { useEffect, useState } from 'react';
import './AllCustomersOrders.css';
import OrderService from "../../services/OrderService.js";
import UserService from '../../services/UserService.js';
import ProductsToOrder from '../../Classes/ProductsToOrder.js';

function AllCustomersOrders() {
    const [orderList, setOrderList] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');





    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const token = sessionStorage.getItem('token');
                const ordersdata = await OrderService.getAllCustomersOrders(token);
                // setOrdersData(data);
                console.log('ordersData:');
                console.log(ordersdata.ordersEntityList);
                setOrderList(ordersdata.ordersEntityList)

            } catch (error) {
                console.error('Błąd podczas ładowania zamówień:', error);
            }
        };

        fetchOrders();


    }, []);


    return (
        <div className="home-container">

            <div className="top-content-container">
                <h1 className="title">Zamówienia wszystkich klientów</h1>

                <input
                    type="text"
                    placeholder="Wyszukaj produkt..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="search-bar"
                />
            </div>
            <div className="productsContainer">
                {orderList.map((order, index0) => (
                    <div className="product-container" key={order.id}>
                        <div className="offer-product-container" key={order.id}>
                            <div className="orders-user-details" key={order.id}>
                                <p style={{marginTop:"5px",marginBottom:"5px"}}>Nr zamówienia {index0 + 1}</p>
                                <p style={{marginTop:"5px",marginBottom:"5px"}}> Użytkownik {order.ourUser.username}</p>
                                <p style={{marginTop:"5px",marginBottom:"5px"}}>ID użytkownika: {order.ourUser.id}</p>
                            </div>

                            {order.productsAndNumbers.map((item, index) => (
                                <div className="productItem-container" key={index}>
                                    <div className="productItem">
                                        <img src={item.product.imageUrl} alt=""/>
                                        <p>{item.product.name}</p>
                                        <p>Ilość: {item.number}</p>
                                    </div>
                                </div>
                            ))}

                        </div>

                    </div>
                ))}


            </div>
        </div>
    );
}

export default AllCustomersOrders;
