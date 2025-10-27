import React, { useRef } from 'react';
import './aaa.css';
import { apiFetch } from './Utils'; // Ajusta la ruta según sea necesario

const Dice = ({ handleDice, myPiece }) => {
    const dice1Ref = useRef(null);
    const dice2Ref = useRef(null);

    const rollDice = async () => {
        const getRandomDiceValue = () => Math.floor(Math.random() * 6) + 1;

        const animateDice = (diceRef, value) => {
            if (!diceRef.current) return;

            diceRef.current.style.animation = 'rolling 1s';

            setTimeout(() => {
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
                diceRef.current.style.animation = 'none';
            }, 1000);
        };

        const diceOneValue = getRandomDiceValue();
        const diceTwoValue = getRandomDiceValue();

        animateDice(dice1Ref, diceOneValue);
        animateDice(dice2Ref, diceTwoValue);

        await new Promise(resolve => setTimeout(resolve, 1000));
        await apiFetch(`/api/v1/pieces/InitialPosition/${myPiece.id}/${diceOneValue}/${diceTwoValue}`, 'PUT');
        handleDice();
    };

    return (
        <>
            <div className="dice-container">
                <div className="container">
                    <div ref={dice2Ref} className="dice dice-two">
                        <div className="face front"></div>
                        <div className="face back"></div>
                        <div className="face top"></div>
                        <div className="face bottom"></div>
                        <div className="face right"></div>
                        <div className="face left"></div>
                    </div>
                </div>
                <div className="container">
                    <div ref={dice1Ref} className="dice dice-one">
                        <div className="face-two front-two"></div>
                        <div className="face-two back-two"></div>
                        <div className="face-two top-two"></div>
                        <div className="face-two bottom-two"></div>
                        <div className="face-two right-two"></div>
                        <div className="face-two left-two"></div>
                    </div>
                </div>

            </div>
            <div id='roll' className='napoleonic-button' onClick={rollDice}>Roll the Dice</div>
        </>
    );
};

export default Dice;
