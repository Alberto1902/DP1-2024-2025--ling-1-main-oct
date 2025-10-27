import React from "react";
import '../static/css/game/Card.css';

const CardContainer = ({ myHand }) => {
    if (!myHand || myHand.length === 0) {
        return null; 
    }
    return (
        <div className="card-container">
            {myHand.map((card, index) => (
                <div className="card" key={index}>
                    <img src={card.letterImage} alt={card.letter} className="card-image minimized" />
                    <img src={card.image} alt={card.title} className="card-image expanded" />
                </div>
            ))}
        </div>
    );
}

export default CardContainer;