import api from './api';

const CHEER_SONG_BASE_URL = 'https://pub-0a6e0917b04e4c0d98f6628fd4a2f837.r2.dev';

export const getPlayer = async (playerCode) => {
    return api.get(`/api/players/${playerCode}`);
};

export const getTeamPlayers = async (teamCode) => {
    return api.get(`/api/players/team/${teamCode}`);
};

export const getStarterLineup = async (teamCode) => {
    return api.get(`/api/players/team/${teamCode}`, {
        params: { role: 'starter' },
    });
};

export const getTeamVersion = async (teamCode) => {
    return api.get(`/api/teams/${teamCode}/version`);
};

export const getCheerSongUrl = (fileName) => {
    return `${CHEER_SONG_BASE_URL}/${fileName}`;
};
