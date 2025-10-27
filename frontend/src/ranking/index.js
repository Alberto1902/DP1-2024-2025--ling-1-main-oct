import React, { useState, useEffect } from 'react';
import tokenService from '../services/token.service';
import useFetchState from "../util/useFetchState";
import '../static/css/ranking/ranking.css';

export default function CombinedRanking() {
    const jwt = tokenService.getLocalAccessToken();
    const username = tokenService.getUser().username;
    const userId = tokenService.getUser().id;

    const [sortBy, setSortBy] = useState('wins');
    const [error, setError] = useState(null);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [users, setUsers] = useState([]);
    const [rankingType, setRankingType] = useState('global'); // Tipo de ranking: 'global' o 'friends'

    useEffect(() => {
        fetchRanking();
    }, [sortBy, page, size, rankingType]);

    const fetchRanking = async () => {
        let url = '';
        if (sortBy === 'gamesPlayed') {
            url = rankingType === 'global' ? `/api/v1/users/rankingByGamesPlayed?page=${page}&size=${size}` : `/api/v1/users/rankingByGamesPlayedFriends?id=${userId}&page=${page}&size=${size}`;
        } else if (sortBy === 'wins') {
            url = rankingType === 'global' ? `/api/v1/users/rankingByWins?page=${page}&size=${size}` : `/api/v1/users/rankingByWinsFriends?id=${userId}&page=${page}&size=${size}`;
        } else if (sortBy === 'winPercentage') {
            url = rankingType === 'global' ? `/api/v1/users/rankingByWinRatio?page=${page}&size=${size}` : `/api/v1/users/rankingByWinRatioFriends?id=${userId}&page=${page}&size=${size}`;
        }

        try {
            const response = await fetch(url, {
                headers: {
                    Authorization: `Bearer ${jwt}`
                }
            });
            const data = await response.json();
            setUsers(data.content || []);
            setTotalPages(data.totalPages || 0);
        } catch (error) {
            console.error("Error fetching ranking:", error);
        }
    };

    const handlePageChange = (newPage) => {
        setPage(newPage);
    };

    const handleSizeChange = (event) => {
        setSize(event.target.value);
    };

    const sorting = (value) => {
        setSortBy(value);
    };

    if (error) {
        return <div>{error}</div>; 
    }

    return (
        <div className="ranking-container" style={{
            padding: '15px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            maxHeight: '80vh',
            margin: '1rem auto'
        }}>
            <div style={{
                backgroundColor: '#fff',
                padding: '20px',
                borderRadius: '10px',
                boxShadow: '0px 4px 6px rgba(0, 0, 0, 0.1)',
                maxWidth: '900px',
                width: '100%',
            }}>
                <h1 style={{ marginBottom: '10px', }}>Ranking</h1>
        
                {/* Selección del tipo de ranking */}
                <div style={{ textAlign: 'center', marginBottom: '15px' }}>
                    <label htmlFor="rankingType" style={{ marginRight: '10px', fontSize: '14px', fontWeight: 'bold' }}>Ranking Type: </label>
                    <select 
                        id="rankingType" 
                        value={rankingType} 
                        onChange={(e) => setRankingType(e.target.value)} 
                        style={{
                            padding: '8px 12px',
                            fontSize: '13px',
                            fontWeight: 'bold',
                            backgroundColor: '#f0f0f0',
                            color: '#333',
                            border: '1px solid #ccc',
                            borderRadius: '5px',
                            cursor: 'pointer',
                            width: '180px'
                        }}
                    >
                        <option value="global">Global Ranking</option>
                        <option value="friends">Friends Ranking</option>
                    </select>
                </div>
        
                {/* Selección de ordenamiento */}
                <div style={{ textAlign: 'center', marginBottom: '15px' }}>
                    <label htmlFor="sortBy" style={{ marginRight: '10px', fontSize: '14px', fontWeight: 'bold' }}>Sort By: </label>
                    <select 
                        id="sortBy" 
                        value={sortBy} 
                        onChange={(e) => sorting(e.target.value)} 
                        style={{
                            padding: '8px 12px',
                            fontSize: '14px',
                            fontWeight: 'bold',
                            backgroundColor: '#f0f0f0',
                            color: '#333',
                            border: '1px solid #ccc',
                            borderRadius: '5px',
                            width: '180px'
                        }}
                    >
                        <option value="gamesPlayed">Games Played</option>
                        <option value="wins">Wins</option>
                        <option value="winPercentage">% Games Played / Wins</option>
                    </select>
                </div>
        
                {/* Tabla del ranking */}
                <div className="ranking-table" style={{
                    maxWidth: '700px',
                    margin: '0 auto',
                    backgroundColor: '#fff',
                    borderRadius: '10px',
                    boxShadow: '0px 4px 6px rgba(0, 0, 0, 0.1)',
                    overflow: 'hidden',
                    maxHeight: '50vh',
                    overflowY: 'auto'
                }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#d2a679', color: '#fff' }}>
                                <th style={{ padding: '8px' }}>Position</th>
                                <th style={{ padding: '8px' }}>Username</th>
                                <th style={{ padding: '8px' }}>Wins</th>
                                <th style={{ padding: '8px' }}>Total Games</th>
                                <th style={{ padding: '8px' }}>Losses</th>
                                <th style={{ padding: '8px' }}>Win %</th>
                                <th style={{ padding: '8px' }}></th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.length > 0 ? users.map((entry, index) => (
                                <tr key={entry.username} style={{ backgroundColor: index % 2 === 0 ? '#f2f2f2' : '#fff' }}>
                                    <td style={{ padding: '8px', textAlign: 'center' }}>{index + 1 + page * size}</td>
                                    <td style={{ padding: '8px' }}>{entry[0].username}</td>
                                    <td style={{ padding: '8px', textAlign: 'right' }}>{entry[1]}</td>
                                    <td style={{ padding: '8px', textAlign: 'right' }}>{entry[2]}</td>
                                    <td style={{ padding: '8px', textAlign: 'right' }}>{entry[2] - entry[1]}</td>
                                    <td style={{ padding: '8px', textAlign: 'right' }}>{entry[2] === 0 ? '0' : Number((entry[1] / entry[2]) * 100).toFixed(2)}%</td>
                                    <td style={{ padding: '8px', textAlign: 'center' }}>
                                        <div style={{
                                            backgroundColor: entry[0].colorTheme,
                                            borderRadius: '50%',
                                            width: '24px',
                                            height: '24px',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            overflow: 'hidden'
                                        }}>
                                            <img 
                                                src={entry[0].profilePictureUri} 
                                                alt={`${entry.username}'s avatar`} 
                                                style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '50%' }} 
                                            />
                                        </div>
                                    </td>
                                </tr>
                            )) : (
                                <tr><td colSpan="7" style={{ padding: '8px', textAlign: 'center' }}>No data available</td></tr>
                            )}
                        </tbody>
                    </table>
                </div>
        
                {/* Controles de paginación */}
                <div style={{ marginTop: '15px' }}>
                    <label>
                        Page Size:
                        <select value={size} onChange={handleSizeChange}>
                            <option value={5}>5</option>
                            <option value={10}>10</option>
                            <option value={20}>20</option>
                        </select>
                    </label>
                </div>
                <div style={{ marginTop: '10px' }}>
                    {Array.from({ length: totalPages }, (_, index) => (
                        <button
                            key={index}
                            onClick={() => handlePageChange(index)}
                            disabled={index === page}
                            style={{
                                margin: '0 5px',
                                padding: '5px 10px',
                                backgroundColor: index === page ? '#d2a679' : '#f0f0f0',
                                color: index === page ? '#fff' : '#333',
                                border: '1px solid #ccc',
                                borderRadius: '5px',
                                cursor: 'pointer'
                            }}
                        >
                            {index + 1}
                        </button>
                    ))}
                </div>
            </div>
        </div>
        
    );
}