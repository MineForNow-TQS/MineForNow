// Base URL do backend
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

/**
 * Dashboard Service
 * Handles API calls related to owner dashboard statistics
 */
export const dashboardService = {
    /**
     * Get owner dashboard statistics
     * @returns {Promise} Promise with dashboard stats (totalRevenue, activeVehicles, pendingBookings, completedBookings)
     */
    getOwnerStats: async () => {
        const token = localStorage.getItem('authToken');

        if (!token) {
            throw new Error('No authentication token found');
        }

        const response = await fetch(`${API_BASE_URL}/dashboard/owner`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Unauthorized - Please login again');
            }
            if (response.status === 404) {
                throw new Error('Owner not found');
            }
            throw new Error(`Failed to fetch dashboard stats: ${response.statusText}`);
        }

        return response.json();
    },

    /**
     * Get owner pending bookings
     * @returns {Promise} Promise with array of pending bookings
     */
    getPendingBookings: async () => {
        const token = localStorage.getItem('authToken');

        if (!token) {
            throw new Error('No authentication token found');
        }

        const response = await fetch(`${API_BASE_URL}/dashboard/owner/pending-bookings`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Unauthorized - Please login again');
            }
            if (response.status === 404) {
                throw new Error('Owner not found');
            }
            throw new Error(`Failed to fetch pending bookings: ${response.statusText}`);
        }

        return response.json();
    },

    /**
     * Get owner active bookings (confirmed rentals in progress)
     * @returns {Promise} Promise with array of active bookings
     */
    getActiveBookings: async () => {
        const token = localStorage.getItem('authToken');

        if (!token) {
            throw new Error('No authentication token found');
        }

        const response = await fetch(`${API_BASE_URL}/dashboard/owner/active-bookings`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Unauthorized - Please login again');
            }
            if (response.status === 404) {
                throw new Error('Owner not found');
            }
            throw new Error(`Failed to fetch active bookings: ${response.statusText}`);
        }

        return response.json();
    }
};
