import React, { useEffect, useState } from 'react';
import tokenService from '../services/token.service';
import '../static/css/ranking/ranking.css';
import useFetchState from '../util/useFetchState';

export default function GlobalRanking() {
    const jwt = tokenService.getLocalAccessToken();

    const [sortBy, setSortBy] = useState('wins');
    const [error, setError] = useState(null);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [usersByWins, setUsersByWins] = useFetchState([], 'api/v1/users/rankingByWins', jwt,setMessage, setVisible);
    const [usersByGamesPlayed, setUsersByGamesPlayed] = useFetchState([], 'api/v1/users/rankingByGamesPlayed', jwt,setMessage, setVisible);
    const [usersByWinPercentage, setUsersByWinPercentage] = useFetchState([], 'api/v1/users/rankingByWinRatio', jwt,setMessage, setVisible);
    //const [usersByTimePlayed, setUsersByTimePlayed] = useFetchState([], 'api/v1/users/rankingByTimePlayed', jwt,setMessage, setVisible);
    const [users, setUsers] = useFetchState([], 'api/v1/users/rankingByWins', jwt,setMessage, setVisible);

   
    useEffect(() => {
        sortRanking();
    }, [sortBy]);

    const sortRanking = () => {
        if (sortBy === 'gamesPlayed') {
            setUsers(usersByGamesPlayed);
        } else if (sortBy === 'wins') {
            setUsers(usersByWins);
        } else if (sortBy === 'winPercentage') {
            setUsers(usersByWinPercentage);
        }
    };

    const sorting = (value) => {
        setSortBy(value);
    };

    if (error) {
        return <div>{error}</div>; 
    }

    return (
        <div className="ranking-container" style={{ padding: '15px', backgroundColor: '#f9f9f9', display: 'flex', flexDirection: 'column', alignItems: 'center', minHeight: '100vh', overflowY: 'auto' }}>
            <h1 style={{ textAlign: 'center', marginBottom: '10px' }}>Global Ranking</h1>

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
                        cursor: 'pointer', 
                        transition: 'all 0.3s ease',
                        width: '180px'
                    }}
                >
                    <option value="gamesPlayed" style={{ padding: '10px' }}>Games Played</option>
                    <option value="wins" style={{ padding: '10px' }}>Wins</option>
                    <option value="winPercentage" style={{ padding: '10px' }}>% Games Played / Wins</option>
                </select>
            </div>

            <div className="ranking-table" style={{ maxWidth: '700px', margin: '0 auto', backgroundColor: '#fff', borderRadius: '10px', boxShadow: '0px 4px 6px rgba(0, 0, 0, 0.1)', overflow: 'hidden', maxHeight: '80vh', overflowY: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', tableLayout: 'auto' }}>
                    <thead>
                        <tr style={{ backgroundColor: '#4CAF50', color: '#fff', textAlign: 'left' }}>
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
                            <tr key={entry.username} style={{ backgroundColor: index % 2 === 0 ? '#f2f2f2' : '#fff', textAlign: 'left' }}>
                                <td style={{ padding: '8px', textAlign: 'center' }}>{index + 1}</td>
                                <td style={{ padding: '8px' }}>{entry[0].username}</td>
                                <td style={{ padding: '8px', textAlign: 'right' }}>{entry[1]}</td>
                                <td style={{ padding: '8px', textAlign: 'right' }}>{entry[2]}</td>
                                <td style={{ padding: '8px', textAlign: 'right' }}>{entry[2]- entry[1]}</td>
                                <td style={{ padding: '8px', textAlign: 'right' }}>{entry[2]=== 0 ? '0' : (entry[1]/entry[2])*100}%</td>
                                <td style={{ padding: '8px', textAlign: 'center' }}>
                                    <div style={{
                                        backgroundColor: entry[0].colorTheme,
                                        borderRadius: '50%',
                                        width: '35px',
                                        height: '35px',
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
        </div>
    );
}
