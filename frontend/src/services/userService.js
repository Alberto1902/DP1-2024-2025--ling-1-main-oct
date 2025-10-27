const API_URL = "http://localhost:8080/api/v1"; 

async function handleResponse(response) {
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({ message: response.statusText }));
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
    }
    return response.json();
}

export async function getUserProfile(userId, token) {
    const response = await fetch(`${API_URL}/users/${userId}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    return handleResponse(response);
}

export async function updateUserProfile(userId, userData, token) {
    const response = await fetch(`${API_URL}/users/${userId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(userData)
    });
    return handleResponse(response);
}

export async function getAllGenres(token) {
    const response = await fetch(`${API_URL}/genres`, { 
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    return handleResponse(response);
}

export async function getAllPlatforms(token) {
    const response = await fetch(`${API_URL}/platforms`, { 
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    return handleResponse(response);
}

export async function getAllSagas(token) {
    const response = await fetch(`${API_URL}/sagas`, { 
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    return handleResponse(response);
}