import React, { useState } from "react";
import { Modal, ModalBody, ModalHeader, Button } from "reactstrap";
import "../static/css/game/Card.css";
import "../static/css/game/HandModal.css";
import tokenService from "../services/token.service";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
const jwt = tokenService.getLocalAccessToken();

const DiscardModal = ({ toDiscard, myPiece, isOpen, toggle }) => {
    const [selectedCards, setSelectedCards] = useState([]);
    const [availableCards, setAvailableCards] = useState(toDiscard || []);
    const [draggingIndex, setDraggingIndex] = useState(null);

    if (!toDiscard || toDiscard.length === 0) {
        return null;
    }

    const handleCardClick = (card) => {
        if (availableCards.some((availableCard) => availableCard.id === card.id)) {
            setSelectedCards([...selectedCards, card]);
            setAvailableCards(availableCards.filter((c) => c.id !== card.id));
        }
    };

    const handleSelectedCardClick = (card) => {
        if (selectedCards.some((selectedCard) => selectedCard.id === card.id)) {
            setAvailableCards([...availableCards, card]);
            setSelectedCards(selectedCards.filter((c) => c.id !== card.id));
        }
    };

    const handleFinishDiscard = async () => {
        try {
            if (selectedCards.length !== toDiscard.length) {
                throw new Error("You must select all the cards to discard.");
            }
            const response = await fetch(
                `/api/v1/decks/discard?id=${myPiece.id}`,
                {
                    method: "PUT",
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        Accept: "application/json",
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(selectedCards),
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(
                    errorData.message || `HTTP error! Status: ${response.status}`
                );
            }

            toggle();
        } catch (error) {
            alert(error.message);
        }
    };

    const onDragStart = (index) => {
        setDraggingIndex(index);
    };

    const onDragOver = (e, index) => {
        e.preventDefault();
        if (draggingIndex === index) return;

        const newOrder = [...selectedCards];
        const draggedItem = newOrder[draggingIndex];
        newOrder.splice(draggingIndex, 1);
        newOrder.splice(index, 0, draggedItem);

        setDraggingIndex(index);
        setSelectedCards(newOrder);
    };

    const onDragEnd = () => {
        setDraggingIndex(null);
    };

    const CardContainerModal = ({ cards, onCardClick, containerClass }) => {
        return (
            <div className={containerClass}>
                {cards.map((card, index) => (
                    <div
                        className="card-out"
                        key={index}
                        onClick={() => onCardClick(card)}
                    >
                        <img
                            src={card.image}
                            alt={card.title}
                            className="card-image expanded-out"
                        />
                    </div>
                ))}
            </div>
        );
    };

    return (
        <DndProvider backend={HTML5Backend}>
            <Modal isOpen={isOpen} toggle={toggle}>
                <ModalHeader toggle={toggle}>Order the cards to discard!</ModalHeader>
                <ModalBody>
                    <CardContainerModal
                        cards={availableCards}
                        onCardClick={handleCardClick}
                        containerClass="card-container-out"
                    />
                    <div className="modal-card-container">
                        {selectedCards.map((card, index) => (
                            <div className="modal-card" key={index} onClick={() => handleSelectedCardClick(card)}
                                draggable
                                onDragStart={() => onDragStart(index)}
                                onDragOver={(e) => onDragOver(e, index)}
                                onDragEnd={onDragEnd}
                            >
                                <img src={card.image} alt={card.title} />
                            </div>
                        ))}
                    </div>
                    <Button color="primary" onClick={handleFinishDiscard}>
                        Finish
                    </Button>
                </ModalBody>
            </Modal>        
        </DndProvider>

    );
};

export default DiscardModal;
