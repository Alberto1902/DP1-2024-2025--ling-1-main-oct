import React, { useState } from "react";
import { Modal, ModalBody, ModalHeader, Button, Form, FormGroup, Label, Input } from 'reactstrap';
import '../static/css/game/Card.css';
import '../static/css/game/HandModal.css';
import tokenService from '../services/token.service';

const jwt = tokenService.getLocalAccessToken();

const BagModal = ({ myBag, myPiece, isOpen, toggle, onDiscardDone }) => {
    const [word, setWord] = useState('');

    if (!myBag || myBag.length === 0) {
        return null;
    }

    const handleFinishDiscard = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch(`/api/v1/pieces/discardCards?id=${myPiece.id}`, {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                },
                body: word
            });
            // Verificar si la respuesta fue exitosa
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || `HTTP error! Status: ${response.status}`);
            }
            const responseData = await response.json();

            if (onDiscardDone) {
                onDiscardDone(responseData);
            }

            // Si la respuesta es correcta, continuar
            toggle();
        } catch (error) {
            alert(`Your bag must have at least 2 cards or form a valid word.`);
        }
    };


    return (
        <Modal isOpen={isOpen} toggle={toggle}>
            <ModalHeader toggle={toggle}>Form your word!</ModalHeader>
            <ModalBody>

                <Form onSubmit={handleFinishDiscard}>
                    <FormGroup>
                        <Label for="wordInput">Enter a Word</Label>
                        <Input
                            type="text"
                            name="word"
                            id="wordInput"
                            placeholder="Enter your word"
                            value={word}
                            onChange={(e) => setWord(e.target.value)}
                            required
                        />
                    </FormGroup>
                    <p>Your Bag:</p>
                    <div className="selected-cards">
                        {myBag.map((card, index) => (
                            <div className="modal-card" key={index}>
                                <img src={card.image} alt={card.title} />
                            </div>
                        ))}
                    </div>
                    <Button color="primary" type="submit">Finish</Button>
                </Form>
            </ModalBody>
        </Modal>
    );
}

export default BagModal;