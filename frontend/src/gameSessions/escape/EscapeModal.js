import React from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import Die from '../Die';
import '../aaa.css';

export default function EscapeModal({ isOpen, diceEscape, myPiece, onClose }) {
    const escapeSuccess = diceEscape < myPiece.strength;

    return (
        <Modal isOpen={isOpen} toggle={onClose} className="escape-modal">
            <ModalHeader toggle={onClose} className="escape-modal-header">
                Escape Attempt
            </ModalHeader>
            <ModalBody className="escape-modal-body">
                <div className="escape-info">
                    <div className="dice-row" style={{ display: 'flex', justifyContent: 'center', marginBottom: '1.5rem' }}>
                        <div className="dice-column" style={{ textAlign: 'center' }}>
                            <p className="dice-label"><strong>Escape Die Roll:</strong></p>
                            <Die initialValue={diceEscape} className="die-style" />
                        </div>
                    </div>
                    <div className="escape-result" style={{ textAlign: 'center', marginTop: '2rem' }}>
                        <p style={{ fontSize: '1.1rem'}}>
                            {escapeSuccess ? 'Congratulations! You successfully escaped from Elba!' :
                                'Escape attempt failed. Better luck next time!'}
                        </p>
                    </div>
                </div>
            </ModalBody>
            <ModalFooter className="escape-modal-footer">
                <Button color="primary" onClick={onClose} className="close-button">
                    Close
                </Button>
            </ModalFooter>
        </Modal>
    );
}
