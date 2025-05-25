import axios from "axios";

class AuthService {

    static BASE_URL = "http://localhost:8080"


    static async login(email, password) {

        const response = await axios.post(`${AuthService.BASE_URL}/auth/login`, { email, password });
        return response.data;
    }

    static async register(userData) {

        const response = await axios.post(`${AuthService.BASE_URL}/auth/register`, userData, {
        })
        return response.data;
    }




    /* auth checker */

    static isAuthenticated(){
        const token = sessionStorage.getItem('token')
        return !!token
    }

    static logout() {
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('role')
        sessionStorage.removeItem('basket')

    }

    static isAdmin(){
        const role = sessionStorage.getItem('role')
        console.log(role)
        return role === 'ADMIN'
    }

    static isUser(){
        const role = sessionStorage.getItem('role')
        return role === 'USER'
    }

    static adminOnly(){
        return this.isAuthenticated() && this.isAdmin();
    }
}

export default AuthService;