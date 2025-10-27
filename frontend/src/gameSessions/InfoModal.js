import React, { useState } from "react";
import { Modal, ModalBody, ModalHeader, Button } from 'reactstrap';
import '../static/css/game/Card.css';
import '../static/css/game/HandModal.css';
import tokenService from '../services/token.service';

const jwt = tokenService.getLocalAccessToken();

const InfoModal = ({isOpen, toggle, state}) => {


    return (
        <Modal isOpen={isOpen} toggle={toggle}>
            <ModalHeader toggle={toggle}>{state} Phase:</ModalHeader>
            <ModalBody>
                {state === 'DRAW' &&
            <div style={{ padding: '10px', overflowY: 'auto', maxHeight: '100vh', flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-start' }}>
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-start' }}>
                    <h5>How to Play:</h5>
                    <p>1. Click on the deck icon to receive a card</p>
                    <p>2. Be careful! You can only draw up to 7 cards and they will determine your action points.</p>
                    <p>3. When you have finished drawing, click on STOP DRAWING.</p>
                </div>
            </div>
            }

            {state === 'ACTION' &&
            <div style={{ padding: '10px', overflowY: 'auto', maxHeight: '100vh', flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-start' }}>
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-start' }}>
                    <h5>How to Play:</h5>
                    <h5>1. During the action phase you have different ways to spend your action points.</h5>
                    <p>1.1. Click on your piece to move it to a neighboring room.</p>
                    <p>1.2. Click on a non-player piece to move it to a neighboring room</p>
                    <p>1.3. Click on the 'JUMP' button to catapult yourself to a room if you can form one word from its name with your bag.</p>
                    <p>1.4. Click on the 'ESCAPE' button to try to win the game!.</p>
                    <h5>2. Click on end turn if you have finished and do not want to spend the rest of your action points.</h5>
                </div>
            </div>
            }
            </ModalBody>
        </Modal>
    );
}

export default InfoModal;