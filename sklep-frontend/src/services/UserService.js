import axios from "axios";

class UserService {

    static BASE_URL = "http://localhost:8080"




    static async getAllUsers() {
        const response = await axios.get(`${UserService.BASE_URL}/admin/get-all-users`, {
            headers: {
                Authorization: `Bearer ${sessionStorage.getItem('token')}`,
            },
        });
        return response.data;
    }

    static async getUserById(userId) {
        const response = await axios.get(`${UserService.BASE_URL}/admin/get-users/${userId}`, {
            headers: {
                Authorization: `Bearer ${sessionStorage.getItem('token')}`,
            },
        });
        return response.data;
    }



    static async updateUser(userId,formData,token) {
        console.log("formdata");
        console.log(token);
        console.log(formData);
        console.log(userId);
        try {
            const response = await fetch(`${UserService.BASE_URL}/admin/update-user/${userId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(formData),
            });
            console.log('Odpowiedz z backendu');
            console.log(response);


            if (!response.ok) {
                throw new Error('Błąd podczas dodawania produktu');
            }

            const data = await response.json();
            console.log('Produkt dodany:', data);
            // navigate('/products');

        } catch (err) {
            // setError(err.message);
            console.error('Błąd:', err);
        }
    }


    static async deleteUser(userId) {
        await axios.delete(`${UserService.BASE_URL}/admin/delete-user/${userId}`, {
            headers: {
                Authorization: `Bearer ${sessionStorage.getItem('token')}`,
            },
        });
    }



    static async getYourProfile(token) {
        const response = await axios.get(`${UserService.BASE_URL}/adminuser/get-profile`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        console.log("response: ", response)
        return response.data;
    }



}

export default UserService;





































// static async login(email, password) {
//
//     const response = await axios.post(`${UserService.BASE_URL}/auth/login`, { email, password });
//     return response.data;
// }
//
// static async register(userData) {
//
//     const response = await axios.post(`${UserService.BASE_URL}/auth/register`, userData, {
//     })
//     return response.data;
// }
//
// static async addProduct(formData,token) {
//
//     try {
//         const response = await fetch(`${UserService.BASE_URL}/admin/add-product`, {
//             method: 'POST',
//             headers: {
//                 'Authorization': `Bearer ${token}` // Dodanie Bearer Tokena
//             },
//             body: formData // Wysłanie danych jako form-data
//         });
//
//         if (!response.ok) {
//             throw new Error('Błąd podczas dodawania produktu');
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
//
//
// static async addOrder(formData,token) {
//
//     try {
//         const response = await fetch(`${UserService.BASE_URL}/adminuser/add_order`, {
//             method: 'POST',
//             headers: {
//                 'Authorization': `Bearer ${token}`, // Dodanie Bearer Tokena
//                 'Content-Type': 'application/json' // Nagłówek JSON
//             },
//             body: formData // Wysłanie danych jako form-data
//         });
//         console.log(response);
//         if (!response.ok) {
//             throw new Error('Błąd podczas dodawania produktu');
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
//
// static async getUserOrders(token) {
//
//     try {
//         const response = await fetch(`${UserService.BASE_URL}/adminuser/orders`, {
//             method: 'GET',
//             headers: {
//                 'Authorization': `Bearer ${token}`, // Dodanie Bearer Tokena
//             },
//         });
//         console.log(response);
//         if (!response.ok) {
//             throw new Error('Błąd podczas dodawania produktu');
//         }
//
//         const data = await response.json();
//         console.log("Datta");
//         console.log(data);
//         return data;
//         // navigate('/products'); // Przekierowanie po sukcesie
//
//     } catch (err) {
//         // setError(err.message);
//         console.error('Błąd:', err);
//     }
// }
//
//
// static async getAllCustomersOrders(token) {
//
//     try {
//         const response = await fetch(`${UserService.BASE_URL}/admin/allOrders`, {
//             method: 'GET',
//             headers: {
//                 'Authorization': `Bearer ${token}`, // Dodanie Bearer Tokena
//             },
//         });
//         console.log(response);
//         if (!response.ok) {
//             throw new Error('Błąd podczas dodawania produktu');
//         }
//
//         const data = await response.json();
//         console.log("Datta");
//         console.log(data);
//         return data;
//         // navigate('/products'); // Przekierowanie po sukcesie
//
//     } catch (err) {
//         // setError(err.message);
//         console.error('Błąd:', err);
//     }
// }

// static async getAllProducts() {
//     const response = await axios.get(`${UserService.BASE_URL}/public/get-all-products`);
//     return response.data.productsEntityList;
// }

// static async getProductById(productId) {
//     const response = await axios.get(`${UserService.BASE_URL}/public/get-product-by-Id/${productId}`);
//     return response.data;
// }
/////////////////////////


// static async updateProduct(productId,formData,token) {
//     try {
//         const response = await fetch(`${UserService.BASE_URL}/admin/update-product/${productId}`, {
//             method: 'POST',
//             headers: {
//                 'Authorization': `Bearer ${token}` // Dodanie Bearer Tokena
//             },
//             body: formData // Wysłanie danych jako form-data
//         });
//         console.log('Odpowiedz z backendu');
//         console.log(response);
//
//         if (!response.ok) {
//             throw new Error('Błąd podczas dodawania produktu');
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



/* auth checker */

// static isAuthenticated(){
//     const token = sessionStorage.getItem('token')
//     return !!token
// }
//
// static logout() {
//     sessionStorage.removeItem('token')
//     sessionStorage.removeItem('role')
//     sessionStorage.removeItem('basket')
//
// }
//
// static isAdmin(){
//     const role = sessionStorage.getItem('role')
//     console.log(role)
//     return role === 'ADMIN'
// }
//
// static isUser(){
//     const role = sessionStorage.getItem('role')
//     return role === 'USER'
// }

// static adminOnly(){
//     return this.isAuthenticated() && this.isAdmin();
// }