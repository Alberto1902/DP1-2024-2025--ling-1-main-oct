import { useState, useEffect } from 'react';
import jwt_decode from 'jwt-decode';
import tokenService from '../services/token.service';
import { CirclePicker } from 'react-color';
import { Button, FormGroup, Label, Input, Card, CardBody, Alert, Table } from 'reactstrap';
import '../static/css/profile/pfp.css';
import { useParams } from 'react-router-dom';

import {
    getUserProfile,
    updateUserProfile,
    getAllGenres,
    getAllPlatforms,
    getAllSagas
} from '../services/userService';

const jwt = tokenService.getLocalAccessToken();

export default function UserProfileScreen() {
    const { username: urlUsername } = useParams();
    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [editing, setEditing] = useState(false);
    const [profileMessage, setProfileMessage] = useState('');
    const [messageType, setMessageType] = useState('');

    const [availableGenres, setAvailableGenres] = useState([]);
    const [availablePlatforms, setAvailablePlatforms] = useState([]);
    const [availableSagas, setAvailableSagas] = useState([]);

    const [statistics, setStatistics] = useState(null);
    const [totalMinutesPlayed, setTotalMinutesPlayed] = useState(0);

    useEffect(() => {
        const fetchUserData = async () => {
            if (!jwt) {
                setError("No se encontró token de autenticación.");
                setLoading(false);
                return;
            }

            let targetUsername = urlUsername;
            let currentUserId = null;
            let isOwnProfile = false;

            if (!targetUsername) {
                try {
                    const decodedToken = jwt_decode(jwt);
                    currentUserId = decodedToken.sub;
                    isOwnProfile = true;
                } catch (decodeError) {
                    setError("Token JWT inválido o sin ID de usuario.");
                    setLoading(false);
                    return;
                }
            }

            let profileData;
            try {
                if (targetUsername) {
                    profileData = await getUserProfile(targetUsername, jwt);
                    setEditing(false); 
                } else if (currentUserId) {
                    profileData = await getUserProfile(currentUserId, jwt);
                    isOwnProfile = true;
                } else {
                    setError("No se pudo determinar el usuario a mostrar.");
                    setLoading(false);
                    return;
                }

                setUser({
                    ...profileData,
                    favoriteGenres: profileData.favoriteGenres || [],
                    favoritePlatforms: profileData.favoritePlatforms || [],
                    favoriteSagas: profileData.favoriteSagas || []
                });

                if (isOwnProfile) {
                    const genres = await getAllGenres(jwt);
                    setAvailableGenres(genres);

                    const platforms = await getAllPlatforms(jwt);
                    setAvailablePlatforms(platforms);

                    const sagas = await getAllSagas(jwt);
                    setAvailableSagas(sagas);
                }

            } catch (err) {
                console.error("Error al cargar los datos del perfil o las opciones:", err);
                setError("No se pudo cargar el perfil o las opciones disponibles. " + (err.message || ''));
            } finally {
                setLoading(false);
            }
        };

        fetchUserData();
    }, [urlUsername, jwt]);

useEffect(() => {
    const fetchUserStatistics = async () => {
        if (!user?.id) return;

        try {
            const method = statistics ? "PUT" : "POST";
            const response = await fetch(`/api/v1/statistics/user?userId=${user.id}`, {
                method,
                headers: { Authorization: `Bearer ${jwt}` }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();

            const processedStats = {
                gamesPlayed: data.gamesPlayed || 0,
                victories: data.victories || 0,
                defeats: data.defeats || 0,
                winRatio: data.winRatio || 0,
                lossRatio: data.lossRatio || 0,
                averageGameDuration: data.averageGameDuration || 0,
                shortestGame: data.shortestGame || 0,
                longestGame: data.longestGame || 0,
                averageGameRoomSize: data.averageGameRoomSize || 0
            };

            setStatistics(processedStats);

            if (processedStats.gamesPlayed && processedStats.averageGameDuration) {
                setTotalMinutesPlayed(processedStats.gamesPlayed * processedStats.averageGameDuration);
            } else {
                setTotalMinutesPlayed(0);
            }
        } catch (error) {
            console.error("Error fetching statistics:", error);
            setStatistics({
                gamesPlayed: 0,
                victories: 0,
                defeats: 0,
                winRatio: 0,
                lossRatio: 0,
                averageGameDuration: 0,
                shortestGame: 0,
                longestGame: 0,
                averageGameRoomSize: 0
            });
            setTotalMinutesPlayed(0);
        }
    };

    fetchUserStatistics();
}, [user?.id, jwt]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setUser(prevUser => ({
            ...prevUser,
            [name]: value
        }));
    };

    const handleColorChange = (color) => {
        setUser(prevUser => ({
            ...prevUser,
            colorTheme: color.hex
        }));
    };

    const handleProfilePictureSelect = (uri) => {
        setUser(prevUser => ({
            ...prevUser,
            profilePictureUrl: uri
        }));
    };

    const handleCollectionChange = (collectionName, itemId, isChecked) => {
        setUser(prevUser => {
            const currentCollection = prevUser[collectionName] || [];
            if (isChecked) {
                let newItem;
                if (collectionName === 'favoriteGenres') newItem = availableGenres.find(g => g.id === itemId);
                else if (collectionName === 'favoritePlatforms') newItem = availablePlatforms.find(p => p.id === itemId);
                else if (collectionName === 'favoriteSagas') newItem = availableSagas.find(s => s.id === itemId);

                if (newItem && !currentCollection.some(item => item.id === newItem.id)) {
                    return { ...prevUser, [collectionName]: [...currentCollection, newItem] };
                }
                return prevUser;
            } else {
                return { ...prevUser, [collectionName]: currentCollection.filter(item => item.id !== itemId) };
            }
        });
    };

    const handleEditClick = () => {
    console.log("Botón de edición clickeado");
    setEditing(true);
    setProfileMessage('');
    window.scrollTo(0, 0);
};

    const handleSaveClick = async () => {
        try {
            const userToSend = {
                ...user,
                favoriteGenres: user.favoriteGenres.map(g => ({ id: g.id })),
                favoritePlatforms: user.favoritePlatforms.map(p => ({ id: p.id })),
                favoriteSagas: user.favoriteSagas.map(s => ({ id: s.id }))
            };

            const updatedData = await updateUserProfile(user.id, userToSend, jwt);

            const newFavoriteGenres = (updatedData.favoriteGenreIds || []).map((id, index) => ({
                id: id,
                name: updatedData.favoriteGenreNames[index] || 'Desconocido'
            }));

            const newFavoritePlatforms = (updatedData.favoritePlatformIds || []).map((id, index) => ({
                id: id,
                name: updatedData.favoritePlatformNames[index] || 'Desconocida'
            }));

            const newFavoriteSagas = (updatedData.favoriteSagaIds || []).map((id, index) => ({
                id: id,
                name: updatedData.favoriteSagaNames[index] || 'Desconocida'
            }));

            setEditing(false);
            setUser({
                ...updatedData,
                favoriteGenres: newFavoriteGenres,
                favoritePlatforms: newFavoritePlatforms,
                favoriteSagas: newFavoriteSagas
            });

            const currentUser = tokenService.getUser();
            if (currentUser && (currentUser.username !== updatedData.username || currentUser.colorTheme !== updatedData.colorTheme || currentUser.profilePictureUrl !== updatedData.profilePictureUrl)) {
                if (updatedData.token) {
                    tokenService.updateLocalAccessToken(updatedData.token);
                    tokenService.setUser(updatedData);
                    window.location.reload();
                } else {
                    tokenService.setUser(updatedData);
                }
            }
            setProfileMessage('Perfil actualizado exitosamente.');
            setMessageType('success');

        } catch (err) {
            console.error("Error al actualizar el perfil:", err.message || err);
            setError("No se pudo guardar el perfil. " + (err.message || ''));
            setProfileMessage('Error al guardar el perfil.');
            setMessageType('danger');
        }
    };

    if (loading) {
        return (
            <div className="container d-flex justify-content-center align-items-center" style={{ minHeight: 'calc(100vh - 80px)' }}>
                <div className="text-center">Cargando perfil...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="container d-flex justify-content-center align-items-center" style={{ minHeight: 'calc(100vh - 80px)' }}>
                <Alert color="danger" className="text-center mx-auto" style={{ maxWidth: '900px' }}>
                    Error: {error}
                </Alert>
            </div>
        );
    }

    if (!user.username) {
        return (
            <div className="container d-flex justify-content-center align-items-center" style={{ minHeight: 'calc(100vh - 80px)' }}>
                <Alert color="info" className="text-center mx-auto" style={{ maxWidth: '900px' }}>
                    No se encontraron datos de perfil.
                </Alert>
            </div>
        );
    }

    const isCurrentUserProfile = () => {
    if (!jwt || !user.username) return false;
    try {
        const decodedToken = jwt_decode(jwt);
        return decodedToken.username.toLowerCase() === user.username.toLowerCase();
    } catch (error) {
        console.error("Error decoding token:", error);
        return false;
    }
};

    return (
        <div className="d-flex justify-content-center align-items-center" style={{
        minHeight: 'calc(100vh - 80px)',
        width: '100%',
        padding: '20px',
        boxSizing: 'border-box'
    }}>
        <Card style={{
            backgroundColor: '#fff',
            padding: '20px',
            borderRadius: '10px',
            boxShadow: '0px 4px 6px rgba(0, 0, 0, 0.1)',
            maxWidth: '900px',
            width: '100%',
            minHeight: '500px',
            display: 'flex',
            flexDirection: 'column',
        }}>
            <CardBody style={{
                overflowY: 'auto',
                padding: '0',
                flexGrow: 1,
            }}>
                <h1 style={{ marginBottom: '20px', textAlign: 'center', color: '#333' }}>Perfil de {user.username}</h1>

                {profileMessage && (
                    <Alert color={messageType} className="mb-3">
                        {profileMessage}
                    </Alert>
                )}

                    {editing ? (
                        <div>
                            <FormGroup className="mb-3">
                                <Label for="username">Username</Label>
                                <Input type="text" name="username" id="username" value={user.username || ''} onChange={handleInputChange} />
                            </FormGroup>
                            <FormGroup className="mb-3">
                                <Label for="firstName">First name</Label>
                                <Input type="text" name="firstName" id="firstName" value={user.firstName || ''} onChange={handleInputChange} />
                            </FormGroup>
                            <FormGroup className="mb-3">
                                <Label for="lastName">Last name</Label>
                                <Input type="text" name="lastName" id="lastName" value={user.lastName || ''} onChange={handleInputChange} />
                            </FormGroup>
                            <FormGroup className="mb-3">
                                <Label for="biography">Biografía</Label>
                                <Input type="textarea" name="biography" id="biography" value={user.biography || ''} onChange={handleInputChange} />
                            </FormGroup>
                            <FormGroup className="mb-3">
                                <Label for="location">Ubicación</Label>
                                <Input type="text" name="location" id="location" value={user.location || ''} onChange={handleInputChange} />
                            </FormGroup>
                            <FormGroup className="mb-3">
                                <Label for="birthDate">Fecha de Nacimiento</Label>
                                <Input type="date" name="birthDate" id="birthDate" value={user.birthDate || ''} onChange={handleInputChange} />
                            </FormGroup>
                            <FormGroup className="mb-3">
                                <Label for="profileType">Tipo de Perfil</Label>
                                <Input type="select" name="profileType" id="profileType" value={user.profileType || ''} onChange={handleInputChange}>
                                    <option value="">Selecciona un tipo</option>
                                    <option value="HARD_CORE_GAMER">Hardcore Gamer</option>
                                    <option value="CASUAL_GAMER">Casual Gamer</option>
                                </Input>
                            </FormGroup>

                            <FormGroup className="mb-3">
                                <Label for="profilePictureUrl">URL de Imagen de Perfil</Label>
                                <Input type="text" name="profilePictureUrl" id="profilePictureUrl" value={user.profilePictureUrl || ''} onChange={handleInputChange} placeholder="Pega aquí la URL de tu imagen" />
                            </FormGroup>

                            <FormGroup className="mb-3">
                                <Label>Imágenes de Perfil Predeterminadas</Label>
                                <div className="d-flex flex-wrap gap-2 mt-2">
                                    <img
                                        src="/napoleonColores.png"
                                        alt="default"
                                        className={`rounded-circle border ${user.profilePictureUrl === "/napoleonColores.png" ? 'border-warning border-5' : ''}`}
                                        onClick={() => handleProfilePictureSelect("/napoleonColores.png")}
                                        style={{ width: '60px', height: '60px', cursor: 'pointer', objectFit: 'cover' }}
                                    />
                                    {user.obtainedAchievements && user.obtainedAchievements.filter(achievement => achievement.profilePictureUrl !== null).map((achievement, index) => (
                                        <img
                                            key={index}
                                            src={achievement.profilePictureUrl}
                                            alt={achievement.name}
                                            className={`rounded-circle border ${user.profilePictureUrl === achievement.profilePictureUrl ? 'border-warning border-5' : ''}`}
                                            onClick={() => handleProfilePictureSelect(achievement.profilePictureUrl)}
                                            style={{ width: '60px', height: '60px', cursor: 'pointer', objectFit: 'cover' }}
                                        />
                                    ))}
                                </div>
                            </FormGroup>

                            <FormGroup className="mb-3">
                                <Label className="mt-3">Color de Tema</Label>
                                <CirclePicker color={user.colorTheme} onChangeComplete={handleColorChange} />
                            </FormGroup>

                            <FormGroup className="mb-3">
                                <Label className="mt-3">Géneros Favoritos</Label>
                                <div className="d-flex flex-wrap gap-2">
                                    {availableGenres.map(genre => (
                                        <FormGroup check inline key={genre.id}>
                                            <Input
                                                type="checkbox"
                                                id={`genre-${genre.id}`}
                                                checked={user.favoriteGenres.some(fg => fg.id === genre.id)}
                                                onChange={(e) => handleCollectionChange('favoriteGenres', genre.id, e.target.checked)}
                                            />
                                            <Label check for={`genre-${genre.id}`}>{genre.name}</Label>
                                        </FormGroup>
                                    ))}
                                </div>
                            </FormGroup>

                            <FormGroup className="mb-3">
                                <Label className="mt-3">Plataformas Favoritas</Label>
                                <div className="d-flex flex-wrap gap-2">
                                    {availablePlatforms.map(platform => (
                                        <FormGroup check inline key={platform.id}>
                                            <Input
                                                type="checkbox"
                                                id={`platform-${platform.id}`}
                                                checked={user.favoritePlatforms.some(fp => fp.id === platform.id)}
                                                onChange={(e) => handleCollectionChange('favoritePlatforms', platform.id, e.target.checked)}
                                            />
                                            <Label check for={`platform-${platform.id}`}>{platform.name}</Label>
                                        </FormGroup>
                                    ))}
                                </div>
                            </FormGroup>

                            <FormGroup className="mb-3">
                                <Label className="mt-3">Sagas Favoritas</Label>
                                <div className="d-flex flex-wrap gap-2">
                                    {availableSagas.map(saga => (
                                        <FormGroup check inline key={saga.id}>
                                            <Input
                                                type="checkbox"
                                                id={`saga-${saga.id}`}
                                                checked={user.favoriteSagas.some(fs => fs.id === saga.id)}
                                                onChange={(e) => handleCollectionChange('favoriteSagas', saga.id, e.target.checked)}
                                            />
                                            <Label check for={`saga-${saga.id}`}>{saga.name}</Label>
                                        </FormGroup>
                                    ))}
                                </div>
                            </FormGroup>

                            <div className="d-flex justify-content-center mt-4">
                                <Button color="primary" onClick={handleSaveClick} className="me-2">
                                    Guardar Cambios
                                </Button>
                                <Button color="secondary" onClick={() => setEditing(false)}>
                                    Cancelar
                                </Button>
                            </div>
                        </div>
                    ) : (
                        <div className="profile-display-section">
                            <div className="profile-header d-flex flex-column align-items-center mb-4">
                                <img
                                    src={user.profilePictureUrl || '/napoleonColores.png'}
                                    alt="Profile"
                                    className="profile-picture"
                                    style={{
                                        width: '120px',
                                        height: '120px',
                                        borderRadius: '50%',
                                        border: `5px solid ${user.colorTheme || '#cccccc'}`,
                                        objectFit: 'cover',
                                        boxShadow: '0px 4px 8px rgba(0, 0, 0, 0.2)'
                                    }}
                                />
                                <h3 className="mt-3" style={{ color: user.colorTheme || '#333' }}>{user.firstName} {user.lastName}</h3>
                                <p className="text-muted">{user.username}</p>
                            </div>
                            <div className="profile-details text-center">
                                <p><strong>Biografía:</strong> {user.biography || 'No especificada.'}</p>
                                <p><strong>Ubicación:</strong> {user.location || 'Desconocida'}</p>
                                <p><strong>Fecha de Nacimiento:</strong> {user.birthDate || 'No especificada'}</p>
                                <p><strong>Tipo de Perfil:</strong> {user.profileType || 'Hardcore gamer'}</p>
                                <p><strong>Géneros Favoritos:</strong> {user.favoriteGenres?.map(g => g.name).join(', ') || 'Ninguno'}</p>
                                <p><strong>Plataformas Favoritas:</strong> {user.favoritePlatforms?.map(p => p.name).join(', ') || 'Ninguna'}</p>
                                <p><strong>Sagas Favoritas:</strong> {user.favoriteSagas?.map(s => s.name).join(', ') || 'Ninguna'}</p>

                                {/* New Game Statistics Section */}
                                <h4 className="mt-4">Game Statistics 🎮</h4>
                                {statistics ? (
                                    <Table bordered striped className="mt-3">
                                        <tbody>
                                            <tr>
                                                <td>Games Played</td>
                                                <td>{statistics.gamesPlayed}</td>
                                            </tr>
                                            <tr>
                                                <td>Victories</td>
                                                <td>{statistics.victories}</td>
                                            </tr>
                                            <tr>
                                                <td>Defeats</td>
                                                <td>{statistics.defeats}</td>
                                            </tr>
                                            <tr>
                                                <td>Win Ratio (%)</td>
                                                <td>{Number(statistics.winRatio).toFixed(2)}</td>
                                            </tr>
                                            <tr>
                                                <td>Loss Ratio (%)</td>
                                                <td>{Number(statistics.lossRatio).toFixed(2)}</td>
                                            </tr>
                                            <tr>
                                                <td>Total Minutes Played</td>
                                                <td>{Number(totalMinutesPlayed).toFixed(2)}</td>
                                            </tr>
                                            <tr>
                                                <td>Average Game Duration (minutes)</td>
                                                <td>{Number(statistics.averageGameDuration).toFixed(2)}</td>
                                            </tr>
                                        </tbody>
                                    </Table>
                                ) : (
                                    <p>No game statistics available for this user.</p>
                                )}
                            </div>
                                    {!urlUsername && (
                            <div className="d-flex justify-content-center mt-4">
                                <Button 
                                    color="primary" 
                                    onClick={handleEditClick}
                                    style={{
                                        backgroundColor: user.colorTheme || '#007bff',
                                        borderColor: user.colorTheme || '#007bff',
                                        padding: '10px 20px',
                                        fontSize: '1.1rem'
                                    }}
                                >
                                    ✏️ Editar Perfil
                                </Button>
                            </div>
                        )}
                        </div>
                    )}
                </CardBody>
            </Card>
        </div>
    );
}