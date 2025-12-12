const API_BASE_URL = 'http://localhost:8080';

export const authService = {
    /**
     * Login user with email and password
     * @param {string} email - User email
     * @param {string} password - User password
     * @returns {Promise<{token: string, type: string}>} - Auth response with JWT token
     */
    async login(email, password) {
        const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
        });

        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Email ou password incorretos');
            }
            throw new Error('Erro ao fazer login');
        }

        const data = await response.json();
        return data;
    },

    /**
     * Get stored JWT token
     * @returns {string|null}
     */
    getToken() {
        return localStorage.getItem('authToken');
    },

    /**
     * Store JWT token
     * @param {string} token 
     */
    setToken(token) {
        localStorage.setItem('authToken', token);
    },

    /**
     * Remove stored token (logout)
     */
    removeToken() {
        localStorage.removeItem('authToken');
    },

    /**
     * Check if user is authenticated (has valid token)
     * @returns {boolean}
     */
    isAuthenticated() {
        return !!this.getToken();
    },
};
