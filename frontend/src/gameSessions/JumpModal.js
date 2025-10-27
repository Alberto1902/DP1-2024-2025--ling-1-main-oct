import React, { useState } from "react";
import { Modal, ModalBody, ModalHeader, Button, Form, FormGroup, Label, Input } from 'reactstrap';
import '../static/css/game/Card.css';
import '../static/css/game/HandModal.css';
import tokenService from '../services/token.service';

const jwt = tokenService.getLocalAccessToken();

const JumpModal = ({ myBag, myPiece, isOpen, toggle, result }) => {
    const [word, setWord] = useState('');

    if (!myBag || myBag.length === 0) {
        return null; 
    }

    const handleFinishWriting = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch(`/api/v1/pieces/possibleLaunches?word=${word}&id=${myPiece.id}`, {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                },
            });
            // Verificar si la respuesta fue exitosa
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || `HTTP error! Status: ${response.status}`);
            }
            const responseData = await response.json();

            if (responseData) {
                result(responseData);
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
                
                <Form onSubmit={handleFinishWriting}>
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
                    <div className="modal-card-container">
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

export default JumpModal;