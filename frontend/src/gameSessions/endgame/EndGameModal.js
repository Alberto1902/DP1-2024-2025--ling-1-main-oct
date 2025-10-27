import React from "react";
import { Modal, ModalHeader, ModalBody } from "reactstrap";
import { useNavigate } from 'react-router-dom';
import './endgame.css'

export default function EndGameModal({ isOpen, winner, user }) {
    const navigate = useNavigate();
    const isWinner = winner && winner.id === user.id;

    const handleHomeRedirect = () => {
        navigate('/');
    };

    return (
        <Modal isOpen={isOpen} centered backdrop="static" keyboard={false}>
            <ModalHeader>
                <h5>{isWinner ? "Congratulations!" : "Game Over"}</h5>
            </ModalHeader>
            <ModalBody>
                {isWinner ? (
                    <>
                        <p>Congratulations! You won the game. 🏆</p>
                        <p>Your strategy was flawless. Enjoy your victory!</p>
                    </>
                ) : (
                    <>
                        <p>{winner?.username} escaped from Elba successfully. 😞</p>
                        <p>Better luck next time!</p>
                    </>
                )}
                <div style={{
                    display: "flex",
                    justifyContent: "center",
                    marginTop: "20px"
                }}>
                    <button
                        onClick={handleHomeRedirect}
                        style={{
                            backgroundColor: "#f0f0f0",
                            border: "2px solid #ddd",
                            padding: "12px",
                            borderRadius: "50%",
                            cursor: "pointer",
                            transition: "background-color 0.3s",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center"
                        }}
                        onMouseOver={e => e.target.style.backgroundColor = "#e0e0e0"}
                        onMouseOut={e => e.target.style.backgroundColor = "#f0f0f0"}
                    >
                        <img
                            src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='black' width='24px' height='24px'%3E%3Cpath d='M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z'/%3E%3C/svg%3E"
                            alt="Home"
                            style={{ width: "24px", height: "24px" }}
                        />
                    </button>
                </div>
            </ModalBody>
        </Modal>
    );
}
