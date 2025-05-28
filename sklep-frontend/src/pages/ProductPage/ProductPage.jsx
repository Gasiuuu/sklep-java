import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './ProductPage.css';
import UserService from '../../services/UserService.js';
import ProductService from '../../services/ProductService.js';
import { GrMoney } from "react-icons/gr";
import { FaCalculator } from "react-icons/fa6";
import { AiOutlineFileProtect } from "react-icons/ai";
import { FaHeart } from "react-icons/fa";
import { MdOutlineCompareArrows } from "react-icons/md";
// import handleAddToBasket from "./../Home/Home.jsx"

function ProductPage() {
    const { productId } = useParams(); // Pobieramy `id` z URL
    const [product, setProduct] = useState(null);
    const [error, setError] = useState(null);
    const [basket, setBasket] = useState([]);


    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const response = await ProductService.getProductById(productId);
                setProduct(response.productsEntity);
                console.log(response);
            } catch (err) {
                setError('Nie udało się załadować produktu');
            }
        };

        fetchProduct();
    }, [productId]);

    const handleAddToBasket = (product) => {
        setBasket(prevBasket => {
            const updatedBasket = [...prevBasket, product]; // Tworzymy zaktualizowany koszyk
            localStorage.setItem('basket', JSON.stringify(updatedBasket)); // Zapisujemy do localStorage
            return updatedBasket; // Zwracamy nowy stan koszyka
        });
        window.alert("Produkt dodany do koszyka");
    };



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

    if (error) return <div>{error}</div>;
    if (!product) return <div>Nie znaleziono produktu</div>;

    return (
        <div className="home-container">
            <div className="product-main">
                <div className="product-image-section">
                    <h1 className="product-name">{product.name}</h1>
                    <img
                        src={product.imageUrl}
                        alt={product.name}
                        className="product-image"
                    />
                </div>

                <div className="product-purchase-section">
                    <div className="product-purchase">
                        <p className="product-price"><span className="product-price-span">Cena: </span>{product.price} zł</p>
                        <button className="buy-btn" onClick={() => handleAddToBasket(product)}>Dodaj do koszyka</button>
                    </div>

                    <div className="availability-shipping">
                        <p className="availability">Dostępność:<span className="availability2"> W magazynie</span></p>
                        <p>Wysyłka: 1-2 dni robocze (u Ciebie w domu już {deliveryDate})</p>
                    </div>

                    <div className="purchase-details">
                        <div className="row1">
                            <p><GrMoney/> Weź na raty</p> <p className="row-right1"><FaHeart/> Dodaj do ulubionych</p>
                        </div>
                        <div className="row2">
                            <p><FaCalculator/> Kalkulator rat</p> <p className="row-right2"><MdOutlineCompareArrows/> Dodaj do porównania</p>
                        </div>
                        <p><AiOutlineFileProtect/> Dodatkowa ochrona</p>

                    </div>
                </div>

            </div>
            <div className="product-details">
                <div className="product-category">
                    <p>Kategoria - {product.category}</p>
                </div>
                <div className="product-description">
                    <h2>Opis</h2>
                    <p>{product.description}</p>
                </div>
            </div>

        </div>
    );
}

export default ProductPage;
