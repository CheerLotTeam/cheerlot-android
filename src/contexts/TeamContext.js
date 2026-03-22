import { createContext, useContext, useState, useEffect } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

const TeamContext = createContext();

export const TEAMS = [
  { id: 'ob', nameEn: 'DOOSAN BEARS', nameKo: '두산 베어스', slogan: 'WE ARE THE BEARS' },
  { id: 'hh', nameEn: 'HANWHA EAGLES', nameKo: '한화 이글스', slogan: 'FLY HIGH' },
  { id: 'ht', nameEn: 'KIA TIGERS', nameKo: '기아 타이거즈', slogan: 'TIGER PRIDE' },
  { id: 'wo', nameEn: 'KIWOOM HEROES', nameKo: '키움 히어로즈', slogan: 'BEYOND THE HERO' },
  { id: 'kt', nameEn: 'KT WIZ', nameKo: 'KT 위즈', slogan: 'WIZ ON TOP' },
  { id: 'lg', nameEn: 'LG TWINS', nameKo: 'LG 트윈스', slogan: 'TWIN POWER' },
  { id: 'lt', nameEn: 'LOTTE GIANTS', nameKo: '롯데 자이언츠', slogan: 'GIANT STEP' },
  { id: 'nc', nameEn: 'NC DINOS', nameKo: 'NC 다이노스', slogan: 'DINO POWER' },
  { id: 'ss', nameEn: 'SAMSUNG LIONS', nameKo: '삼성 라이온즈', slogan: 'WIN OR WOW!' },
  { id: 'sk', nameEn: 'SSG LANDERS', nameKo: 'SSG 랜더스', slogan: 'LANDING ON TOP' },
];

export function TeamProvider({ children }) {
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);
  const [showTeamSelect, setShowTeamSelect] = useState(false);

  useEffect(() => {
    AsyncStorage.getItem('selectedTeam').then((team) => {
      if (team) setSelectedTeam(team);
      setIsLoaded(true);
    });
  }, []);

  const selectTeam = async (teamId) => {
    await AsyncStorage.setItem('selectedTeam', teamId);
    setSelectedTeam(teamId);
    setShowTeamSelect(false);
  };

  const openTeamSelect = () => setShowTeamSelect(true);
  const closeTeamSelect = () => setShowTeamSelect(false);

  const teamInfo = TEAMS.find((t) => t.id === selectedTeam);

  return (
    <TeamContext.Provider
      value={{
        selectedTeam,
        teamInfo,
        isLoaded,
        showTeamSelect: showTeamSelect || !selectedTeam,
        selectTeam,
        openTeamSelect,
        closeTeamSelect,
      }}
    >
      {children}
    </TeamContext.Provider>
  );
}

export function useTeam() {
  return useContext(TeamContext);
}
