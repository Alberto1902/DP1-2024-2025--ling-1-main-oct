import React, { useState } from "react";
import { Modal, ModalBody, ModalHeader, Button } from 'reactstrap';
import tokenService from '../../services/token.service';

const jwt = tokenService.getLocalAccessToken();

const StealModal = ({ gameId, isOpen, toggle, fight }) => {

    const [selectedCard, setSelectedCard] = useState([]);

    const handleCardClick = (card) => {
        if (selectedCard.id !== card.id && selectedCard.length === 0) {
            setSelectedCard(card);
        }
    };

    const handleStealFromBag = async () => {
        try {
            const response = await fetch(`/api/v1/pieces/stealFromPlayerBag?gameId=${gameId}&cardId=${selectedCard.id}`, {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                },
            })
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || `HTTP error! Status: ${response.status}`);
            }
            toggle();
        } catch (error) {
            alert(error);
        }
    };

    const CardContainerModal = ({ fight, handleCardClick }) => {
        return (
            <div className="card-container-out">
                {fight.loser.bag.map((card, index) => (
                    <div className="card-out" key={index} onClick={() => handleCardClick(card)}>
                        <img src={card.image} alt={card.title} className="card-image expanded-out" />
                    </div>
                ))}
            </div>
        );
    }


    return (
        <Modal isOpen={isOpen} toggle={toggle}>
            <ModalHeader toggle={toggle}>
                What card do you want to steal?
            </ModalHeader>
            <ModalBody>
                <CardContainerModal fight={fight} handleCardClick={handleCardClick} />
                    <div className="modal-card-container">
                        <div className="modal-card">
                            <img src={selectedCard.image} alt={selectedCard.title} />
                        </div>
                    </div>
                    <Button color="primary" onClick={handleStealFromBag}>Steal</Button>
            </ModalBody>
        </Modal>
    );
};

export default StealModal;