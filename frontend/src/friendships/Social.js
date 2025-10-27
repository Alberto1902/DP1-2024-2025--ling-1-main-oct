import React, { useEffect, useState } from 'react';
import tokenService from '../services/token.service';
import useFetchState from "../util/useFetchState";
import '../static/css/game/gameSession.css';
import { useNavigate } from "react-router-dom";

const jwt = tokenService.getLocalAccessToken();

export default function Social() {
    useEffect(() => {
        document.body.style.overflow = 'hidden';

        return () => {
            document.body.style.overflow = 'auto';
        };
    }, []);

    const user = tokenService.getUser();
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const [friends, setFriends] = useFetchState(
        [],
        `/api/v1/friendship/friends?username=${user.username}`,
        jwt
    );

    const [pendingRequests, setPendingRequests] = useFetchState(
        [],
        `/api/v1/friendship/requests?username=${user.username}&status=PENDING`,
        jwt
    );

    const [pendingInvitations, setPendingInvitations] = useFetchState(
        [],
        `/api/v1/invitation/pending?receiver_id=${user.id}`,
        jwt
    );

    // NUEVA FUNCIÓN PARA NAVEGAR AL PERFIL DEL AMIGO
    const handleFriendClick = (username) => {
        navigate(`/profile/${username}`); // Asegúrate de que esta sea la ruta correcta para UserProfileScreen
    };

    const handleSendClick = (usernameReq) => {
        fetch(
            `/api/v1/friendship/sendRequest?username1=${user.username}&username2=${usernameReq}`, {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: 'application/json',
                'Content-Type': 'application/json',
            }
        }
        ).then((response) => response.text())
            .then(() => window.location.reload());
    };

    const handleAcceptRequest = (requestUsername) => {
        fetch(
            `/api/v1/friendship/accept?username1=${requestUsername}&username2=${user.username}`, {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: 'application/json',
                'Content-Type': 'application/json',
            }
        }
        ).then((response) => response.text())
            .then(() => window.location.reload());
    };

    const handleRejectRequest = (requestUsername) => {
        fetch(
            `/api/v1/friendship/reject?username1=${requestUsername}&username2=${user.username}`, {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: 'application/json',
                'Content-Type': 'application/json',
            }
        }
        ).then((response) => response.text())
            .then(() => window.location.reload());
    };

    const handleAcceptInvitation = (invitation) => {
        fetch(
            `/api/v1/invitation/accept?id=${invitation.id}`, {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: 'application/json',
                'Content-Type': 'application/json',
            }
        }
        ).then((response) => response.text())
            .then(() => navigate('/gamesessions/' + invitation.game.id));
    };

    const handleRejectInvitation = (invitation) => {
        fetch(
            `/api/v1/invitation/reject?id=${invitation.id}`, {
            method: 'PUT',
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: 'application/json',
                'Content-Type': 'application/json',
            }
        }
        ).then((response) => response.text())
            .then(() => window.location.reload());
    };

    return (
        <div>
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'flex-start',
                height: '100vh',
                overflow: 'auto',
                paddingTop: '2vh'
            }}>
                <div style={{
                    backgroundColor: 'white',
                    borderRadius: '15px',
                    boxShadow: '0 4px 10px rgba(0, 0, 0, 0.1)',
                    padding: '20px',
                    maxWidth: '1200px',
                    width: '90%',
                    maxHeight: '90vh',
                    overflowY: 'auto',
                    display: 'flex',
                    flexDirection: 'row'
                }}>
                    <div style={{
                        padding: '10px',
                        flex: 1,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'flex-start',
                        overflow: 'hidden'
                    }}>
                        <h2 style={{ marginBottom: '10px', textAlign: 'center' }}>Send friend request</h2>
                        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '2vh' }}>
                            <input
                                type="text"
                                placeholder="Enter the username"
                                value={message}
                                onChange={(e) => setMessage(e.target.value)}
                                style={{
                                    width: '70%',
                                    padding: '10px',
                                    marginRight: '10px',
                                    borderRadius: '5px',
                                    border: '1px solid #ccc'
                                }}
                            />
                            <button
                                style={{
                                    padding: '10px 20px',
                                    backgroundColor: '#d2a679',
                                    color: '#fff',
                                    border: 'none',
                                    borderRadius: '5px',
                                    cursor: 'pointer',
                                    fontSize: '16px',
                                    fontWeight: 'bold',
                                    transition: 'background-color 0.3s',
                                }}
                                onClick={() => handleSendClick(message)}
                                onMouseEnter={(e) => e.target.style.backgroundColor = '#c4976b'}
                                onMouseLeave={(e) => e.target.style.backgroundColor = '#d2a679'}
                            >
                                Send
                            </button>
                        </div>

                        <div style={{ padding: '5vh 10px', overflowY: 'auto', maxHeight: '60vh', width: '100%' }}>
                            <h3 style={{ textAlign: 'center' }}>My pending requests</h3>
                            <ul style={{ listStyleType: 'none', padding: 0, textAlign: 'center' }}>
                                {pendingRequests.length > 0 ? (
                                    pendingRequests.map((friendship, index) => (
                                        <li style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', marginTop: '2vh' }} key={index}>
                                            <span style={{ marginRight: '10px' }}>
                                                {friendship.user1.username}
                                            </span>
                                            <div
                                                className="profile-picture-container"
                                                style={{
                                                    backgroundColor: friendship.user1.colorTheme,
                                                    borderRadius: '50%',
                                                    height: '40px',
                                                    width: '40px',
                                                    overflow: 'hidden',
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    justifyContent: 'center',
                                                    position: 'relative'
                                                }}
                                            >
                                                <img
                                                    src={friendship.user1.profilePictureUri}
                                                    alt={`${friendship.user1.username}'s profile`}
                                                    className="profile-picture"
                                                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                                                />
                                            </div>
                                            <div style={{ display: 'flex', gap: '10px', justifyContent: 'center', marginLeft: '10px' }}>
                                                <button
                                                    onClick={() => handleAcceptRequest(friendship.user1.username)}
                                                    style={{
                                                        backgroundColor: '#4CAF50',
                                                        color: 'white',
                                                        border: 'none',
                                                        borderRadius: '5px',
                                                        padding: '5px 10px',
                                                        cursor: 'pointer'
                                                    }}
                                                >
                                                    ✔
                                                </button>
                                                <button
                                                    onClick={() => handleRejectRequest(friendship.user1.username)}
                                                    style={{
                                                        backgroundColor: 'red',
                                                        color: 'white',
                                                        border: 'none',
                                                        borderRadius: '5px',
                                                        padding: '5px 10px',
                                                        cursor: 'pointer'
                                                    }}
                                                >
                                                    ✖
                                                </button>
                                            </div>
                                        </li>
                                    ))
                                ) : (
                                    <li style={{ marginTop: '2vh' }}>No requests found.</li>
                                )}
                            </ul>
                        </div>

                        <div style={{ padding: '5vh 10px', overflowY: 'auto', maxHeight: '60vh', width: '100%' }}>
                            <h3 style={{ textAlign: 'center' }}>My pending invitations</h3>
                            <ul style={{ listStyleType: 'none', padding: 0, textAlign: 'center' }}>
                                {pendingInvitations.length > 0 ? (
                                    pendingInvitations.map((invitation, index) => (
                                        <li style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', marginTop: '2vh' }} key={index}>
                                            <span style={{ marginRight: '10px' }}>
                                                {invitation.sender.username}
                                            </span>
                                            <div
                                                className="profile-picture-container"
                                                style={{
                                                    backgroundColor: invitation.sender.colorTheme,
                                                    borderRadius: '50%',
                                                    height: '40px',
                                                    width: '40px',
                                                    overflow: 'hidden',
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    justifyContent: 'center',
                                                    position: 'relative'
                                                }}
                                            >
                                                <img
                                                    src={invitation.sender.profilePictureUri}
                                                    alt={`${invitation.sender.username}'s profile`}
                                                    className="profile-picture"
                                                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                                                />
                                            </div>
                                            <div style={{ display: 'flex', gap: '10px', justifyContent: 'center', marginLeft: '10px' }}>
                                                <button
                                                    onClick={() => handleAcceptInvitation(invitation)}
                                                    style={{
                                                        backgroundColor: '#4CAF50',
                                                        color: 'white',
                                                        border: 'none',
                                                        borderRadius: '5px',
                                                        padding: '5px 10px',
                                                        cursor: 'pointer'
                                                    }}
                                                >
                                                    ✔
                                                </button>
                                                <button
                                                    onClick={() => handleRejectInvitation(invitation)}
                                                    style={{
                                                        backgroundColor: 'red',
                                                        color: 'white',
                                                        border: 'none',
                                                        borderRadius: '5px',
                                                        padding: '5px 10px',
                                                        cursor: 'pointer'
                                                    }}
                                                >
                                                    ✖
                                                </button>
                                            </div>
                                        </li>
                                    ))
                                ) : (
                                    <li style={{ marginTop: '2vh' }}>No requests found.</li>
                                )}
                            </ul>
                        </div>
                    </div>

                    <div style={{
                        padding: '10px',
                        overflowY: 'auto',
                        maxHeight: '100vh',
                        flex: 1,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'flex-start'
                    }}>
                        <h2 style={{ textAlign: 'center' }}>My friends</h2>
                        <ul style={{ listStyleType: 'none', padding: 0, textAlign: 'center' }}>
                            {friends.length > 0 ? (
                                friends.map((friend) => (
                                    <li
                                        style={{
                                            marginTop: '2vh',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            gap: '10px',
                                            cursor: 'pointer', // Make it look clickable
                                            padding: '8px 15px',
                                            borderRadius: '8px',
                                            transition: 'background-color 0.3s ease',
                                        }}
                                        key={friend.id}
                                        onClick={() => handleFriendClick(friend.username)} // Call the new function
                                        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = 'rgba(210, 166, 121, 0.2)'}
                                        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                                    >
                                        <span>{friend.username}</span>
                                        <div
                                            className="profile-picture-container"
                                            style={{
                                                backgroundColor: friend.colorTheme,
                                                borderRadius: '50%',
                                                height: '40px',
                                                width: '40px',
                                                overflow: 'hidden',
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'center',
                                                position: 'relative'
                                            }}
                                        >
                                            <img
                                                src={friend.profilePictureUri}
                                                alt={`${friend.username}'s profile`}
                                                className="profile-picture"
                                                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                                            />
                                            {friend.online && (
                                                <span
                                                    style={{
                                                        position: 'absolute',
                                                        bottom: '5px',
                                                        right: '5px',
                                                        width: '10px',
                                                        height: '10px',
                                                        backgroundColor: 'green',
                                                        borderRadius: '50%',
                                                        border: '2px solid white'
                                                    }}
                                                />
                                            )}
                                        </div>
                                    </li>
                                ))
                            ) : (
                                <li style={{ marginTop: '2vh' }}>No friends found.</li>
                            )}
                        </ul>
                    </div>
                </div>
            </div>
        </div>

    );
}