import { useState, useEffect, useCallback } from 'react';
import { getTeamInfo } from '../services/teamService';
import { getCachedTeamInfo, setCachedTeamInfo } from '../storage/teamCache';

export const useTeamInfo = (teamCode) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    if (!teamCode) return;
    setError(null);

    try {
      const cached = await getCachedTeamInfo(teamCode);

      if (cached.data) {
        setData(cached.data);
        setLoading(false);
      } else {
        setLoading(true);
      }

      if (cached.isFresh) return;

      const fresh = await getTeamInfo(teamCode);
      setData(fresh);
      setLoading(false);
      await setCachedTeamInfo(teamCode, fresh);
    } catch (err) {
      setError(err);
      setLoading(false);
    }
  }, [teamCode]);

  useEffect(() => {
    load();
  }, [load]);

  return { data, loading, error, refetch: load };
};
