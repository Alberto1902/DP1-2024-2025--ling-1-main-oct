import {useState} from 'react';
import tokenService from '../services/token.service';
import { Link } from 'react-router-dom';
import { Form, Input, Label } from 'reactstrap';
import getErrorModal from '../util/getErrorModal';
import { useNavigate } from 'react-router-dom';
import { Button } from 'reactstrap';

const jwt = tokenService.getLocalAccessToken();


export default function GameSessionCreate() {
    const user = tokenService.getUser();
    const emptyGameSession = {
        name: '',
        maxPlayers: 3,
        isPrivate: false,
        pin: null,
        creator: user,
        chatRoom: null
    };

    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [isChecked, setIsChecked] = useState(false);

    const [gameSession, setGameSession] = useState(emptyGameSession);

    const modal = getErrorModal(setVisible, visible, message);
    const navigate = useNavigate();

    function handleSubmit(event) {
        event.preventDefault();
        if(!handleValidation()){
            return;
        }
        fetch(
            `/api/v1/gamesessions`,
            {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(gameSession),
            }
        )
        .then((response) => response.text())
        .then((data) => {
            if(data==="")
                navigate('/');
            else{
                let json = JSON.parse(data);
                
                if(json.message){
                    setMessage(JSON.parse(data).message);
                    setVisible(true);
                }else
                    navigate('/gamesessions/'+json.id);
            }
        })
        .catch((message) => alert(message));
    }

    function handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        setGameSession({ ...gameSession, [name]: value });
    }

    function handleValidation() {
        if(!gameSession.isPrivate && gameSession.pin){
            setMessage('You cannot set a pin for a public game');
            setVisible(true);
            return false;
        } else if((gameSession.isPrivate && gameSession.pin && gameSession.pin.length !== 4) || (gameSession.isPrivate && !gameSession.pin)){
            setMessage('Pin must be 4 characters long');
            setVisible(true);
            return false;
        }
        return true
    }

    function handleCheckboxChange(event) {
        setIsChecked(event.target.checked);
        setGameSession({ ...gameSession, isPrivate: event.target.checked });
    }

    return (
        <div className="home-page-container" style={{ textAlign: 'center' }}>
        <div className='auth-page-container'>
            <div className='auth-form-container'>
                {modal}
                <Form onSubmit={handleSubmit}>
                                <h2 className='text-center'>
                {"Create Game Session"}
            </h2>
                    <div className='custom-form-input'>
                        <Label for="name" className='custom-form-input-label'>
                            Game Name
                        </Label>
                        <Input
                            type="text"
                            required
                            name="name"
                            id="name"
                            value={gameSession.name || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className='custom-form-input'>
                        <Label for="maxPlayers" className='custom-form-input-label'>
                            Number of Players
                        </Label>
                        <Input
                            type="number"
                            required
                            name="maxPlayers"
                            id="maxPlayers"
                            value={gameSession.maxPlayers || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className='custom-form-input'>
                        <Label for="isPrivate" className='custom-form-input-label'>
                            Private
                        </Label>
                        <Input
                            type="checkbox"
                            name='isPrivate'
                            id="isPrivate"
                            checked={isChecked}
                            onChange={handleCheckboxChange}
                        />
                        <p>{isChecked ? 'Private game' : 'Public game'}</p>
                    </div>
                    {gameSession.isPrivate ? <div className='custom-form-input'>
                        <Label for="pin" className='custom-form-input-label'>
                            Pin
                        </Label>
                        <Input
                            type="text"
                            name="pin"
                            id="pin"
                            value={gameSession.pin}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div> : <div></div>}
                    <div className="custom-button-row">
                        <button className='auth-button' tag={Link} to={'/gamesessions/game?id='+gameSession.id}>Create</button>
                        <Link to={'/'}
                        className='auth-button'
                        style={{textDecoration: "none"}}
                        >
                            Cancel
                        </Link>
                    </div>
                </Form>
            </div>
        </div>
    </div>
    );
}