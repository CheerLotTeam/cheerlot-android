import { useState, useEffect } from 'react';
import { getTeamPlayers } from '../services/playerService';

export const useTeamPlayers = (teamCode) => {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchData = async () => {
        if (!teamCode) return;

        setLoading(true);
        setError(null);

        try {
            const result = await getTeamPlayers(teamCode);
            const seen = new Set();
            const uniquePlayers = result.players?.filter((p) => {
                if (seen.has(p.playerCode)) return false;
                seen.add(p.playerCode);
                return true;
            });
            setData({ ...result, players: uniquePlayers });
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [teamCode]);

    return { data, loading, error, refetch: fetchData };
};
