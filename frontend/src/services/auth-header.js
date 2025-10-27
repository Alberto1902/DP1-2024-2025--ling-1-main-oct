// auth-header.js
export default function authHeader(token = null) {
    let user = JSON.parse(localStorage.getItem('user')); // Assuming 'user' object is stored in localStorage

    if (token) {
        // If a token is explicitly passed (e.g., from tokenService.getLocalAccessToken())
        return { Authorization: 'Bearer ' + token };
    } else if (user && user.accessToken) {
        // If user object with accessToken is in localStorage
        return { Authorization: 'Bearer ' + user.accessToken };
    } else {
        // No token found
        return {};
    }
}