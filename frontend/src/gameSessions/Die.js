import React, { useEffect, useRef } from 'react';
import './aaa.css';

const Die = ({ initialValue }) => {
    const diceRef = useRef(null);

    useEffect(() => {
        if (initialValue) {
            animateDice(diceRef, initialValue);
        }
    }, [initialValue]);

    const animateDice = (diceRef, value) => {
        if (!diceRef.current) return;

        // Aplicar animación inicial
        diceRef.current.style.animation = 'rolling 1s';
        setTimeout(() => {
            if (!diceRef.current) return; // Verificar de nuevo si la referencia es válida

            switch (value) {
                case 1:
                    diceRef.current.style.transform = 'rotateX(0deg) rotateY(0deg)';
                    break;
                case 6:
                    diceRef.current.style.transform = 'rotateX(180deg) rotateY(0deg)';
                    break;
                case 2:
                    diceRef.current.style.transform = 'rotateX(-90deg) rotateY(0deg)';
                    break;
                case 5:
                    diceRef.current.style.transform = 'rotateX(90deg) rotateY(0deg)';
                    break;
                case 3:
                    diceRef.current.style.transform = 'rotateX(0deg) rotateY(90deg)';
                    break;
                case 4:
                    diceRef.current.style.transform = 'rotateX(0deg) rotateY(-90deg)';
                    break;
                default:
                    break;
            }

            // Detener la animación después de que se complete
            diceRef.current.style.animation = 'none';
        }, 1000);
    };

    return (
        <div className="diced-container">
            <div ref={diceRef} className="diced diced-one">
                <div className="face-two front-two"></div>
                <div className="face-two back-two"></div>
                <div className="face-two top-two"></div>
                <div className="face-two bottom-two"></div>
                <div className="face-two right-two"></div>
                <div className="face-two left-two"></div>
            </div>
        </div>
    );
};

export default Die;
