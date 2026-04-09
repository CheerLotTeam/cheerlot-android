import { useState, useMemo, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation, useRoute } from '@react-navigation/native';
import { colors } from '../constants/colors';
import { useTeamPlayers } from '../hooks/useTeamPlayers';
import { useLineup } from '../contexts/LineupContext';

export default function SubstituteScreen() {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();
  const route = useRoute();
  const { player, lineup = [], selectedTeam } = route.params;

  const [selectedPlayer, setSelectedPlayer] = useState(null);
  const { data: playersData, loading, error } = useTeamPlayers(selectedTeam);
  const { applySubstitution } = useLineup();

  const teamColor = colors.team[selectedTeam]?.primary || colors.grayscale.gray800;

  const benchPlayers = useMemo(() => {
    const starterCodes = new Set(lineup.map((p) => p.playerCode));
    return (playersData?.players || [])
      .filter((p) => !starterCodes.has(p.playerCode))
      .sort((a, b) => a.name.localeCompare(b.name, 'ko'));
  }, [lineup, playersData]);

  const handleConfirm = useCallback(() => {
    if (!selectedPlayer) return;
    const keyCode = player.originalPlayerCode || player.playerCode;
    applySubstitution(keyCode, selectedPlayer);
    navigation.goBack();
  }, [selectedPlayer, player, applySubstitution, navigation]);

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={styles.topBar}>
        <TouchableOpacity onPress={() => navigation.goBack()} activeOpacity={0.7}>
          <Ionicons name="close" size={24} color={colors.text.primary} />
        </TouchableOpacity>
        <Text style={styles.topBarTitle}>선수 교체</Text>
        <TouchableOpacity
          onPress={handleConfirm}
          activeOpacity={0.7}
          disabled={!selectedPlayer}
        >
          <Ionicons
            name="checkmark"
            size={26}
            color={selectedPlayer ? teamColor : colors.grayscale.gray300}
          />
        </TouchableOpacity>
      </View>

      <View style={styles.targetSection}>
        <Text style={styles.targetLabel}>교체 선수</Text>
        <Text style={styles.targetName}>{player.name}</Text>
      </View>

      {loading ? (
        <View style={styles.centerContent}>
          <ActivityIndicator size="large" color={teamColor} />
        </View>
      ) : error ? (
        <View style={styles.centerContent}>
          <Ionicons name="alert-circle-outline" size={48} color={colors.text.tertiary} />
          <Text style={styles.errorText}>선수 목록을 불러올 수 없습니다</Text>
        </View>
      ) : (
        <ScrollView
          contentContainerStyle={[styles.scrollContent, { paddingBottom: insets.bottom + 20 }]}
          showsVerticalScrollIndicator={false}
        >
          <View style={styles.grid}>
            {benchPlayers.map((p) => {
              const isSelected = selectedPlayer?.playerCode === p.playerCode;

              return (
                <TouchableOpacity
                  key={p.playerCode}
                  style={[
                    styles.playerCard,
                    isSelected && { borderColor: teamColor, backgroundColor: `${teamColor}0D` },
                  ]}
                  activeOpacity={0.7}
                  onPress={() => setSelectedPlayer(isSelected ? null : p)}
                >
                  <Text
                    style={[
                      styles.playerName,
                      isSelected && { color: teamColor },
                    ]}
                  >
                    {p.name}
                  </Text>
                </TouchableOpacity>
              );
            })}
          </View>
        </ScrollView>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background.primary,
  },
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    paddingVertical: 12,
  },
  topBarTitle: {
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 16,
    color: colors.text.primary,
  },
  targetSection: {
    alignItems: 'center',
    paddingVertical: 20,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border.default,
    marginHorizontal: 20,
    marginBottom: 20,
  },
  targetLabel: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 13,
    color: colors.text.tertiary,
    marginBottom: 6,
  },
  targetName: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 22,
    color: colors.text.primary,
  },
  centerContent: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  errorText: {
    marginTop: 12,
    fontFamily: 'Pretendard-Medium',
    fontSize: 14,
    color: colors.text.tertiary,
  },
  scrollContent: {
    paddingHorizontal: 20,
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
  },
  playerCard: {
    width: '48.5%',
    paddingVertical: 14,
    paddingHorizontal: 16,
    borderRadius: 12,
    borderWidth: 1.5,
    borderColor: colors.border.default,
    backgroundColor: colors.background.primary,
  },
  playerName: {
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 15,
    color: colors.text.primary,
    textAlign: 'center',
  },
});
