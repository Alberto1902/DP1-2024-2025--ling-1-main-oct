import { Table, Button, Modal, ModalHeader, ModalBody, ModalFooter, Form, FormGroup, Label, Input } from 'reactstrap';
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

const jwt = tokenService.getLocalAccessToken();

export default function GameSessionsList() {
    const [modal, setModal] = useState(false);
    const [pin, setPin] = useState(null);
    const [selectedSession, setSelectedSession] = useState(null);
    const navigate = useNavigate();

    const toggle = () => setModal(!modal);
    const userId = tokenService.getUser() ? tokenService.getUser().id : null;

    const [gamesessions, setGamesessions] = useFetchState(
        [],
        `/api/v1/gamesessions?status=WAITING`,
        jwt
    );

    const [activeSessions, setActiveSessions] = useFetchState(
        [],
        `/api/v1/gamesessions/active?userId=${userId}`,
        jwt
    );

    const user = tokenService.getUser();

    const handleJoinClick = (session) => {
        if (session.isPrivate) {
            setSelectedSession(session);
            toggle();
        } else {
            handleJoinSession(session, session.id, null);
        }
    };

    const handleJoinSession = (a, id, pin) => {
        if (a.isPrivate && a.pin !== pin) {
            alert('Invalid pin');
            return;
        }
        if (a.currentPlayers === a.maxPlayers) {
            alert('Game is full');
            return;
        }
        if (a.status !== "WAITING") {
            alert('Game has already started');
            return;
        }
        if (a.players.some((player) => player.id === user.id)) {
            navigate(`/gamesessions/${a.id}`);
            return;
        }
        const url = new URL(`/api/v1/gamesessions/game/join`, window.location.origin);
        url.searchParams.append('id', id);
        url.searchParams.append('userId', user.id);
        if (pin) {
            url.searchParams.append('code', pin);
        }
        fetch(
            url,
            {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                },
            }
        )
            .then((response) => response.text())
            .then((data) => {
                if (data === "")
                    navigate('/');
                else {
                    let json = JSON.parse(data);
                    navigate('/gamesessions/' + json.id);
                }
            })
            .catch((message) => {
                alert(message);
            });
    };

    const combinedSessionsList = [...activeSessions, ...gamesessions].map((a, index) => (
        <tr key={a.id}>
            <td className="text-center">{a.name}</td>
            <td className="text-center">{a.currentPlayers + "/" + a.maxPlayers}</td>
            <td className="text-center">
                {a.isPrivate ? (
                    <img
                        src="https://cdn-icons-png.flaticon.com/128/483/483408.png"
                        alt="Private"
                        style={{ width: '20px', height: '20px' }}
                    />
                ) : (
                    <img
                        src="https://cdn-icons-png.flaticon.com/128/4975/4975092.png"
                        alt="Public"
                        style={{ width: '25px', height: '25px' }}
                    />
                )}
            </td>
            <td className="text-center">
                {index < activeSessions.length ? (
                    <Button onClick={() => navigate(`/gamesessions/${a.id}`)}>Rejoin</Button>
                ) : (
                    <Button onClick={() => handleJoinClick(a)}>Join</Button>
                )}
            </td>
        </tr>
    ));

    return (
        <div>
            <div className="home-page-container">
                <div className="admin-page-container">
                    <div 
                        className="table-container" 
                        style={{ 
                            color: 'white', 
                            width: '90vh', 
                            height: '60vh', 
                            display: 'flex', 
                            justifyContent: 'center', 
                            alignItems: 'center', 
                            border: '1px solid white',
                            borderRadius: '10px',
                            overflow: 'hidden',
                        }}
                    >
                        <div 
                        >
                            <style>
                                {`
                                    .table-container div::-webkit-scrollbar {
                                        width: 12px;
                                    }
                                    .table-container div::-webkit-scrollbar-track {
                                        background: white;
                                    }
                                    .table-container div::-webkit-scrollbar-thumb {
                                        background-color: gray;
                                        border-radius: 6px;
                                        border: 3px solid white;
                                    }
                                `}
                            </style>
                            <h1 className="text-center" style={{ color: 'black' }}>Game Sessions</h1>
                            <Table aria-label="game-sessions" className="mt-4" style={{ color: 'black' }}>
                                <thead>
                                    <tr>
                                        <th className="text-center">Game Name</th>
                                        <th className="text-center">Players</th>
                                        <th className="text-center">Private</th>
                                        <th className="text-center"></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {combinedSessionsList.length > 0 ? (
                                        combinedSessionsList
                                    ) : (
                                        <tr>
                                            <td className="text-center" colSpan="4">No sessions available</td>
                                        </tr>
                                    )}
                                </tbody>
                            </Table>
                        </div>
                        <Modal isOpen={modal} toggle={toggle}>
                            <ModalHeader toggle={toggle}>Join Game</ModalHeader>
                            <ModalBody>
                                <Form>
                                    <FormGroup>
                                        <Label for="pin">Enter Pin</Label>
                                        <Input
                                            type="text"
                                            name="pin"
                                            id="pin"
                                            value={pin}
                                            onChange={(e) => setPin(e.target.value)}
                                            placeholder="Enter game pin"
                                        />
                                    </FormGroup>
                                </Form>
                            </ModalBody>
                            <ModalFooter>
                                <Button color="primary" onClick={() => handleJoinSession(selectedSession, selectedSession.id, pin)}>Join</Button>{' '}
                                <Button color="secondary" onClick={toggle}>Cancel</Button>
                            </ModalFooter>
                        </Modal>
                    </div>
                </div>
            </div>
        </div>
    );
    
}
