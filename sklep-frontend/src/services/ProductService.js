import axios from "axios";

class ProductService {

    static BASE_URL = "http://localhost:8080"


    static async addProduct(formData,token) {

        try {
            const response = await fetch(`${ProductService.BASE_URL}/admin/add-product`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}` // Dodanie Bearer Tokena
                },
                body: formData // Wysłanie danych jako form-data
            });

            if (!response.ok) {
                throw new Error('Błąd podczas dodawania produktu');
            }

            const data = await response.json();
            console.log('Produkt dodany:', data);
            // navigate('/products'); // Przekierowanie po sukcesie

        } catch (err) {
            // setError(err.message);
            console.error('Błąd:', err);
        }
    }


    static async updateProduct(productId, formData) {
        console.log(productId);
        console.log(formData);
        console.log(sessionStorage.getItem('token'));
        console.log(sessionStorage.getItem('role'));
        console.log("formdata1");




        try {
            const response = await fetch(`${ProductService.BASE_URL}/admin/update-product/${productId}`, {
                method: 'PUT',
                headers: {
                    // 'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('token')}` // Dodanie Bearer Tokena
                },
                body: formData // Wysłanie danych jako form-data
            });

            if (!response.ok) {
                throw new Error('Błąd podczas dodawania produktu');
            }

            const data = await response.json();
            console.log('Produkt dodany:', data);
            // navigate('/products'); // Przekierowanie po sukcesie

        } catch (err) {
            // setError(err.message);
            console.error('Błąd:', err);
        }
        window.location.reload();

    }

    // static async updateProduct(productId,formData) {
    //     try {
    //         // const response = await fetch(`${ProductService.BASE_URL}/admin/update-product/${productId}`, {
    //         //     method: 'POST',
    //         //     headers: {
    //         //         'Authorization': `Bearer ${token}`, // Dodanie Bearer Tokena
    //         //     },
    //         //     body: formData // Wysłanie danych jako form-data
    //         // });
    //         // console.log('Odpowiedz z backendu');
    //         // console.log(response);
    //
    //         const response=await axios.post(`${ProductService.BASE_URL}/admin/delete-product/${productId}`, formData,{
    //             headers: {
    //                 Authorization: `Bearer ${sessionStorage.getItem('token')}`
    //             },
    //
    //         });
    //
    //         if (!response.ok) {
    //             throw new Error('Błąd podczas aktualizowania produktu');
    //         }
    //
    //         const data = await response.json();
    //         console.log('Produkt dodany:', data);
    //         // navigate('/products'); // Przekierowanie po sukcesie
    //
    //     } catch (err) {
    //         // setError(err.message);
    //         console.error('Błąd:', err);
    //     }
    // }

    // static async updateProduct(productId, formData) {
    //     try {
    //         // Poprawny endpoint dla aktualizacji produktu
    //         const response = await axios.post(
    //             `${ProductService.BASE_URL}/admin/update-product/${productId}`,
    //             formData, // Dane wysyłane w żądaniu
    //             {
    //                 headers: {
    //                     Authorization: `Bearer ${sessionStorage.getItem('token')}`, // Token uwierzytelniający
    //                 },
    //             }
    //         );
    //
    //         console.log('Produkt zaktualizowany:', response.data);
    //         // navigate('/products'); // Przekierowanie po sukcesie, jeśli wymagane
    //     } catch (err) {
    //         console.error('Błąd podczas aktualizowania produktu:', err.message);
    //         // setError(err.message); // Ewentualna obsługa błędu
    //     }
    // }


    static async deleteProduct(productId) {
        await axios.delete(`${ProductService.BASE_URL}/admin/delete-product/${productId}`, {
            headers: {
                Authorization: `Bearer ${sessionStorage.getItem('token')}`,
            },
        });
    }


    static async getAllProducts() {
        const response = await axios.get(`${ProductService.BASE_URL}/public/get-all-products`);
        return response.data.productsEntityList;
    }

    static async getProductById(productId) {
        const response = await axios.get(`${ProductService.BASE_URL}/public/get-product-by-Id/${productId}`);
        return response.data;
    }

}

export default ProductService;