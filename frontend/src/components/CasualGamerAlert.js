import React from 'react';
import { Alert } from 'reactstrap';

export default function CasualGamerAlert({ limitType, isVisible, onDismiss }) {
    const getMessage = () => {
        switch (limitType) {
            case 'DAILY_LIMIT':
                return 'Los jugadores Casual Gamer pueden crear un máximo de 2 partidas por día. Has alcanzado tu límite diario.';
            case 'TIME_EXCEEDED':
                return 'Has excedido el tiempo máximo de partida (30 minutos). Has sido expulsado de la partida.';
            default:
                return 'Se ha alcanzado una limitación para jugadores Casual Gamer.';
        }
    };

    if (!isVisible) return null;

    return (
        <Alert color="warning" isOpen={isVisible} toggle={onDismiss}>
            <h4 className="alert-heading">Limitación Casual Gamer</h4>
            <p>{getMessage()}</p>
            <hr />
            <p className="mb-0">
                Puedes actualizar tu perfil a "Hard Core Gamer" para eliminar estas limitaciones.
            </p>
        </Alert>
    );
}
