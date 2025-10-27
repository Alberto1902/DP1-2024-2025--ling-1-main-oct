import React from 'react';

const HighlightedSquare = ({ square, handleSquareClick, selectedPiece }) => {
    return (
        <div
            className="highlighted-square"
            data-square-id={square.id}
            onClick={() => handleSquareClick(square.id, selectedPiece)}
            style={{
                top: `calc(${square.yposition}% - 2.5vh)`,
                left: `calc(${square.xposition}% - 2.5vh)`,
            }}
        />
    );
};

export default HighlightedSquare;
