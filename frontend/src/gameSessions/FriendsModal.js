import React, { useState } from "react";
import { Modal, ModalBody, ModalHeader, Button } from 'reactstrap';
import '../static/css/game/Card.css';
import '../static/css/game/HandModal.css';
import tokenService from '../services/token.service';
import useFetchState from "../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

const FriendsModal = ({ user, isOpen, toggle, isSpectator, gameId }) => {

    const [friends, setFriends] = useFetchState(
        [],
        `/api/v1/friendship/friends?username=${user.username}`,
        jwt
    );

    const handleShare = async (friendId) => {
        try {
            const response = await fetch(`/api/v1/invitation?sender_id=${user.id}&receiver_id=${friendId}&game_id=${gameId}&is_spectator=${isSpectator}`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                }
        });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || `HTTP error! Status: ${response.status}`);
            }
        } catch (error) {
            alert(error);
        }
    };

    return (
        <Modal isOpen={isOpen} toggle={toggle}>
            <ModalHeader toggle={toggle}>Invite a friend!</ModalHeader>
            <ModalBody>
            <div style={{ padding: '10px', overflowY: 'auto', maxHeight: '100vh', flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-start' }}>
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
                                    gap: '10px'
                                }}
                                key={friend.id}
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
                                <button
                                    style={{
                                        background: 'linear-gradient(90deg, #4CAF50, #45a049)',
                                        color: '#fff',
                                        border: 'none',
                                        borderRadius: '8px',
                                        padding: '8px 16px',
                                        fontWeight: 'bold',
                                        cursor: 'pointer',
                                        fontSize: '16px',
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '5px',
                                        transition: 'transform 0.2s, box-shadow 0.2s',
                                        boxShadow: '0px 4px 8px rgba(0, 0, 0, 0.2)',
                                    }}
                                    onMouseEnter={(e) => {
                                        e.target.style.transform = 'translateY(-2px)';
                                        e.target.style.boxShadow = '0px 6px 12px rgba(0, 0, 0, 0.3)';
                                        e.target.style.background = 'linear-gradient(90deg, #45a049, #4CAF50)';
                                    }}
                                    onMouseLeave={(e) => {
                                        e.target.style.transform = 'translateY(0px)';
                                        e.target.style.boxShadow = '0px 4px 8px rgba(0, 0, 0, 0.2)';
                                        e.target.style.background = 'linear-gradient(90deg, #4CAF50, #45a049)';
                                    }}
                                    onClick={() => handleShare(friend.id)}
                                >
                                Invite
                                </button>

                                

                            </li>
                        ))
                    ) : (
                        <li style={{ marginTop: '2vh' }}>No friends found.</li>
                    )}
                </ul>
            </div>
            </ModalBody>
        </Modal>
    );
}

export default FriendsModal;