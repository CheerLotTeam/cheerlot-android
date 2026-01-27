import axios from 'axios';
import Constants from 'expo-constants';

const api = axios.create({
    baseURL: Constants.expoConfig?.extra?.apiBaseUrl || '',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use(
    (config) => {
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

api.interceptors.response.use(
    (response) => {
        return response.data;
    },
    (error) => {
        if (error.response) {
            const { errorCode, message } = error.response.data;
            console.error(`[API Error] ${errorCode}: ${message}`);
        } else {
            console.error(`[Network Error]`, error.message);
        }
        return Promise.reject(error);
    }
);

export default api;
