import React, { useEffect, useState } from 'react';
import './Home.css';
import ProductService from '../../services/ProductService.js';
import { FaSearch } from "react-icons/fa";
import {Link} from "react-router-dom";


function Home() {
    const [products, setProducts] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [basket, setBasket] = useState([]);
    const [items_count, setItems_count] = useState(0);

    useEffect(() => {
        fetchProducts()
        const savedBasket = JSON.parse(localStorage.getItem('basket')) || [];
        setBasket(savedBasket);
    }, []);

    const fetchProducts = async () => {
        try {
            const productList = await ProductService.getAllProducts();
            setProducts(productList);
            setItems_count(productList.length);
        } catch (error) {
            console.error('Error fetching products:', error);
        }
    };

    const filteredProducts = products.filter(product =>
        product.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const handleAddToBasket = (product) => {
        setBasket(prevBasket => {
            const updatedBasket = [...prevBasket, product];
            localStorage.setItem('basket', JSON.stringify(updatedBasket));
            return updatedBasket;
        });
        window.alert("Produkt dodany do koszyka");
    };

    return (
        <div className="home-container">
            <div className="top-content-container">
                <h1 className="items-count">Ilość przedmiotów - {items_count}</h1>

                <form
                    className="search-form"
                    onSubmit={(e) => {
                        e.preventDefault();
                        fetchProducts();
                    }}
                >
                    <input
                        type="text"
                        placeholder="Wyszukaj produkt..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-bar"
                    />
                    <button type="submit" className="search-btn"><FaSearch/> Szukaj</button>
                </form>
            </div>


            <div className="productsContainer">
                {filteredProducts.length === 0 ? (
                    <p className="err-container"><span className="sad">:(</span><span className="msg"> Brak produktów</span></p>
                ) : (
                    filteredProducts.map((product) => (
                        <div className="product-container" key={product.id}>
                            <Link to={`/product/${product.id}`}>
                                <img className="product-img" src={product.imageUrl} alt={product.name}/>
                            </Link>
                            <div className="product-info">
                                <Link to={`/product/${product.id}`}>
                                    <p className="product-name">{product.name}</p>
                                </Link>
                            </div>
                            <div className="product-actions">
                                <p className="product-price">{product.price} zł</p>
                                <button className="add-btn" onClick={() => handleAddToBasket(product)}>
                                    Dodaj do koszyka
                                </button>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );

}

export default Home;