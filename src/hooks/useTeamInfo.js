import { useState, useEffect } from 'react';
import { getTeamInfo } from '../services/teamService';

export const useTeamInfo = (teamCode) => {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchData = async () => {
        if (!teamCode) return;

        setLoading(true);
        setError(null);

        try {
            const result = await getTeamInfo(teamCode);
            setData(result);
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
