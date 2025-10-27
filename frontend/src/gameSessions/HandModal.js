import React, { useState } from "react";
import { Modal, ModalBody, ModalHeader, Button } from "reactstrap";
import { DndProvider, useDrag, useDrop } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import "../static/css/game/Card.css";
import "../static/css/game/HandModal.css";
import tokenService from "../services/token.service";

const jwt = tokenService.getLocalAccessToken();

const HandModal = ({ myHand, myPiece, isOpen, toggle }) => {
    const [selectedCards, setSelectedCards] = useState([]);
    const [availableCards, setAvailableCards] = useState(myHand || []);
    const [draggingIndex, setDraggingIndex] = useState(null);

    if (!myHand || myHand.length === 0) {
        return null;
    }

    const handleCardClick = (card) => {
        if (availableCards.some((availableCard) => availableCard.id === card.id)) {
            setSelectedCards([...selectedCards, card]);
            setAvailableCards(availableCards.filter((c) => c.id !== card.id));
        }
    };



    const handleFinishDiscard = async () => {
        try {
            const response = await fetch(
                `/api/v1/pieces/putCardsInBag?id=${myPiece.id}`,
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
            alert(`You must have at most 7 cards in your hand after discarding.`);
        }
    };

    const Card = ({ card, index, moveCard }) => {
        const [, ref] = useDrag({
            type: "CARD",
            item: { index },
        });

        const [, drop] = useDrop({
            accept: "CARD",
            hover: (draggedItem) => {
                if (draggedItem.index !== index) {
                    moveCard(draggedItem.index, index);
                    draggedItem.index = index;
                }
            },
        });

        return (
            <div ref={(node) => ref(drop(node))} className="modal-card">
                <img src={card.image} alt={card.title} />
            </div>
        );
    };

    const moveCard = (fromIndex, toIndex) => {
        const updatedCards = [...selectedCards];
        const [movedCard] = updatedCards.splice(fromIndex, 1);
        updatedCards.splice(toIndex, 0, movedCard);
        setSelectedCards(updatedCards);
    };

    const CardContainerModal = ({ cards, onCardClick, containerClass }) => (
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
    const handleSelectedCardClick = (card) => {
        if (selectedCards.some((selectedCard) => selectedCard.id === card.id)) {
            setAvailableCards([...availableCards, card]);
            setSelectedCards(selectedCards.filter((c) => c.id !== card.id));
        }
    };
    const onDragEnd = () => {
        setDraggingIndex(null);
    };
    return (
        <DndProvider backend={HTML5Backend}>
            <Modal isOpen={isOpen} toggle={toggle}>
                <ModalHeader toggle={toggle}>Put cards in your bag!</ModalHeader>
                <ModalBody>
                    <p>Select cards from your hand!:</p>
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

export default HandModal;
