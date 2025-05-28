import React, { useState, useEffect } from 'react';
import UserService from '../../services/UserService.js';
import './ProfilePage.css'
import { FaCity } from "react-icons/fa";
import { MdEmail } from "react-icons/md";
import { CgProfile } from "react-icons/cg";

function ProfilePage() {
    const [profileInfo, setProfileInfo] = useState({});

    useEffect(() => {
        fetchProfileInfo();
    }, []);

    const fetchProfileInfo = async () => {
        try {

            const token = sessionStorage.getItem('token');
            console.log("token: " + token);
            const response = await UserService.getYourProfile(token);
            console.log('Otrzymano profil:', response);
            setProfileInfo(response.ourUsersEntity);
            console.log(profileInfo);

        } catch (error) {
            console.error('Error fetching profile information: ', error);
        }
    };

    return (
        <div className="profile-page-container">
            <h1>Profil</h1>
            <p className="profile-info"><span className="profile-icon"><CgProfile /></span> Nazwa użytkownika: {profileInfo.name}</p>
            <p className="profile-info"><span className="profile-icon"><MdEmail /></span> Email: {profileInfo.email}</p>
            <p className="profile-info"><span className="profile-icon"><FaCity /></span> Miasto: {profileInfo.city}</p>
        </div>
    );
}

export default ProfilePage;