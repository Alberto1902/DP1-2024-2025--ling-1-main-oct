import React, { useState } from 'react';
import FriendsRanking from '../index';  // Importa el Ranking de Amigos
import GlobalRanking from '../../globalRanking';  // Importa el Ranking Global
import '../../static/css/ranking/ranking.css';  // Estilos globales si es necesario

export default function RankingSwitcher() {
    const [view, setView] = useState('friends'); // Estado para alternar entre rankings

    return (
        <div className="ranking-switcher-container" style={{
            padding: '20px',
            backgroundColor: '#f9f9f9',
            minHeight: '100vh',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
        }}>
            {/* Botones de alternancia */}
            <div className="ranking-buttons" style={{
                display: 'flex',
                marginBottom: '15px',
                justifyContent: 'center',
            }}>
                <button 
                    onClick={() => setView('friends')}
                    style={{
                        backgroundColor: view === 'friends' ? '#4CAF50' : '#ccc',
                        color: '#fff',
                        padding: '10px 20px',
                        border: 'none',
                        borderRadius: '5px',
                        marginRight: '10px',
                        cursor: 'pointer',
                    }}
                >
                    Friends Ranking
                </button>
                <button 
                    onClick={() => setView('global')}
                    style={{
                        backgroundColor: view === 'global' ? '#4CAF50' : '#ccc',
                        color: '#fff',
                        padding: '10px 20px',
                        border: 'none',
                        borderRadius: '5px',
                        cursor: 'pointer',
                    }}
                >
                    Global Ranking
                </button>
            </div>

            {/* Mostrar la vista correspondiente */}
            {view === 'friends' ? <FriendsRanking /> : <GlobalRanking />}
        </div>
    );
}
