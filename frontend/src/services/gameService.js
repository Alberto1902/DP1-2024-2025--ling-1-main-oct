import api from './api';

const API_URL = '/gamesessions';

export const gameService = {
    createGameSession: async (gameData) => {
        try {
            const response = await api.post(API_URL, gameData);
            return response.data;
        } catch (error) {
            if (error.response?.data?.message?.includes('Casual Gamer')) {
                throw {
                    type: 'CASUAL_GAMER_LIMIT',
                    limitType: 'DAILY_LIMIT',
                    message: error.response.data.message
                };
            }
            throw error;
        }
    },

    joinGameSession: async (gameId, userId, code = null) => {
        try {
            const params = { id: gameId, userId };
            if (code) params.code = code;
            
            const response = await api.put(`${API_URL}/game/join`, null, { params });
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    checkPlayerTimeStatus: async (gameSessionId, userId) => {
        try {
            const response = await api.get(`${API_URL}/checkPlayerTime`, {
                params: { gameSessionId, userId }
            });
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    nextTurn: async (gameId) => {
        try {
            const response = await api.put(`${API_URL}/game/nextTurn`, null, {
                params: { id: gameId }
            });
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    changeStatus: async (gameId) => {
        try {
            const response = await api.put(`${API_URL}/game/changeStatus`, null, {
                params: { id: gameId }
            });
            return response.data;
        } catch (error) {
            throw error;
        }
    }
};

export default gameService;
