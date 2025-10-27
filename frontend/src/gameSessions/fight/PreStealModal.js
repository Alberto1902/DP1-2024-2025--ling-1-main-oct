import React from 'react';
import { Modal, ModalBody, ModalHeader, Button } from 'reactstrap';
import tokenService from '../../services/token.service';

const jwt = tokenService.getLocalAccessToken();

const PreStealModal = ({ gameId, isOpen, toggle, toggleStealModal, fight }) => {


    const handleStealFromHand = async () => {
        try {
            if(fight.loser.hand.length === 0) {
                throw new Error('The player has no cards in hand.');
            }
            const response = await fetch(`/api/v1/pieces/stealFromPlayerHand?gameId=${gameId}`, {
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

    const handleStealFromBag = () => {
        if(fight.loser.bag.length <= 2) {
            throw new Error('The player has no stealable cards in bag.');
        }
        toggle();
        toggleStealModal();
    };


    return (
        <Modal isOpen={isOpen} toggle={toggle}>
            <ModalHeader toggle={toggle}>
                Where do you want to steal a card from?
            </ModalHeader>
            <ModalBody>
                <Button variant="primary" onClick={handleStealFromHand} style={{ marginRight: '10px' }}>
                    Hand
                </Button>
                <Button variant="secondary" onClick={handleStealFromBag}>
                    Bag
                </Button>
            </ModalBody>
        </Modal>
    );
};

export default PreStealModal;