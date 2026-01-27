import api from './api';

export const getTeamInfo = async (teamCode) => {
    return api.get(`/api/teams/${teamCode}`);
};
