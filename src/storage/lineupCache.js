import AsyncStorage from '@react-native-async-storage/async-storage';

const dataKey = (teamCode) => `lineup:data:${teamCode}`;
const versionKey = (teamCode) => `lineup:version:${teamCode}`;
const subsKey = (teamCode) => `lineup:subs:${teamCode}`;

export const getCachedLineup = async (teamCode) => {
  try {
    const [dataStr, versionStr, subsStr] = await Promise.all([
      AsyncStorage.getItem(dataKey(teamCode)),
      AsyncStorage.getItem(versionKey(teamCode)),
      AsyncStorage.getItem(subsKey(teamCode)),
    ]);
    return {
      data: dataStr ? JSON.parse(dataStr) : null,
      version: versionStr ? Number(versionStr) : null,
      substitutions: subsStr ? JSON.parse(subsStr) : {},
    };
  } catch (err) {
    console.warn('[lineupCache] read failed', err);
    return { data: null, version: null, substitutions: {} };
  }
};

export const setCachedLineup = async (teamCode, { data, version }) => {
  try {
    await Promise.all([
      AsyncStorage.setItem(dataKey(teamCode), JSON.stringify(data)),
      AsyncStorage.setItem(versionKey(teamCode), String(version)),
    ]);
  } catch (err) {
    console.warn('[lineupCache] write failed', err);
  }
};

export const setCachedSubstitutions = async (teamCode, substitutions) => {
  try {
    await AsyncStorage.setItem(subsKey(teamCode), JSON.stringify(substitutions));
  } catch (err) {
    console.warn('[lineupCache] subs write failed', err);
  }
};

export const clearCachedSubstitutions = async (teamCode) => {
  try {
    await AsyncStorage.removeItem(subsKey(teamCode));
  } catch (err) {
    console.warn('[lineupCache] subs clear failed', err);
  }
};
