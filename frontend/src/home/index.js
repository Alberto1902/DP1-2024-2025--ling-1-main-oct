import React, { useState, useEffect } from 'react';
import { Button, Modal, ModalHeader, ModalBody } from 'reactstrap';
import '../App.css';
import '../static/css/home/home.css';
import logo from '../static/images/logoElba.png';
import { Link } from 'react-router-dom';
import jwt_decode from "jwt-decode";
import tokenService from '../services/token.service';
import Register from "../auth/register";
import Login from "../auth/login";
import '../static/css/profile/pfp.css'

const jwt = tokenService.getLocalAccessToken();

export default function Home() {
    const [user, setUser] = useState({});
    const [authorities, setAuthorities] = useState([]);
    const [friends, setFriends] = useState([]);
    const [onlineNotifications, setOnlineNotifications] = useState([]);
    const [loginModalOpen, setLoginModalOpen] = useState(false);
    const [registerModalOpen, setRegisterModalOpen] = useState(false);

    

    useEffect(() => {
        if (jwt) {
            const decodedToken = jwt_decode(jwt);
            if (decodedToken.authorities) {
                setAuthorities(decodedToken.authorities);
            }

            fetch(`/api/v1/users/${decodedToken.sub}`, {
                headers: { 'Authorization': `Bearer ${jwt}` }
            })
                .then(response => response.json())
                .then(data => { setUser(data); })
                .catch(error => { console.error("There was an error fetching the user data!", error); });
        }
    }, []);

    const role = authorities[0];

    useEffect(() => {
        if (user.username) {
            fetch(`/api/v1/friendship/friends?username=${user.username}`, {
                headers: { 'Authorization': `Bearer ${jwt}` }
            })
                .then(response => response.json())
                .then(data => {
                    setFriends(data);
                    checkFriendsOnline(data);
                })
                .catch(error => { console.error("There was an error fetching the friends data!", error); });
        }
    }, []);

    function checkFriendsOnline(friendsList) {
        friendsList.forEach(friend => {
            if (friend.online) {
                showNotification(`${friend.username} is connected`);
            }
        });
    }

    function showNotification(message) {
        const id = Date.now();
        setOnlineNotifications(prevNotifications => [...prevNotifications, { id, message }]);

        setTimeout(() => {
            setOnlineNotifications(prevNotifications => prevNotifications.filter(notif => notif.id !== id));
        }, 5000);
    }

    function toggleLoginModal() {
        setLoginModalOpen(!loginModalOpen);
    }

    function toggleRegisterModal() {
        setRegisterModalOpen(!registerModalOpen);
    }


    const containerStyle = {
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        zIndex: jwt ? -1 : 0,
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        overflow: 'hidden'
    };

    return (
        <div className="home-page-container" style={containerStyle}>
            {!jwt && <video autoPlay muted loop>
                <source src={require('../static/images/0103.mp4')} type="video/mp4" />
                Your browser does not support the video tag.
            </video>}

            {jwt ? (
                <div style={{ marginTop: '350px' }}>
                    <Button
                        style={{
                            backgroundColor: 'transparent',
                            border: 'none',
                            cursor: 'pointer',
                            padding: '0',
                        }}
                        tag={Link}
                        to={role === 'ADMIN' ? '/gamesessions/currentGames' : '/gamesessions'}
                    >
                        <img
                            src={role === 'ADMIN' ? 'CurrentGamesButton.png' : 'joinButton.png'}
                            alt="button icon"
                            style={{
                                width: '85vh',
                                height: '50vh',
                                marginBottom: '20vh',
                                backgroundColor: 'transparent',
                                transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                            }}
                            className="hover-effect"
                        />
                    </Button>
                    <Button
                        tag={Link}
                        style={{
                            backgroundColor: 'transparent',
                            border: 'none',
                            cursor: 'pointer',
                            padding: '0',
                        }}
                        to={role === 'ADMIN' ? '/gamesessions/finishedGames' : '/gamesessions/new'}
                    >
                        <img
                            src={role === 'ADMIN' ? 'finishedGamesButton.png' : 'newGameButton.png'}
                            alt="button icon"
                            style={{
                                width: '85vh',
                                height: '50vh',
                                marginBottom: '20vh',
                                backgroundColor: 'transparent',
                                transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                            }}
                            className="hover-effect"
                        />
                    </Button>
                </div>
            ) : (
                <div className="landing-page">
                    <div className="landing-content">
                        <img src={logo} alt="Escape from Elba" className="landing-logo" />
                        <p className="landing-subtitle">
                            Experience the thrilling adventure of escaping from the infamous island. Join us now and be part of the excitement!
                        </p>
                        <div className="landing-buttons">
                            <button className="landing-button register" onClick={toggleRegisterModal}>Register</button>
                            <button className="landing-button login" onClick={toggleLoginModal}>Login</button>
                        </div>
                    </div>
                </div>
            )}

            <div className="notification-container">
                {onlineNotifications.map((notif) => (
                    <div key={notif.id} className="notification">
                        {notif.message}
                    </div>
                ))}
            </div>

            <Modal isOpen={loginModalOpen} toggle={toggleLoginModal} centered>
                <ModalHeader toggle={toggleLoginModal}>Login</ModalHeader>
                <ModalBody className="custom-modal-body">
                    <Login />
                </ModalBody>
            </Modal>

            <Modal isOpen={registerModalOpen} toggle={toggleRegisterModal} centered>
                <ModalHeader toggle={toggleRegisterModal}>Register</ModalHeader>
                <ModalBody className="custom-modal-body">
                    <Register />
                </ModalBody>
            </Modal>
        </div>
    );
}
