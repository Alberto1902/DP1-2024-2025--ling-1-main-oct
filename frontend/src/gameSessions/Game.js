import React, { useState, useEffect } from 'react';
import gameImage from '../static/images/board.png';
import { useNavigate } from 'react-router-dom';
import tokenService from '../services/token.service';
import gameService from '../services/gameService';
import '../static/css/game/gameSession.css';
import getIdFromUrl from '../util/getIdFromUrl';
import useIntervalFetchState from '../util/useIntervalFetchState';
import { Button } from 'reactstrap';
import CardContainer from './Card';
import Chat from './ChatWS';
import Dice from './Dice';
import Edges from './Edges';
import Piece from './Piece';
import HandModal from './HandModal';
import BagModal from './BagModal';
import JumpModal from './JumpModal';
import DiscardModal from './DiscardModal';
import FriendsModal from './FriendsModal';
import HighlightedSquare from './HighlightedSquare';
import { Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from 'reactstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import { apiFetch, renderPlayerItem, handleDeck } from './Utils';
import "./escape/escape.css";
import EndGameModal from './endgame/EndGameModal';
import FightModal from './fight/FightModal';
import InfoModal from './InfoModal';
import PreStealModal from './fight/PreStealModal';
import StealModal from './fight/StealModal';
import EscapeModal from './escape/EscapeModal';

const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function Game() {
    const navigate = useNavigate();
    const [squares, setSquares] = useState([]);
    const [edges, setEdges] = useState(new Set());
    const gameSessionId = getIdFromUrl(2);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [selectedPiece, setSelectedPiece] = useState(null);
    const [isChatOpen, setChatOpen] = useState(false);
    const [highlightedSquares, setHighlightedSquares] = useState([]);
    const [gameSession, setGameSession] = useIntervalFetchState({}, `/api/v1/gamesessions/game?id=${gameSessionId}&userId=${user.id}`, jwt, setMessage, setVisible, null, 1000);
    const [fight, setFight] = useIntervalFetchState({}, `/api/v1/gamesessions/currentFight?gameId=${gameSessionId}`, jwt, setMessage, setVisible, null, 1000);
    const [isFightModalOpen, setIsFightModalOpen] = useState(false);
    const [pieces, setPieces] = useIntervalFetchState([], `/api/v1/pieces/${gameSessionId}`, jwt, setMessage, setVisible, null, 1000);
    const [myPiece, setMyPiece] = useIntervalFetchState({}, `/api/v1/pieces/${user.id}/${gameSessionId}`, jwt, setMessage, setVisible, null, 1000);
    const [discardDeck, setDiscardDeck] = useIntervalFetchState({}, `/api/v1/decks/deck?isDiscard=true&gameSessionId=${gameSessionId}`, jwt, setMessage, setVisible, null, 1000);
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const toggleDropdown = () => setDropdownOpen(!dropdownOpen);
    const [isSpectator, setIsSpectator] = useState(null);
    const [isInformationModalOpen, setInformationModalOpen] = useState(false);
    const [myHand, setMyHand] = useState(myPiece.hand);
    const [myBag, setMyBag] = useState(myPiece.bag);
    const [toDiscard, setToDiscard] = useState(false);
    const [isHandModalOpen, setIsHandModalOpen] = useState(false);
    const [isBagModalOpen, setIsBagModalOpen] = useState(false);
    const [isDiscardModalOpen, setIsDiscardModalOpen] = useState(false);
    const [isFriendModalOpen, setIsFriendModalOpen] = useState(false);
    const [isJumpModalOpen, setIsJumpModalOpen] = useState(false);
    const [possibleJumps, setPossibleJumps] = useState([]);
    const [isPreStealModalOpen, setIsPreStealModalOpen] = useState(false);
    const [isStealModalOpen, setIsStealModalOpen] = useState(false);
    const [isDieRolling, setIsDieRolling] = useState(true);
    const [isEndGameModalOpen, setIsEndGameModalOpen] = useState(false);
    const [attacker, setAttacker] = useIntervalFetchState({}, `/api/v1/pieces/attacker?gameId=${gameSession.id}`, jwt, setMessage, setVisible, null, 1000);
    const [defender, setDefender] = useIntervalFetchState({}, `/api/v1/pieces/defender?gameId=${gameSession.id}`, jwt, setMessage, setVisible, null, 1000);
    const [isEscapeModalOpen, setIsEscapeModalOpen] = useState(false);
    const [diceEscape, setDiceEscape] = useState(null);
    const [isModalManuallyClosed, setIsModalManuallyClosed] = useState(false);
    const [flag, setFlag] = useState(false);
    useEffect(() => {
        const fetchSquares = async () => {
            try {
                const squaresData = await apiFetch(`/api/v1/squares/dto`);
                setSquares(squaresData);

                const uniqueEdges = new Set();
                squaresData.forEach(square => {
                    const neighbors = square.adyacentSquaresId.split(',').map(id => parseInt(id));
                    neighbors.forEach(neighborId => {
                        const edgeKey = [square.id, neighborId].sort().join('-');
                        uniqueEdges.add(edgeKey);
                    });
                });
                setEdges(uniqueEdges);
            } catch (error) {
                console.error('Error fetching squares:', error);
            }
        };

        fetchSquares();
    }, [jwt]);

    const handleHomeRedirect = () => {
        navigate('/');
    };

    useEffect(() => {
        if (gameSession.status === "IN_PROGRESS" && gameSession.turn === myPiece.playerOrder && myPiece.state.stateType === "BAG") {
            setIsHandModalOpen(true); 
        } else {
            setIsHandModalOpen(false);
        }
    }, [gameSession, myPiece]);

    useEffect(() => {
        if (gameSession.status === "IN_PROGRESS" && gameSession.turn === myPiece.playerOrder && myPiece.state.stateType === "WORD") {
            setIsBagModalOpen(true);
            setIsDieRolling(false);

        } else {
            setIsBagModalOpen(false);
        }
    }, [gameSession, myPiece]);

    useEffect(() => {
        if (gameSession.status === "IN_PROGRESS" && gameSession.turn === myPiece.playerOrder && myPiece.state.stateType === "DISCARD") {
            setIsDiscardModalOpen(true);
            setIsDieRolling(false);
        } else {
            setIsDiscardModalOpen(false);
        }
    }, [gameSession, myPiece]);



    const handlePieces = async (gameSession) => {
        if (gameSession.status === 'WAITING') {
            try {
                const piecesData = await apiFetch(`/api/v1/pieces/${gameSession.id}`);
                if (piecesData.length === 0) {
                    await apiFetch(`/api/v1/pieces?gameId=${gameSession.id}`, 'POST', gameSession);
                    setFlag(true);
                }
            } catch (error) {
                console.error('Error handling pieces:', error);
            }
        }
    };
    const handlePossibleJumps = async (possibleJumps) => {
        setPossibleJumps(possibleJumps);
        setHighlightedSquares(possibleJumps.map(jump => jump.id));
    };

    const handleNonPlayerPosition = async (gameSession) => {
        await apiFetch(`/api/v1/pieces/initialPositionNonPlayer/${gameSession.id}`, 'PUT');
    };

    const handleTurn = async (gameSession) => {
        await apiFetch(`/api/v1/gamesessions/game/nextTurn?id=${gameSession.id}`, 'PUT');
        setIsDieRolling(true);
    };

    const handlePhase = async (gameSession) => {
        await apiFetch(`/api/v1/pieces/endActionPhase?id=${myPiece.id}`, 'PUT');
    };

    const handleStatus = async (gameSession) => {
        try {
            await apiFetch(`/api/v1/gamesessions/game/changeStatus?id=${gameSession.id}`, 'PUT', gameSession);
        } catch (error) {
            console.error('Error handling status:', error);
        }
    };

    const dealCards = async (gameSession, player) => {
        try {
            const cards = await apiFetch(`/api/v1/decks/initialCards?gameSessionId=${gameSession.id}`, 'PUT', gameSession);
            await apiFetch(`/api/v1/pieces/receiveInitialCards?id=${player.id}`, 'PUT', cards);
        } catch (error) {
            console.error('Error handling deal:', error);
        }
    };

    const handleDice = async (gameSession) => {
        if (gameSession.turn === myPiece.playerOrder && gameSession.turn !== gameSession.currentPlayers - 1) {
            await dealCards(gameSession, myPiece);
            await handleTurn(gameSession);
        } else if (gameSession.turn === myPiece.playerOrder && gameSession.turn === gameSession.currentPlayers - 1) {
            await dealCards(gameSession, myPiece);
            await handleTurn(gameSession);
            await handleNonPlayerPosition(gameSession);
        }
    }

    useEffect(() => {
        setMyHand(myPiece.hand);
    }, [myPiece.hand]);

    useEffect(() => {
        setMyBag(myPiece.bag);
    }, [myPiece.bag]);

    useEffect(() => {
        if (gameSession.status === "FINISHED" && !isEscapeModalOpen) {
            setIsEndGameModalOpen(true);
        }
    }, [gameSession, isEscapeModalOpen]);


    const handleDiscardDone = (toDiscard) => {
        setToDiscard(toDiscard);
    }


    async function handleDrawCard() {
        try {
            console.log(myPiece)
            const card = await apiFetch(`/api/v1/decks/takeOneCard?isDiscard=false&gameSessionId=${gameSession.id}`, 'PUT');
            await apiFetch(`/api/v1/pieces/receiveCard?id=${myPiece.id}`, 'PUT', card);
        } catch (error) {
            console.error('Error al sacar una carta:', error);
        }
    }

    const handlerStartGameClick = async () => {
        handlePieces(gameSession);
        handleStatus(gameSession);
        handleDeck(gameSession);
    }

    const movePiece = async (piece, targetSquareId) => {
        setHighlightedSquares([]);
        try {
            if (myPiece.state.stateType === "ACTION") {
                const targetPieces = await apiFetch(`/api/v1/pieces/piecesInSquare?gameSessionId=${gameSessionId}&squareId=${targetSquareId}`, 'GET');
                await apiFetch(`/api/v1/pieces/move?position=${targetSquareId}&id=${piece.id}`, 'PUT');
                if (targetPieces.length > 0 && targetSquareId !== 1) {
                    await handleFight(piece, targetPieces);
                }
            }
        } catch (error) {
            console.error('Error moving piece:', error);
        }
    };

    useEffect(() => {
        if (Object.keys(fight).length !== 0 && fight.attacker && !isModalManuallyClosed && !isFightModalOpen) {
            setIsFightModalOpen(true);
        }
    }, [fight, isModalManuallyClosed, isFightModalOpen]);

    useEffect(() => {
        if (Object.keys(fight).length !== 0) {
            setIsModalManuallyClosed(false);
        }
    }, [fight]);

    useEffect(() => {
        if (fight.winner === myPiece && attacker.user && defender.user) {
            setIsPreStealModalOpen(true);
        }
    }, [fight, myPiece, attacker, defender]);

    const handleFight = async (piece, targetPieces) => {
        for (const targetPiece of targetPieces) {
            if (piece.id !== targetPiece.id && (piece.user || targetPiece.user) && gameSession.status === "IN_PROGRESS") {
                await apiFetch(`/api/v1/pieces/attacking?pieceId=${piece.id}`, 'PUT');
                await apiFetch(`/api/v1/pieces/defending?pieceId=${targetPiece.id}`, 'PUT');

                const fightData = await apiFetch(
                    `/api/v1/fights/${piece.id}-${targetPiece.id}?gameId=${gameSessionId}`,
                    'POST'
                );

                const looser = fightData
                    ? (fightData.winner.id === fightData.attacker.id ? fightData.defender : fightData.attacker)
                    : null;

                if (looser !== null) {
                    await new Promise((resolve) => {
                        setIsFightModalOpen(true);

                        const handleModalClose = () => {
                            setIsFightModalOpen(false);
                            resolve();
                        };

                        document.addEventListener('modalClosed', handleModalClose, { once: true });
                    });

                    await handleCatapulted(looser.id);
                }
            }
        }
    };

    useEffect(() => {
        if (isFightModalOpen) {
            const timer = setTimeout(async () => {
                closeModal()
            }, 5000);
            return () => clearTimeout(timer);
        }
    }, [isFightModalOpen]);


    const handlePieceClick = (piece) => {
        if (gameSession.turn === myPiece.playerOrder && myPiece.state.stateType === "ACTION" && ((piece.user && piece.user.id === user.id) || !piece.user)) {
            setSelectedPiece(piece);
            setHighlightedSquares([]);
            const currentSquare = squares.find(sq => sq.id === piece.position?.id);
            if (currentSquare) {
                const neighbors = currentSquare.adyacentSquaresId.split(',').map(id => parseInt(id));
                setHighlightedSquares(neighbors);
            }
        }
    };

    const handleActionPoints = async () => {
        await apiFetch(`/api/v1/pieces/defineActionPoints?id=${myPiece.id}`, 'PUT');
    };

    const handleSquareClick = (squareId) => {
        if (highlightedSquares.includes(squareId) && selectedPiece) {
            movePiece(selectedPiece, squareId);
        } else {
            console.error(`Square with id ${squareId} not found or is not highlighted`);
        }
    };

    useEffect(() => {
        const delay = 5000;

        const timer = setTimeout(() => {
            const isGameSessionEmpty =
                !gameSession ||
                (typeof gameSession === 'object' && Object.keys(gameSession).length === 0);

            if (isGameSessionEmpty) {

                navigate('/');
            }
        }, delay);

        return () => clearTimeout(timer);
    }, [gameSession, navigate]);

    async function escape(piece) {
        try {
            const diceEscapeValue = await apiFetch(`/api/v1/pieces/escape?id=${piece.id}`, 'PUT');
            setDiceEscape(diceEscapeValue);
            setIsEscapeModalOpen(true);
            await new Promise((resolve) => {

                const handleEscapeModalClose = () => {
                    setIsEscapeModalOpen(false);
                    resolve();
                };

                document.addEventListener('EscapeModalClosed', handleEscapeModalClose, { once: true });
            })
            if (diceEscapeValue >= piece.strength) {
                handleCatapulted(piece.id)
            }
        } catch (error) {
            alert('You cannot escape');
        }
    }

    async function handleCatapulted(pieceId) {
        try {
            const response = await apiFetch(`/api/v1/pieces/catapulted?id=${pieceId}`, 'PUT');
            const pieces = await apiFetch(`/api/v1/pieces/piecesInSquare?gameSessionId=${gameSessionId}&squareId=${response.position.id}`, 'GET');
            if (pieces.length > 1) {
                handleFight(response, pieces);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    const closeModal = async () => {
        if (isFightModalOpen) {
            try {
                await apiFetch(`/api/v1/gamesessions/removeCurrentFight?gameId=${gameSessionId}`, 'PUT');
                setIsFightModalOpen(false);
                setIsModalManuallyClosed(true);
                document.dispatchEvent(new Event('modalClosed'));
    
                if (gameSession.turn === myPiece.playerOrder && myPiece.state.stateType === "BAG") {
                    setIsHandModalOpen(true);
                }
            } catch (error) {
                console.error('Error closing fight modal:', error);
            }
        }
    };


    useEffect(() => {
        if (Object.keys(fight).length !== 0 && fight.attacker && !isModalManuallyClosed && !isFightModalOpen) {
            setIsFightModalOpen(true);
        }
    }, [fight, isModalManuallyClosed, isFightModalOpen]);

    const closeEscapeModal = () => {
        document.dispatchEvent(new Event('EscapeModalClosed'));
        setIsEscapeModalOpen(false);
    };
    useEffect(() => {
        if (myPiece?.bag) {
            setMyBag(myPiece.bag);
        }
    }, [myPiece]);

    // Verificación periódica de límite de tiempo para Casual Gamers
    useEffect(() => {
        if (!gameSession?.id || !user?.id || gameSession?.status !== 'IN_PROGRESS') {
            return;
        }

        const checkTimeLimit = async () => {
            try {
                const result = await gameService.checkPlayerTimeStatus(gameSession.id, user.id);
                if (result === 'KICKED_TIME_EXCEEDED') {
                    setMessage('Has sido expulsado de la partida por exceder el límite de tiempo de 1 minuto (Casual Gamer)');
                    setVisible(true);
                    // Redirigir después de un breve delay
                    setTimeout(() => {
                        navigate('/game-sessions');
                    }, 3000);
                }
            } catch (error) {
                console.error('Error checking time limit:', error);
            }
        };

        // Verificar cada 10 segundos
        const interval = setInterval(checkTimeLimit, 10000);
        
        return () => clearInterval(interval);
    }, [gameSession?.id, gameSession?.status, user?.id, navigate]);


    useEffect(() => {
        const fetchMyPiece = async () => {
            try {
                const piece = await apiFetch(`/api/v1/pieces/${user.id}/${gameSessionId}`);
                if (piece) {
                    setMyPiece(piece);
                    setMyBag(piece?.bag || []);
                } else {
                    console.warn('Piece not found or is null');
                }
            } catch (error) {
                console.error('Error fetching my piece:', error);
            }
        };


        fetchMyPiece();

    }, [gameSessionId, user?.id, gameSession?.status]);


    return (
        <div className="game-container">

            <div className="upper-bar-container">
                <button onClick={handleHomeRedirect} className="home-button"></button>
                <button onClick={() => setChatOpen(true)} className="chat-button"></button>
                <div className='dropdown-container' style={{ zIndex: 1001 }}>
                    <Dropdown isOpen={dropdownOpen} toggle={toggleDropdown}>
                        <DropdownToggle tag="button" className="invite-button">
                        </DropdownToggle>
                        <DropdownMenu>
                            {gameSession.status === 'WAITING' && <DropdownItem onClick={() => {
                                setIsSpectator(false)
                                setIsFriendModalOpen(true)
                            }}>
                                Invite Player
                            </DropdownItem>}
                            <DropdownItem onClick={() => {
                                setIsSpectator(true)
                                setIsFriendModalOpen(true)
                            }}>
                                Invite Spectator
                            </DropdownItem>
                        </DropdownMenu>
                    </Dropdown>
                </div>
                <div className="players-container">
                    <ul>
                        {pieces.length > 0 ? (
                            pieces.filter(piece => piece.user != null).map((piece) => {
                                const isCurrentTurn = piece.playerOrder === gameSession.turn;
                                return renderPlayerItem(piece.user, isCurrentTurn, piece.word);
                            })
                        ) : (
                            gameSession.players && gameSession.players.map((player) => {
                                const isCurrentTurn = myPiece.playerOrder === gameSession.turn;
                                return renderPlayerItem(player, isCurrentTurn, "");
                            })
                        )}
                    </ul>
                </div>
            </div>
            <div className="game-page-container" style={{ marginTop: '10vh' }}> </div>
            {gameSession.status === "IN_PROGRESS" && (
                <div className="turn-indicator" style={{
                    position: 'absolute',
                    top: '12vh',
                    left: '15px',
                    padding: '10px 20px',
                    backgroundColor: '#2c3e50',
                    color: '#ecf0f1',
                    borderRadius: '8px',
                    fontSize: '1.5em',
                    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)',
                    zIndex: 1000,
                }}>
                    {gameSession.turn === myPiece.playerOrder
                        ? "YOUR TURN"
                        : `${pieces.find(p => p.playerOrder === gameSession.turn)?.user?.username}'s turn`}

                </div>)}
            {myPiece.position !== null && gameSession.status === "IN_PROGRESS" && gameSession.turn === myPiece.playerOrder &&
                <div className="card-interaction-container">
                    <div className="card-image-container">
                        <img
                            src={'/card.png'}
                            alt="Agregar carta"
                            onClick={handleDrawCard}
                            style={{
                                width: '20vh',
                                height: 'auto',
                                borderRadius: '5%'
                            }}
                        />
                    </div>
                </div>}
            <div className="card-interaction-container">
                <div className="card-discard-image-container">
                    <img
                        src={discardDeck.cards?.[discardDeck.cards.length - 1]?.image || '/card.png'}
                        alt="Agregar carta"
                        style={{
                            width: '20vh',
                            height: 'auto',
                            borderRadius: '5%'
                        }}
                    />
                </div>
            </div>

            {myPiece.position !== null && gameSession.turn === myPiece.playerOrder && myPiece.state?.stateType === "ACTION" && myPiece.actionPoints > 0 &&
                <button onClick={() => escape(myPiece)} className="escape-button">
                    Escape
                </button>
            }

            <EndGameModal
                isOpen={isEndGameModalOpen}
                winner={gameSession.winner}
                user={user}
                onClose={() => setIsEndGameModalOpen(false)}
            />

            <EscapeModal
                isOpen={isEscapeModalOpen}
                diceEscape={diceEscape}
                myPiece={myPiece}
                onClose={closeEscapeModal}
            />

            <div className="game-board-container">
                <img src={gameImage} alt="Game board" className="game-board-image" />
                <>
                    {pieces.map((piece) => {
                        const square = squares.find(sq => sq.id === piece.position?.id);
                        if (!square) return null;

                        const piecesOnSameSquare = pieces.filter(p => p.position?.id === square.id);

                        const totalPieces = piecesOnSameSquare.length;
                        let offsetX = 0;
                        let offsetY = 0;

                        if (totalPieces > 1) {
                            const pieceIndex = piecesOnSameSquare.findIndex(p => p.id === piece.id);
                            const angleStep = 360 / totalPieces;
                            const radius = 2.5;
                            const angle = pieceIndex * angleStep;
                            offsetX = radius * Math.cos((angle * Math.PI + 90) / 180);
                            offsetY = radius * Math.sin((angle * Math.PI + 90) / 180);
                        }
                        return (
                            <Piece
                                key={piece.id} piece={piece} square={square} offsetX={offsetX} offsetY={offsetY}
                                gameSession={gameSession} player={myPiece} handlePieceClick={handlePieceClick} user={user}
                            />
                        );
                    })}

                    {highlightedSquares.map((squareId) => {
                        const square = squares.find(sq => sq.id === squareId);
                        if (!square) return null;

                        return (
                            <HighlightedSquare
                                key={square.id}
                                square={square}
                                handleSquareClick={() => handleSquareClick(square.id)}
                                selectedPiece={selectedPiece}
                            />
                        );
                    })}

                    <Edges edges={edges} squares={squares} />
                </>
            </div>
            <div className="start-button-overlay" style={{ marginTop: '10vh' }}>
                {gameSession?.creator?.id === user.id && gameSession.status === 'WAITING' && gameSession?.currentPlayers === gameSession?.maxPlayers &&
                    <Button onClick={handlerStartGameClick} className="start-game-button">Start Game</Button>}
            </div>

            <div>
                {gameSession.status === "IN_PROGRESS" && myPiece.position !== null && myPiece.playerOrder === gameSession.turn &&
                    <Button
                        onClick={() => setInformationModalOpen(!isInformationModalOpen)}
                        className="information-button"
                    >
                        {myPiece.state?.stateType}
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            height="20"
                            viewBox="0 96 960 960"
                            width="20"
                            fill="#c57000"
                            style={{ marginLeft: '8px', verticalAlign: 'middle' }}
                        >
                            <path d="M480 896q17 0 28.5-11.5T520 856q0-17-11.5-28.5T480 816q-17 0-28.5 11.5T440 856q0 17 11.5 28.5T480 896Zm-40-200h80q0-33 4-54t20-43q14-19 43-41 35-27 50-56.5T608 452q0-75-49.5-113T480 301q-59 0-99.5 27T336 392l66 28q12-23 34.5-39.5T480 364q38 0 58.5 19.5T559 438q0 21-10 38t-36 33q-36 27-55.5 51T439 598q-6 18-8 39t-1 59Zm40 280q-83 0-155.5-31.5T237 844q-54-54-85.5-126.5T120 562q0-83 31.5-155.5T237 280q54-54 126.5-85.5T480 163q83 0 155.5 31.5T762 280q54 54 85.5 126.5T879 562q0 83-31.5 155.5T762 844q-54 54-126.5 85.5T480 976ZM480 562ZM480 876q142 0 239-97t97-239q0-142-97-239t-239-97q-142 0-239 97T144 540q0 142 97 239t239 97Z" />
                        </svg>
                    </Button>
                }
            </div>
            <div className="game-buttons">
                {gameSession.status === "IN_PROGRESS" && gameSession.turn === myPiece.playerOrder && myPiece.position !== null && myPiece.word && myPiece.state.stateType === "ACTION" && <Button onClick={() => setIsJumpModalOpen(true)} className='napoleonic-button'>Jump</Button>}
                {gameSession.status === "IN_PROGRESS" && gameSession.turn === myPiece.playerOrder && myPiece.position !== null && myPiece.state.stateType === "ACTION" && <Button onClick={() => handlePhase(gameSession)} className='napoleonic-button'>End turn</Button>}
            </div>
            <div className="game">
                {gameSession.status === "IN_PROGRESS" && gameSession.turn === myPiece.playerOrder && myPiece.state.stateType === "DRAW" && <Button onClick={() => handleActionPoints()} className='napoleonic-button'>Stop drawing</Button>}
            </div>
            <div className="game-board-container">
                {gameSession.status === "IN_PROGRESS" && gameSession.turn === myPiece.playerOrder && myPiece.state.stateType === "BAG" && !isFightModalOpen && !isEscapeModalOpen && !isPreStealModalOpen && !isStealModalOpen && Object.keys(fight) && (
                    <HandModal myHand={myHand} myPiece={myPiece} isOpen={isHandModalOpen} toggle={() => setIsHandModalOpen(!isHandModalOpen)} />
                )}
            </div>
            <div className="game-board-container">
                {gameSession.status === "IN_PROGRESS" && gameSession.turn === myPiece.playerOrder && !isDieRolling && myPiece.state.stateType === "WORD" && (
                    <BagModal myBag={myBag} myPiece={myPiece} isOpen={isBagModalOpen} toggle={() => setIsBagModalOpen(!isBagModalOpen)} onDiscardDone={handleDiscardDone} />
                )}
            </div>
            <div className="game-board-container">
                {gameSession.status === "IN_PROGRESS" && isJumpModalOpen && (
                    <JumpModal myBag={myBag} myPiece={myPiece} isOpen={isJumpModalOpen} toggle={() => setIsJumpModalOpen(!isJumpModalOpen)} result={handlePossibleJumps} />
                )}
            </div>
            <div className="game-board-container">
                {gameSession.status === "IN_PROGRESS" && gameSession.turn === myPiece.playerOrder && myPiece.state.stateType === "DISCARD" && (
                    <DiscardModal toDiscard={toDiscard} myPiece={myPiece} isOpen={isDiscardModalOpen} toggle={() => setIsDiscardModalOpen(!isDiscardModalOpen)} />
                )}
            </div>
            <div className="game-board-container">
                <FriendsModal user={user} isOpen={isFriendModalOpen} toggle={() => setIsFriendModalOpen(!isFriendModalOpen)} isSpectator={isSpectator} gameId={gameSessionId} />
            </div>

            <div className="game-board-container">
                <InfoModal isOpen={isInformationModalOpen} toggle={() => setInformationModalOpen(!isInformationModalOpen)} state={myPiece.state?.stateType} />
            </div>

            <CardContainer myHand={myHand} />
            {gameSession.chatRoom && (
                <Chat chatRoomId={gameSession.chatRoom.id}
                    isOpen={isChatOpen}
                    onClose={() => setChatOpen(false)} />
            )}
            {gameSession.turn === myPiece.playerOrder && myPiece.position === null && (
                <div>
                    <div className="game">
                        <Dice handleDice={() => handleDice(gameSession)}
                            myPiece={myPiece}
                        />
                    </div>
                </div>
            )}
            <FightModal
                isOpen={isFightModalOpen && fight}
                fight={fight}
                onClose={closeModal}
            />

            <PreStealModal
                gameId={gameSessionId}
                isOpen={isPreStealModalOpen}
                toggle={() => setIsPreStealModalOpen(!isPreStealModalOpen)}
                toggleStealModal={() => setIsStealModalOpen(!isStealModalOpen)}
                fight={fight}
            />
            <StealModal
                gameId={gameSessionId}
                isOpen={isStealModalOpen}
                toggle={() => setIsStealModalOpen(!isStealModalOpen)}
                fight={fight}
            />
            {/* {loserPiece && loserPiece.user && loserPiece.user.id === user.id && (
                <div>
                    <div className="game">
                        <Die handleDice={() => handleDie(gameSession)}
                            myPiece={loserPiece}
                        />
                    </div>
                </div>
            )} */}
        </div>

    )
}