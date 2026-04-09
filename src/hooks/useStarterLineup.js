import { useState, useEffect, useCallback, useRef } from 'react';
import { getStarterLineup, getTeamVersion } from '../services/playerService';
import {
  getCachedLineup,
  setCachedLineup,
  setCachedSubstitutions,
  clearCachedSubstitutions,
} from '../storage/lineupCache';

const dedupePlayers = (players) => {
  const seen = new Set();
  return (players || []).filter((p) => {
    if (seen.has(p.playerCode)) return false;
    seen.add(p.playerCode);
    return true;
  });
};

export const useStarterLineup = (teamCode) => {
  const [data, setData] = useState(null);
  const [substitutions, setSubstitutions] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Guard: once the user has modified substitutions locally, async cache
  // reads must not overwrite them.
  const userModifiedRef = useRef(false);
  const versionRef = useRef(null);

  const load = useCallback(async () => {
    if (!teamCode) return;
    setError(null);

    try {
      const cached = await getCachedLineup(teamCode);

      if (cached.data) {
        setData(cached.data);
        versionRef.current = cached.version;
        if (!userModifiedRef.current) {
          setSubstitutions(cached.substitutions || {});
        }
        setLoading(false);
      } else {
        setLoading(true);
      }

      let serverVersion = null;
      try {
        const versionRes = await getTeamVersion(teamCode);
        serverVersion = versionRes?.lineupVersion ?? null;
      } catch (err) {
        console.warn('[useStarterLineup] version check failed', err);
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

      const fresh = await fetchLineupSafe(teamCode);
      const newVersion = serverVersion ?? (cached.version ?? 0) + 1;

      setData(fresh);
      versionRef.current = newVersion;

      // Lineup version changed → substitutions are no longer valid.
      // This IS an intentional reset, so override even user-modified state.
      userModifiedRef.current = false;
      setSubstitutions({});

      setLoading(false);

      await setCachedLineup(teamCode, { data: fresh, version: newVersion });
      await clearCachedSubstitutions(teamCode);
    } catch (err) {
      setError(err);
      setLoading(false);
    }
  }, [teamCode]);

  useEffect(() => {
    userModifiedRef.current = false;
    load();
  }, [load]);

  const applySubstitution = useCallback(
    (originalPlayerCode, newPlayer) => {
      if (!teamCode || !originalPlayerCode || !newPlayer) return;
      userModifiedRef.current = true;
      setSubstitutions((prev) => {
        const next = { ...prev, [originalPlayerCode]: newPlayer };
        setCachedSubstitutions(teamCode, next);
        return next;
      });
    },
    [teamCode]
  );

  return {
    data,
    substitutions,
    loading,
    error,
    refetch: load,
    applySubstitution,
  };
};

const fetchLineupSafe = async (teamCode) => {
  const result = await getStarterLineup(teamCode);
  return { ...result, players: dedupePlayers(result.players) };
};
