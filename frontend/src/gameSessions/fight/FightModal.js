import React from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import Die from '../Die';
import '../aaa.css';

export default function Fight({ isOpen, fight, onClose }) {
    if (!fight || !fight.attacker || !fight.defender) return null;

    const winner = fight.winner;
    const loser = fight.winner 
        ? (fight.winner.id === fight.attacker.id ? fight.defender : fight.attacker) 
        : null;

    // Ajustar puntaje de fuerza del perdedor
    const adjustedAttackerStrength = loser?.id === fight.attacker.id ? fight.attacker.strength - 1 : fight.attacker.strength;
    const adjustedDefenderStrength = loser?.id === fight.defender.id ? fight.defender.strength - 1 : fight.defender.strength;

    return (
        <div>
            <Modal isOpen={isOpen} toggle={onClose} className="fight-modal">
                <ModalHeader toggle={onClose} className="fight-modal-header">
                    Fight in Progress
                </ModalHeader>
                <ModalBody className="fight-modal-body">
                    <div className="fight-info">
                        <div className="dice-row">
                            <div className="dice-column">
                                <p className="username">
                                    <strong>{fight.attacker.user?.username || (fight.attacker.isCampbell ? 'Nial Campbell' : 'Non-player')}</strong>
                                </p>
                                <Die initialValue={fight.attackerDiceValue} className="die-style" />
                            </div>
                            <div className="vs-separator"><strong>VS</strong></div>
                            <div className="dice-column">
                                <p className="username">
                                    <strong>{fight.defender.user?.username || (fight.defender.isCampbell ? 'Nial Campbell' : 'Non-player')}</strong>
                                </p>
                                <Die initialValue={fight.defenderDiceValue} className="die-style" />
                            </div>
                        </div>
                        <div className="dice-values">
                            <p>
                                <strong>Attacker score:</strong> {fight.attackerDiceValue + adjustedAttackerStrength}
                            </p>
                            <p>
                                <strong>Defender score:</strong> {fight.defenderDiceValue + adjustedDefenderStrength}
                            </p>
                        </div>
                        {winner && (
                            <p>
                                <strong>Winner:</strong> {winner.id === fight.attacker.id ? 'Attacker' : 'Defender'}
                            </p>
                        )}
                        {fight.canTakeDiscardPile && (
                            <p>The discard pile can be taken.</p>
                        )}
                        {fight.takeFromOtherPlayer !== null && (
                            <p>
                                {fight.takeFromOtherPlayer
                                    ? 'The attacker takes from the defender.'
                                    : 'The defender takes from the attacker.'}
                            </p>
                        )}
                    </div>
                </ModalBody>

                <ModalFooter className="fight-modal-footer">
                    <Button color="primary" onClick={onClose} className="close-button">
                        Close
                    </Button>
                </ModalFooter>
            </Modal>
        </div>
    );
}
