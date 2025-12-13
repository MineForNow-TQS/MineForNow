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
     * Register new user
     * @param {Object} userData - User registration data
     * @returns {Promise<Object>} - Registration response
     */
    async register(userData) {
        const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });

        if (!response.ok) {
            let errorMessage = 'Erro ao criar conta';
            try {
                // Try to parse JSON error message first
                const errorData = await response.json();
                errorMessage = errorData.message || errorMessage;
            } catch (e) {
                // If not JSON, try text
                const errorText = await response.text();
                if (errorText) errorMessage = errorText;
            }
            throw new Error(errorMessage);
        }

        return await response.json();
    },

    /**
   * Logout user
   */
    async logout() {
        try {
            await fetch(`${API_BASE_URL}/api/auth/logout`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.getToken()}`
                }
            });
        } catch (error) {
            console.error('Logout failed:', error);
        } finally {
            this.removeToken();
        }
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
