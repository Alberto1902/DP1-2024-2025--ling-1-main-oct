import tokenService from "../services/token.service";

const jwt = tokenService.getLocalAccessToken();

export const apiFetch = async (url, method = 'GET', body = null) => {
    try {
        console.debug('API Request:', {
            url,
            method,
            body,
        });

        const response = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`,
            },
            body: body ? JSON.stringify(body) : null,
        });

        // Verifica si la respuesta es válida
        if (!response.ok) {
            console.error(`API Error: ${response.status} ${response.statusText}`);
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const text = await response.text();

        // Manejo de respuesta vacía
        if (!text) {
            console.warn('API Response is empty:', url);
            return null;
        }

        // Intenta parsear el texto como JSON
        try {
            return JSON.parse(text);
        } catch (parseError) {
            console.error('Failed to parse JSON response:', parseError);
            throw new Error('Invalid JSON response');
        }
    } catch (error) {
        console.error('Error in apiFetch:', {
            error: error.message,
            url,
            method,
            body,
        });
        return null;
    }
};

export const handleDeck = async (gameSession) => {
    try {
        // Poblar el mazo
        const poblateResponse = await fetch(
            `/api/v1/decks?gameSessionId=${gameSession.id}`,
            {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                }
            }
        );

        if (!poblateResponse.ok) {
            throw new Error('Error poblando el mazo');
        }

        // Barajar el mazo
        const shuffleResponse = await fetch(
            `/api/v1/decks/shuffle?isDiscard=${false}&gameSessionId=${gameSession.id}`,
            {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                }
            }
        );

        if (!shuffleResponse.ok) {
            throw new Error('Error barajando el mazo');
        }
    } catch (error) {
        console.error('Error handling cards deal:', error);
    }
};

export const renderPlayerItem = (piece, isCurrentTurn, word) => (
    <li key={piece.id} className="player-item">
        <div className="player-info">
            <div
                className="profile-picture-container"
                style={{
                    position: 'relative',
                    display: 'inline-block',
                    height: '8vh',
                    width: '8vh'
                }}
            >
                <img
                    src={piece.profilePictureUri}
                    alt={`${piece.username}'s profile`}
                    style={{
                        backgroundColor: piece.colorTheme,
                        borderRadius: '50%',
                        height: '8vh',
                        width: '8vh',
                        position: 'relative',
                        zIndex: 1
                    }}
                    className="profile-picture"
                />
                {isCurrentTurn && (
                    <div
                        className="highlight-circles"
                        style={{
                            borderRadius: '50%',
                            height: '9vh',
                            width: '9vh',
                            position: 'absolute',
                            top: '-0.5vh',
                            left: '-0.5vh',
                            border: '3px solid red',
                            pointerEvents: 'none'
                        }}
                    />
                )}
            </div>
            <div className="player-details">
                <div className="username">{piece.username}</div>
                <div className="word">{word}</div>
            </div>
        </div>
    </li>
);