import React, { useEffect, useState } from 'react';
import {Link, useNavigate} from 'react-router-dom';
import './Basket.css';
import OrderService from '../../services/OrderService.js';
import ProductsToOrder from '../../Classes/ProductsToOrder.js';
import { FaTrashAlt } from "react-icons/fa";

function Basket() {
    const [products, setProducts] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [productsInBasket, setProductsInBasket] = useState([]);
    const [number, setNumber] = useState(1);
    const navigate = useNavigate();


    // useEffect(() => {
    //
    //     const savedBasket = JSON.parse(localStorage.getItem('basket')) || [];
    //     // setBasket(savedBasket);
    //     setProducts(savedBasket)
    // }, []);

    useEffect(() => {
        const savedBasket = JSON.parse(localStorage.getItem('basket')) || [];
        setProductsInBasket(savedBasket);
        console.log(savedBasket);
    }, []);




    const filteredProducts = productsInBasket.filter(product =>
        product.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const handleDeleteFromBasket = (productToDelete) => {

        setProductsInBasket(prevBasket => {
            // console.log(productToDelete);
            // console.log(productToDelete.id);
            const updatedBasket = prevBasket.filter(product => product.id !== productToDelete.id);
            localStorage.setItem('basket', JSON.stringify(updatedBasket));
            // window.location.reload();
            console.log(updatedBasket);
            return updatedBasket;
        });
    };


    const handleAddOrder = () => {
        // let productsAndNumbersList=[];
        let productsToOrderList=[];
        for (let i = 0; i < productsInBasket.length; i++) {
            console.log(productsInBasket[i]);
            console.log(productsInBasket[i].number);
            let productToOrder= new ProductsToOrder(productsInBasket[i].id,productsInBasket[i].number);
            console.log("productToOrder.toJSON()");
            console.log(productToOrder.toJSON());
            productsToOrderList.push(productToOrder.toJSON());
        }
        console.log("productsToOrderList");
        console.log(productsToOrderList);
        const formattedData = {
            productsAndNumbersList: productsToOrderList.map(item => ({
                productId: String(item.productId),
                productNumber: String(item.productNumber)
            }))
        };
        console.log("formattedData");
        console.log(formattedData);
        const resultJson = JSON.stringify(formattedData, null, 2);
        console.log(resultJson);
        // const jsonProductsToOrderList = JSON.stringify(productsToOrderList);
        OrderService.addOrder(resultJson,sessionStorage.getItem('token'));

        navigate('/order-confirmation');
    };

    const handleSetNumber = (product0, number) => {
        setProductsInBasket(prevBasket => {
            const updatedBasket = prevBasket.map(product =>
                product.id === product0.id
                    ? { ...product, number: Number(number) || 0 }
                    : product
            );

            console.log(updatedBasket);
            localStorage.setItem('basket', JSON.stringify(updatedBasket));

            return updatedBasket;
        });
    };


    return (
        <div className="home-container">
            <div className="top-content-container">

                <h1 className="title">Koszyk</h1>

                <input
                    type="text"
                    placeholder="Wyszukaj produkt..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="search-bar"
                />
            </div>
            <div className="productsContainer">
                {filteredProducts.length === 0 ? (
                    <p className="err-container"><span className="sad">:(</span><span
                        className="msg"> Brak produktów</span></p>
                ) : (
                    filteredProducts.map((product) => (
                        <div className="product-container" key={product.id}>
                            <img className="product-img" src={product.imageUrl} alt={product.name}/>
                            <p className="product-name">{product.name}</p>
                            <p className="product-price">{product.price} zł</p>

                            <form>
                                <div className="addProduct-form-group">
                                    <input
                                        type="number"
                                        id="number"
                                        value={product.number}
                                        onChange={(e) => handleSetNumber(product, e.target.value)}
                                        placeholder="Ilość"
                                        required
                                    />
                                </div>
                            </form>

                            <button className="delete-product-btn"
                                    onClick={() => handleDeleteFromBasket(product)}><FaTrashAlt /> Usuń z koszyka
                            </button>

                        </div>
                    ))
                )}

                <div className="order-btn-container">
                    <button className="order-btn" onClick={handleAddOrder}>
                        Złóż zamówienie
                    </button>
                </div>

            </div>
        </div>
    );
}

export default Basket;
