import { useState, useCallback } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, ActivityIndicator, RefreshControl } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { BlurView } from 'expo-blur';
import { LinearGradient } from 'expo-linear-gradient';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { colors } from '../constants/colors';
import { useSearch } from '../contexts/SearchContext';
import { useTeamPlayers } from '../hooks/useTeamPlayers';

const TEAM_INFO = {
  ob: { name: 'DOOSAN BEARS', slogan: 'WE ARE THE BEARS' },
  hh: { name: 'HANWHA EAGLES', slogan: 'FLY HIGH' },
  ht: { name: 'KIA TIGERS', slogan: 'TIGER PRIDE' },
  wo: { name: 'KIWOOM HEROES', slogan: 'BEYOND THE HERO' },
  kt: { name: 'KT WIZ', slogan: 'WIZ ON TOP' },
  lg: { name: 'LG TWINS', slogan: 'TWIN POWER' },
  lt: { name: 'LOTTE GIANTS', slogan: 'GIANT STEP' },
  nc: { name: 'NC DINOS', slogan: 'DINO POWER' },
  ss: { name: 'SAMSUNG LIONS', slogan: 'WIN OR WOW!' },
  sk: { name: 'SSG LANDERS', slogan: 'LANDING ON TOP' },
};

export default function AllPlayersScreen({ selectedTeam }) {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();
  const { searchQuery } = useSearch();

  const { data: playersData, loading, error, refetch } = useTeamPlayers(selectedTeam);

  const [refreshing, setRefreshing] = useState(false);
  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    try {
      await refetch();
    } finally {
      setRefreshing(false);
    }
  }, [refetch]);

  const teamColor = colors.team[selectedTeam]?.primary || colors.grayscale.gray800;
  const teamInfo = TEAM_INFO[selectedTeam] || { name: '', slogan: '' };

  if (loading) {
    return (
      <View style={[styles.container, styles.centerContent]}>
        <ActivityIndicator size="large" color={teamColor} />
        <Text style={styles.loadingText}>선수 목록을 불러오는 중...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={[styles.container, styles.centerContent]}>
        <Ionicons name="alert-circle-outline" size={48} color={colors.text.tertiary} />
        <Text style={styles.errorText}>데이터를 불러올 수 없습니다</Text>
        <TouchableOpacity
          style={[styles.retryButton, { backgroundColor: teamColor }]}
          activeOpacity={0.7}
          onPress={refetch}
        >
          <Ionicons name="refresh" size={16} color="#FFFFFF" />
          <Text style={styles.retryText}>다시 시도</Text>
        </TouchableOpacity>
      </View>
    );
  }

  const players = playersData?.players?.map((player) => ({
    id: player.playerCode,
    name: player.name,
    number: player.backNumber,
    lyrics: player.cheerSongs?.[0]?.lyrics || '',
    playerCode: player.playerCode,
    cheerSongs: player.cheerSongs,
  })) || [];

  const sortedPlayers = [...players].sort((a, b) => {
    const aHas = (a.cheerSongs?.length || 0) > 0;
    const bHas = (b.cheerSongs?.length || 0) > 0;
    if (aHas !== bHas) return aHas ? -1 : 1;
    return a.name.localeCompare(b.name, 'ko');
  });

  const filteredPlayers = sortedPlayers.filter((player) => {
    if (!searchQuery) return true;
    const query = searchQuery.toLowerCase();
    return (
      player.name.toLowerCase().includes(query) ||
      String(player.number).includes(query)
    );
  });

  const playablePlayers = filteredPlayers.filter((p) => p.cheerSongs?.length > 0);

  const handlePlayAll = () => {
    if (playablePlayers.length === 0) return;
    navigation.navigate('CheerPlayer', {
      player: playablePlayers[0],
      players: playablePlayers,
      currentIndex: 0,
      selectedTeam,
    });
  };

  const handlePlayerPress = (player) => {
    if (!player.cheerSongs?.length) return;
    const playerIndex = playablePlayers.findIndex((p) => p.id === player.id);
    navigation.navigate('CheerPlayer', {
      player,
      players: playablePlayers,
      currentIndex: playerIndex,
      selectedTeam,
    });
  };

  return (
    <View style={styles.container}>
      <ScrollView
        contentContainerStyle={[
          styles.scrollContent,
          { paddingTop: insets.top + 16, paddingBottom: 100 },
        ]}
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={teamColor} colors={[teamColor]} />
        }
      >
        <View style={styles.header}>
          <Text style={styles.headerTitle}>전체 선수</Text>
          <TouchableOpacity
            style={styles.profileButton}
            onPress={() => navigation.navigate('Profile')}
            activeOpacity={0.7}
            hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
            accessibilityLabel="설정 열기"
            accessibilityRole="button"
          >
            <BlurView intensity={50} tint="light" style={styles.profileBlur}>
              <View style={styles.profileGlass} />
              <Ionicons name="person" size={16} color={colors.text.primary} />
            </BlurView>
          </TouchableOpacity>
        </View>

        <View style={styles.teamCardOuter}>
          <LinearGradient
            colors={[teamColor, teamColor]}
            start={{ x: 0, y: 0 }}
            end={{ x: 0, y: 1 }}
            style={styles.teamCard}
          >
            <LinearGradient
              colors={['rgba(255,255,255,0.28)', 'rgba(255,255,255,0)']}
              start={{ x: 0, y: 0 }}
              end={{ x: 0, y: 1 }}
              style={styles.teamCardShine}
            />
            <LinearGradient
              colors={['rgba(0,0,0,0)', 'rgba(0,0,0,0.15)']}
              start={{ x: 0, y: 0 }}
              end={{ x: 0, y: 1 }}
              style={styles.teamCardDepth}
              pointerEvents="none"
            />
            <Text style={styles.teamName}>{teamInfo.name}</Text>
            <Text style={styles.teamSlogan}>{teamInfo.slogan}</Text>
          </LinearGradient>
        </View>

        <View style={styles.countRow}>
          <Text style={styles.countText}>총 {filteredPlayers.length}곡</Text>
          <TouchableOpacity
            style={[styles.playAllButton, { backgroundColor: teamColor }]}
            activeOpacity={0.7}
            onPress={handlePlayAll}
          >
            <Text style={styles.playAllText}>전체 재생</Text>
            <Ionicons name="musical-notes" size={14} color="#FFFFFF" />
          </TouchableOpacity>
        </View>

        {filteredPlayers.length === 0 && (
          <View style={styles.emptyState}>
            <Ionicons name="search-outline" size={40} color={colors.text.tertiary} />
            <Text style={styles.emptyTitle}>
              {searchQuery ? '검색 결과가 없어요' : '선수가 없어요'}
            </Text>
            {searchQuery ? (
              <Text style={styles.emptySubtitle}>다른 이름이나 등번호로 검색해보세요</Text>
            ) : null}
          </View>
        )}

        <View style={styles.playerList}>
          {filteredPlayers.map((player) => (
            <TouchableOpacity
              key={player.id}
              style={styles.playerRow}
              activeOpacity={0.7}
              onPress={() => handlePlayerPress(player)}
            >
              <View style={styles.playerInfo}>
                <Text style={styles.playerName}>
                  {player.name}
                  <Text style={styles.playerNumber}>  {player.number}</Text>
                </Text>
              </View>
              <Ionicons
                name="caret-forward"
                size={18}
                color={player.cheerSongs?.length > 0 ? teamColor : colors.grayscale.gray300}
              />
            </TouchableOpacity>
          ))}
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background.primary,
  },
  centerContent: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    marginTop: 12,
    fontFamily: 'Pretendard-Medium',
    fontSize: 14,
    color: colors.text.secondary,
  },
  errorText: {
    marginTop: 12,
    fontFamily: 'Pretendard-Medium',
    fontSize: 14,
    color: colors.text.tertiary,
  },
  retryButton: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    marginTop: 20,
    paddingHorizontal: 18,
    paddingVertical: 10,
    borderRadius: 20,
  },
  retryText: {
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 13,
    color: '#FFFFFF',
  },
  emptyState: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 48,
    gap: 8,
  },
  emptyTitle: {
    marginTop: 4,
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 15,
    color: colors.text.secondary,
  },
  emptySubtitle: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 13,
    color: colors.text.tertiary,
  },
  scrollContent: {
    paddingHorizontal: 20,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 20,
  },
  headerTitle: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 24,
    color: colors.text.primary,
  },
  profileButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 12,
    elevation: 4,
  },
  profileBlur: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  profileGlass: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(255, 255, 255, 0.85)',
    borderWidth: 1,
    borderColor: 'rgba(0, 0, 0, 0.06)',
    borderRadius: 20,
  },
  teamCardOuter: {
    borderRadius: 28,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 20 },
    shadowOpacity: 0.28,
    shadowRadius: 40,
    elevation: 16,
  },
  teamCard: {
    borderRadius: 28,
    paddingVertical: 28,
    paddingHorizontal: 24,
    alignItems: 'center',
    overflow: 'hidden',
  },
  teamCardShine: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: 100,
    borderTopLeftRadius: 28,
    borderTopRightRadius: 28,
  },
  teamCardDepth: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    height: 140,
    borderBottomLeftRadius: 28,
    borderBottomRightRadius: 28,
  },
  teamName: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 28,
    color: colors.text.inverse,
    textAlign: 'center',
    marginBottom: 6,
  },
  teamSlogan: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 13,
    color: 'rgba(255,255,255,0.7)',
    textAlign: 'center',
  },
  countRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  countText: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 13,
    color: colors.text.tertiary,
  },
  playAllButton: {
    flexDirection: 'row',
    alignItems: 'center',
    borderRadius: 16,
    paddingVertical: 8,
    paddingHorizontal: 14,
    gap: 4,
  },
  playAllText: {
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 12,
    color: '#FFFFFF',
  },
  playerList: {},
  playerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 16,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border.default,
  },
  playerInfo: {
    flex: 1,
  },
  playerName: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 17,
    color: colors.text.primary,
  },
  playerNumber: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 14,
    color: colors.text.tertiary,
  },
});
