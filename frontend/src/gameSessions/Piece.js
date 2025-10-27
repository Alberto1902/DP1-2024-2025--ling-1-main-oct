import React from 'react';

const Piece = ({ piece, square, offsetX, offsetY, handlePieceClick, gameSession, user }) => {
    return (
        <div
            style={{
                position: 'absolute',
                top: `calc(${square.yposition}% - 1.9vh + ${offsetY}vh)`,
                left: `calc(${square.xposition}% - 1.9vh + ${offsetX}vh)`,
                width: '3.8vh',
                height: '3.8vh',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                transition: 'top 0.5s, left 0.5s',
            }}
        >
            {piece.strength !== undefined && (
                <span
                    style={{
                        position: 'absolute',
                        top: '-1.5vh',
                        right: '-0.7vh',
                        fontSize: '1.4vh',
                        fontWeight: '900',
                        backgroundColor: 'rgba(0, 0, 0, 0.9)', 
                        color: 'white',
                        padding: '0.3vh 0.6vh',
                        borderRadius: '6px', 
                        width: '2.2vh', 
                        textAlign: 'center',
                        zIndex: 9999,
                    }}
                >
                    {piece.strength}
                </span>
            )}

            <img
                key={piece.id}
                className="player-piece"
                src={piece.image}
                alt="piece"
                title={piece.user ? piece.user.username : ''}
                data-user={((piece.user && piece.user.id === user.id) || !piece.user) && gameSession.turn === piece.playerOrder}
                onClick={() => handlePieceClick(piece)}
                style={{
                    width: '100%',
                    height: '100%',
                    borderRadius: '50%',
                }}
            />
        </div>
    );
};

export default Piece;
