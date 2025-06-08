import React, { useEffect, useState } from 'react';
import UserService from '../../services/UserService.js';
import ProductService from "../../services/ProductService.js";
import './AdminProductPage.css';

function AdminProductPage() {
    const [products, setProducts] = useState([]);
    const [error, setError] = useState(null);
    const [file, setFile] = useState(null);

    const [editingProduct, setEditingProduct] = useState(null);
    const [editFormData, setEditFormData] = useState({
        name: '',
        price: '',
        category: '',
        description: '',
        // imageUrl: ''
    });

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const token = sessionStorage.getItem('token');
                const allProducts = await ProductService.getAllProducts();
                setProducts(allProducts);
            } catch (err) {
                setError('Nie udało się pobrać produktów.');
                console.error(err);
            }
        };

        fetchProducts();
    }, []);

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };


    const handleDeleteProduct = async (productId) => {
        try {
            // const token = sessionStorage.getItem('token');
            await ProductService.deleteProduct(productId);
            setProducts(products.filter((product) => product.id !== productId));
        } catch (err) {
            setError('Nie udało się usunąć produktu.');
            console.error(err);
        }
    };

    const handleEditProduct = (product) => {
        setEditingProduct(product);
        setEditFormData({
            name: product.name,
            price: product.price,
            category: product.category,
            description: product.description,
            // imageUrl: product.imageUrl
        });

    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setEditFormData((prevData) => ({
            ...prevData,
            [name]: value
        }));
    };

    const handleUpdateProduct = async (e) => {
        e.preventDefault();
        try {

            const formData = new FormData();
            console.log("JSON.stringify(editFormData)");
            console.log(JSON.stringify(editFormData));

            const productDtoBlob = new Blob(
                [JSON.stringify(editFormData)],
                { type: 'application/json' }
            );
            formData.append("productDto", productDtoBlob);


            formData.append("productDto", JSON.stringify(editFormData));

            console.log(formData.productDto);
            if (file) {
                formData.append('file', file);
                console.log(file);
            }
            await ProductService.updateProduct(editingProduct.id, formData);


            setProducts((prevProducts) => {
                return prevProducts.map((prod) => {
                    if (prod.id === editingProduct.id) {
                        return { ...prod, ...editFormData };
                    }
                    return prod;
                });
            });

            setEditingProduct(null);
            setEditFormData({
                name: '',
                price: '',
                category: '',
                description: '',
                imageUrl: ''
            });
        } catch (err) {
            setError('Nie udało się zaktualizować produktu.');
            console.error(err);
        }
    };

    const handleCancelEdit = () => {
        setEditingProduct(null);
        setEditFormData({
            name: '',
            price: '',
            category: '',
            description: '',
            // imageUrl: ''
        });
    };

    return (
        <div className="home-container">
            <div className="top-content-container">
                <h1 className="title">Lista produktów</h1>
                {error && <p className="error-message">{error}</p>}
            </div>

            <div className="productsContainer">
                {products.map((product) => (
                    <div className="product-container" key={product.id}>
                        <div className="user-info">
                            <img
                                className="product-img"
                                src={product.imageUrl}
                                alt={product.name}
                            />
                            <p><strong>ID:</strong> {product.id}</p>
                            <p><strong>Nazwa:</strong> {product.name}</p>
                            <p><strong>Cena:</strong> {product.price} PLN</p>
                            <p><strong>Kategoria:</strong> {product.category}</p>
                            <p><strong>Opis:</strong> {product.description}</p>
                        </div>
                        <div className="actions">
                            <button
                                className="edit-product-btn"
                                onClick={() => handleEditProduct(product)}
                            >
                                Edytuj produkt
                            </button>
                        </div>
                        <div className="actions">
                            <button
                                className="delete-product-btn"
                                onClick={() => handleDeleteProduct(product.id)}
                            >
                                Usuń produkt
                            </button>
                        </div>
                    </div>
                ))}
            </div>

            {editingProduct && (
                <div className="edit-product-modal">
                    <form onSubmit={handleUpdateProduct} className="editProduct-form">
                        <div className="editProduct-form-group">
                            <input
                                type="text"
                                id="name"
                                value={editFormData.name}
                                onChange={(e) =>
                                    setEditFormData((prev) => ({ ...prev, name: e.target.value }))
                                }
                                placeholder="Nazwa"
                                required
                            />
                        </div>

                        <div className="editProduct-form-group">
                            <input
                                type="text"
                                id="category"
                                value={editFormData.category}
                                onChange={(e) =>
                                    setEditFormData((prev) => ({ ...prev, category: e.target.value }))
                                }
                                placeholder="Kategoria"
                                required
                            />
                        </div>

                        <div className="editProduct-form-group">
                            <input
                                type="number"
                                id="price"
                                value={editFormData.price}
                                onChange={(e) =>
                                    setEditFormData((prev) => ({ ...prev, price: e.target.value }))
                                }
                                placeholder="Cena"
                                required
                            />
                        </div>

                        <div className="editProduct-form-group">
                            <input
                                type="text"
                                id="description"
                                value={editFormData.description}
                                onChange={(e) =>
                                    setEditFormData((prev) => ({
                                        ...prev,
                                        description: e.target.value
                                    }))
                                }
                                placeholder="Opis produktu"
                                required
                            />
                        </div>

                        <div className="editProduct-form-group">
                            <input
                                type="file"
                                id="file"
                                accept="file/*"
                                onChange={(e) =>
                                    // setEditFormData((prev) => ({
                                    //     ...prev,
                                    //     file: e.target.files[0]
                                    // }))
                                    setFile(e.target.files[0])
                                }
                            />
                        </div>

                        {error && <p className="editProduct-error-message">{error}</p>}

                        <button type="submit" className="editProduct-submit-button">Zapisz</button>
                        <button type="button" onClick={handleCancelEdit} className="editProduct-submit-button">
                            Anuluj
                        </button>
                    </form>
                </div>
            )}

        </div>
    );
}

export default AdminProductPage;
