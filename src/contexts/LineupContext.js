import { createContext, useContext } from 'react';
import { useStarterLineup } from '../hooks/useStarterLineup';
import { useTeam } from './TeamContext';

const LineupContext = createContext(null);

export function LineupProvider({ children }) {
  const { selectedTeam } = useTeam();
  const value = useStarterLineup(selectedTeam);
  return <LineupContext.Provider value={value}>{children}</LineupContext.Provider>;
}

export const useLineup = () => {
  const ctx = useContext(LineupContext);
  if (!ctx) throw new Error('useLineup must be used within LineupProvider');
  return ctx;
};
