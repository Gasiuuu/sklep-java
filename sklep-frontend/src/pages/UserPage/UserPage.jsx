import React, { useState, useEffect } from 'react';
import UserService from '../../services/UserService';
import './UserPage.css';

function UserPage() {
    const [users, setUsers] = useState([]);
    const [temporaryPassword, setTemporaryPassword] = useState('')

    const [error, setError] = useState(null);

    const [editingUser, setEditingUser] = useState(null);
    // Stan formularza – przechowuje nowe wartości pól podczas edycji
    const [editFormData, setEditFormData] = useState({
        city: '',
        email: '',
        name: '',
        password: '',
        role: ''
    });


    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const token = sessionStorage.getItem('token');
            const response = await UserService.getAllUsers(token);
            setUsers(response.ourUsersEntityList);
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };

    const deleteUser = async (userId) => {
        try {
            const confirmDelete = window.confirm('Are you sure you want to delete this user?');
            const token = sessionStorage.getItem('token');
            if (confirmDelete) {
                await UserService.deleteUser(userId, token);
                fetchUsers();
            }
        } catch (error) {
            console.error('Error deleting user:', error);
        }
    };







    const handleUpdateUser = async (e) => {
        e.preventDefault();
        try {
            const token = sessionStorage.getItem('token');
            console.log(token);
            console.log(editingUser.id);
            console.log(editFormData);
            // Wywołanie metody aktualizującej produkt w serwisie
            await UserService.updateUser(editingUser.id, editFormData, token);

            // Aktualizacja listy produktów w stanie – np. możemy pobrać je jeszcze raz,
            // lub zaktualizować tylko jeden produkt w tablicy:
            setUsers((prevUsers) => {
                return prevUsers.map((prod) => {
                    if (prod.id === editingUser.id) {
                        return { ...prod, ...editFormData };
                    }
                    return prod;
                });
            });

            // Wyczyść stany po zakończeniu edycji
            setEditingUser(null);
            setEditFormData({
                city: '',
                email: '',
                name: '',
                password: '',
                role: ''
            });
        } catch (err) {
            setError('Nie udało się zaktualizować produktu.');
            console.error(err);
        }
    };

    // Anulowanie/wyjście z edycji bez zapisywania
    const handleCancelEdit = () => {
        setEditingUser(null);
        setEditFormData({
            city: '',
            email: '',
            name: '',
            password: '',
            role: ''
        });
    };

    const handleEditUser = (user) => {
        // Ustawiamy aktualnie edytowany produkt w stanie
        setEditingUser(user);
        // Wypełniamy formularz wartościami produktu
        setEditFormData({
            city: user.city,
            email: user.email,
            name: user.name,
            password: user.password,
            role: user.role
        });

    };

    return (
        <div className="home-container">
            <div className="top-content-container">
                <h1 className="title">Panel Użytkowników</h1>
            </div>

            <div className="usersContainer">
                {users.map((user) => (
                    <div className="user-container" key={user.id}>
                        <div className="user-info">
                            <p><strong>ID: </strong> {user.id}</p>
                            <p><strong>Nazwa użytkownika: </strong> {user.name}</p>
                            <p><strong>Email: </strong> {user.email}</p>
                            <p><strong>Miasto: </strong> {user.city}</p>
                            <p><strong>Rola: </strong> {user.role}</p>
                        </div>
                        <div className="actions">
                            <div className="button-div">
                                <button style={{width:'180px', float: 'right'}}
                                    className="edit-user-btn"
                                    onClick={() => handleEditUser(user)}
                                >
                                    Edytuj produkt
                                </button>
                                <button style={{width:'180px', float: 'right'}} className="delete-product-btn" onClick={() => deleteUser(user.id)}>Usuń
                                    użytkownika
                                </button>
                            </div>

                        </div>
                    </div>
                ))}
            </div>
            {editingUser && (
                <div className="edit-user-modal">
                    <form onSubmit={handleUpdateUser} className="editUser-form">
                        <div className="editUser-form-group">
                            <input
                                type="text"
                                id="name"
                                value={editFormData.name}
                                onChange={(e) =>
                                    setEditFormData((prev) => ({...prev, name: e.target.value}))
                                }
                                placeholder="Nazwa"
                                required
                            />
                        </div>

                        <div className="editUser-form-group">
                            <input
                                type="text"
                                id="city"
                                value={editFormData.city}
                                onChange={(e) =>
                                    setEditFormData((prev) => ({...prev, city: e.target.value}))
                                }
                                placeholder="Miasto"
                                required
                            />
                        </div>

                        <div className="editUser-form-group">
                            <input
                                type="text"
                                id="price"
                                value={editFormData.email}
                                onChange={(e) =>
                                    setEditFormData((prev) => ({...prev, email: e.target.value}))
                                }
                                placeholder="Email"
                                required
                            />
                        </div>

                        <div className="editUser-form-group">
                            <input
                                type="text"
                                id={temporaryPassword}
                                value={temporaryPassword}
                                onChange={(e) =>{
                                    setTemporaryPassword(e.target.value)
                                    setEditFormData((prev) => ({
                                        ...prev,
                                        password: e.target.value
                                    }))
                                }
                                }
                                placeholder="Hasło"
                                required
                            />
                        </div>

                        {/* Jeśli chcesz mieć opcję aktualizacji pliku w trakcie edycji: */}
                        <div className="editUser-form-group">
                            <input
                                type="text"
                                id="role"
                                value={editFormData.role}
                                onChange={(e) =>
                                    setEditFormData((prev) => ({
                                        ...prev,
                                        role: e.target.value
                                    }))
                                }
                                placeholder="Rola"
                                required
                            />
                        </div>

                        {error && <p className="editUser-error-message">{error}</p>}

                        <button type="submit" className="editUser-submit-button">Zapisz</button>
                        <button type="button" onClick={handleCancelEdit} className="editUser-submit-button">
                            Anuluj
                        </button>
                    </form>
                </div>
            )}
        </div>
    );
}

export default UserPage;