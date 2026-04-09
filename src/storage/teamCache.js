import AsyncStorage from '@react-native-async-storage/async-storage';

const playersDataKey = (teamCode) => `players:data:${teamCode}`;
const playersVersionKey = (teamCode) => `players:version:${teamCode}`;
const teamInfoDataKey = (teamCode) => `teamInfo:data:${teamCode}`;
const teamInfoTimeKey = (teamCode) => `teamInfo:ts:${teamCode}`;

const TEAM_INFO_TTL_MS = 5 * 60 * 1000; // 5 minutes

export const getCachedPlayers = async (teamCode) => {
  try {
    const [dataStr, versionStr] = await Promise.all([
      AsyncStorage.getItem(playersDataKey(teamCode)),
      AsyncStorage.getItem(playersVersionKey(teamCode)),
    ]);
    return {
      data: dataStr ? JSON.parse(dataStr) : null,
      version: versionStr ? Number(versionStr) : null,
    };
  } catch (err) {
    console.warn('[teamCache] players read failed', err);
    return { data: null, version: null };
  }
};

export const setCachedPlayers = async (teamCode, { data, version }) => {
  try {
    await Promise.all([
      AsyncStorage.setItem(playersDataKey(teamCode), JSON.stringify(data)),
      AsyncStorage.setItem(playersVersionKey(teamCode), String(version)),
    ]);
  } catch (err) {
    console.warn('[teamCache] players write failed', err);
  }
};

export const getCachedTeamInfo = async (teamCode) => {
  try {
    const [dataStr, tsStr] = await Promise.all([
      AsyncStorage.getItem(teamInfoDataKey(teamCode)),
      AsyncStorage.getItem(teamInfoTimeKey(teamCode)),
    ]);
    if (!dataStr || !tsStr) return { data: null, isFresh: false };
    const age = Date.now() - Number(tsStr);
    return {
      data: JSON.parse(dataStr),
      isFresh: age < TEAM_INFO_TTL_MS,
    };
  } catch (err) {
    console.warn('[teamCache] teamInfo read failed', err);
    return { data: null, isFresh: false };
  }
};

export const setCachedTeamInfo = async (teamCode, data) => {
  try {
    await Promise.all([
      AsyncStorage.setItem(teamInfoDataKey(teamCode), JSON.stringify(data)),
      AsyncStorage.setItem(teamInfoTimeKey(teamCode), String(Date.now())),
    ]);
  } catch (err) {
    console.warn('[teamCache] teamInfo write failed', err);
  }
};
