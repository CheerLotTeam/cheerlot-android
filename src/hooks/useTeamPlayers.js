import { useState, useEffect, useCallback } from 'react';
import { getTeamPlayers, getTeamVersion } from '../services/playerService';
import { getCachedPlayers, setCachedPlayers } from '../storage/teamCache';

const dedupePlayers = (players) => {
  const seen = new Set();
  return (players || []).filter((p) => {
    if (seen.has(p.playerCode)) return false;
    seen.add(p.playerCode);
    return true;
  });
};

export const useTeamPlayers = (teamCode) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    if (!teamCode) return;
    setError(null);

    try {
      const cached = await getCachedPlayers(teamCode);

      if (cached.data) {
        setData(cached.data);
        setLoading(false);
      } else {
        setLoading(true);
      }

      let serverVersion = null;
      try {
        const versionRes = await getTeamVersion(teamCode);
        serverVersion = versionRes?.playersVersion ?? null;
      } catch (err) {
        console.warn('[useTeamPlayers] version check failed', err);
      }

      const cacheIsFresh =
        cached.data &&
        cached.version != null &&
        serverVersion != null &&
        cached.version === serverVersion;

      if (cacheIsFresh) {
        setLoading(false);
        return;
      }

      const result = await getTeamPlayers(teamCode);
      const fresh = { ...result, players: dedupePlayers(result.players) };
      const newVersion = serverVersion ?? (cached.version ?? 0) + 1;

      setData(fresh);
      setLoading(false);
      await setCachedPlayers(teamCode, { data: fresh, version: newVersion });
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
