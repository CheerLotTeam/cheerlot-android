import { useRef, useState, useMemo, useCallback } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, Animated, ActivityIndicator, RefreshControl } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { BlurView } from 'expo-blur';
import { LinearGradient } from 'expo-linear-gradient';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { PanGestureHandler, State } from 'react-native-gesture-handler';
import { colors } from '../constants/colors';
import { useLineup } from '../contexts/LineupContext';
import { useTeamInfo } from '../hooks/useTeamInfo';
import { TEAMS } from '../contexts/TeamContext';

const getTeamKoName = (code) => {
  if (!code) return '';
  const team = TEAMS.find((t) => t.id === code.toLowerCase());
  return team?.nameKo || code.toUpperCase();
};

const TEAM_NAMES = {
  ob: 'DOOSAN BEARS',
  hh: 'HANWHA EAGLES',
  ht: 'KIA TIGERS',
  wo: 'KIWOOM HEROES',
  kt: 'KT WIZ',
  lg: 'LG TWINS',
  lt: 'LOTTE GIANTS',
  nc: 'NC DINOS',
  ss: 'SAMSUNG LIONS',
  sk: 'SSG LANDERS',
};

const SWIPE_OPEN = -72;

function SwipeablePlayerRow({ player, onPress, onSubstitute }) {
  const translateX = useRef(new Animated.Value(0)).current;
  const isOpen = useRef(false);

  const buttonOpacity = translateX.interpolate({
    inputRange: [SWIPE_OPEN, 0],
    outputRange: [1, 0],
    extrapolate: 'clamp',
  });

  const snapTo = (toValue) => {
    Animated.timing(translateX, {
      toValue,
      duration: 150,
      useNativeDriver: true,
    }).start();
    isOpen.current = toValue !== 0;
  };

  const close = useCallback(() => snapTo(0), []);

  const onGestureEvent = ({ nativeEvent }) => {
    if (nativeEvent.state === State.END) {
      const { translationX } = nativeEvent;
      if (!isOpen.current && translationX < -20) {
        snapTo(SWIPE_OPEN);
      } else if (isOpen.current && translationX > 20) {
        snapTo(0);
      }
    }
  };

  return (
    <View style={styles.swipeContainer}>
      <Animated.View style={[styles.substituteButton, { opacity: buttonOpacity }]}>
        <TouchableOpacity
          activeOpacity={0.7}
          onPress={() => {
            close();
            onSubstitute(player);
          }}
        >
          <View style={styles.substituteInner}>
            <Ionicons name="swap-horizontal" size={18} color="#FFFFFF" />
            <Text style={styles.substituteText}>교체</Text>
          </View>
        </TouchableOpacity>
      </Animated.View>

      <PanGestureHandler
        activeOffsetX={[-15, 15]}
        failOffsetY={[-10, 10]}
        onHandlerStateChange={onGestureEvent}
      >
        <Animated.View style={{ transform: [{ translateX }] }}>
          <TouchableOpacity
            style={styles.playerRow}
            activeOpacity={0.7}
            onPress={isOpen.current ? close : onPress}
          >
            <Text style={styles.orderNumber}>{player.order}</Text>
            <Text style={styles.playerName}>{player.name}</Text>
            <Text style={styles.playerDetail}>{[player.position, player.bats].filter(Boolean).join(', ')}</Text>
            <Ionicons
              name="play"
              size={14}
              color={player.cheerSongs?.length > 0 ? 'rgba(255,255,255,0.8)' : 'rgba(255,255,255,0.2)'}
            />
          </TouchableOpacity>
        </Animated.View>
      </PanGestureHandler>
    </View>
  );
}

export default function LineupScreen({ selectedTeam }) {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();

  const {
    data: lineupData,
    substitutions,
    loading: lineupLoading,
    error: lineupError,
    refetch: refetchLineup,
  } = useLineup();
  const { data: teamData, loading: teamLoading, error: teamError, refetch: refetchTeam } = useTeamInfo(selectedTeam);

  const [refreshing, setRefreshing] = useState(false);
  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    try {
      await Promise.all([refetchLineup(), refetchTeam()]);
    } finally {
      setRefreshing(false);
    }
  }, [refetchLineup, refetchTeam]);

  const players = useMemo(() => {
    if (!lineupData?.players) return [];
    return lineupData.players.map((player) => {
      const sub = substitutions?.[player.playerCode];
      if (sub) {
        return {
          order: player.battingOrder,
          name: sub.name,
          position: '교체선수',
          bats: '',
          lyrics: sub.cheerSongs?.[0]?.lyrics || '',
          playerCode: sub.playerCode,
          cheerSongs: sub.cheerSongs || [],
          isSubstituted: true,
          originalPlayerCode: player.playerCode,
        };
      }
      return {
        order: player.battingOrder,
        name: player.name,
        position: player.position,
        bats: player.batThrow,
        lyrics: player.cheerSongs?.[0]?.lyrics || '',
        playerCode: player.playerCode,
        cheerSongs: player.cheerSongs,
      };
    });
  }, [lineupData, substitutions]);

  const teamColor = colors.team[selectedTeam]?.primary || colors.grayscale.gray800;
  const teamName = TEAM_NAMES[selectedTeam] || '';

  if (lineupLoading || teamLoading) {
    return (
      <View style={[styles.container, styles.centerContent]}>
        <ActivityIndicator size="large" color={teamColor} />
        <Text style={styles.loadingText}>라인업을 불러오는 중...</Text>
      </View>
    );
  }

  if (lineupError || teamError) {
    return (
      <View style={[styles.container, styles.centerContent]}>
        <Ionicons name="alert-circle-outline" size={48} color={colors.text.tertiary} />
        <Text style={styles.errorText}>데이터를 불러올 수 없습니다</Text>
        <TouchableOpacity
          style={[styles.retryButton, { backgroundColor: teamColor }]}
          activeOpacity={0.7}
          onPress={() => {
            if (lineupError) refetchLineup();
            if (teamError) refetchTeam();
          }}
        >
          <Ionicons name="refresh" size={16} color="#FFFFFF" />
          <Text style={styles.retryText}>다시 시도</Text>
        </TouchableOpacity>
      </View>
    );
  }

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
          <Text style={styles.headerTitle}>선발 라인업</Text>
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

        <View style={styles.cardOuter}>
          <LinearGradient
            colors={[teamColor, teamColor]}
            start={{ x: 0, y: 0 }}
            end={{ x: 0, y: 1 }}
            style={styles.card}
          >
            <LinearGradient
              colors={['rgba(255,255,255,0.28)', 'rgba(255,255,255,0)']}
              start={{ x: 0, y: 0 }}
              end={{ x: 0, y: 1 }}
              style={styles.cardShine}
            />
            <LinearGradient
              colors={['rgba(0,0,0,0)', 'rgba(0,0,0,0.15)']}
              start={{ x: 0, y: 0 }}
              end={{ x: 0, y: 1 }}
              style={styles.cardDepth}
              pointerEvents="none"
            />

            <Text style={styles.teamName}>{teamName}</Text>

            <View style={styles.gameBadge}>
              <Ionicons name="calendar-outline" size={13} color="rgba(255,255,255,0.85)" />
              <Text style={styles.gameBadgeText}>
                {teamData?.hasTodayGame
                  ? `${getTeamKoName(selectedTeam)} vs ${getTeamKoName(teamData.opponentTeamCode)}`
                  : '오늘 경기 없음'}
              </Text>
              <View style={styles.gameBadgeDivider} />
              <Ionicons name="baseball-outline" size={13} color="rgba(255,255,255,0.85)" />
              <Text style={styles.gameBadgeText}>
                {teamData?.starterPitcherName || '-'}
              </Text>
            </View>

            <View style={styles.lineupList}>
              {players.map((player, index) => (
                <SwipeablePlayerRow
                  key={player.playerCode}
                  player={player}
                  onPress={() => {
                    navigation.navigate('LineupPlayer', {
                      lineup: players,
                      initialIndex: index,
                      selectedTeam,
                      game: teamData,
                    });
                  }}
                  onSubstitute={(p) => {
                    navigation.navigate('Substitute', {
                      player: p,
                      lineup: players,
                      selectedTeam,
                    });
                  }}
                />
              ))}
            </View>
          </LinearGradient>
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
  scrollContent: {
    paddingHorizontal: 20,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 14,
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
  cardOuter: {
    borderRadius: 36,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 20 },
    shadowOpacity: 0.28,
    shadowRadius: 40,
    elevation: 16,
  },
  card: {
    borderRadius: 32,
    paddingTop: 26,
    paddingBottom: 18,
    paddingHorizontal: 22,
    alignItems: 'center',
    overflow: 'hidden',
  },
  cardShine: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: 120,
    borderTopLeftRadius: 32,
    borderTopRightRadius: 32,
  },
  cardDepth: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    height: 200,
    borderBottomLeftRadius: 32,
    borderBottomRightRadius: 32,
  },
  teamName: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 32,
    color: colors.text.inverse,
    textAlign: 'center',
    marginBottom: 10,
  },
  gameBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(255,255,255,0.15)',
    borderRadius: 16,
    borderWidth: 1,
    borderColor: 'rgba(255,255,255,0.25)',
    paddingVertical: 8,
    paddingHorizontal: 14,
    gap: 6,
    marginBottom: 18,
  },
  gameBadgeIcon: {
    width: 20,
    height: 20,
    borderRadius: 10,
    backgroundColor: 'rgba(255,255,255,0.15)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  gameBadgeDivider: {
    width: 1,
    height: 12,
    backgroundColor: 'rgba(255,255,255,0.3)',
    marginHorizontal: 4,
  },
  gameBadgeText: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 12,
    color: 'rgba(255,255,255,0.9)',
  },
  lineupList: {
    width: '100%',
  },
  swipeContainer: {
    overflow: 'hidden',
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: 'rgba(255,255,255,0.08)',
  },
  substituteButton: {
    position: 'absolute',
    right: 0,
    top: 0,
    bottom: 0,
    width: 72,
    alignItems: 'center',
    justifyContent: 'center',
  },
  substituteInner: {
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'rgba(255,255,255,0.18)',
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: 'rgba(255,255,255,0.3)',
    borderRadius: 12,
    paddingVertical: 10,
    paddingHorizontal: 12,
    gap: 2,
  },
  substituteText: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 10,
    color: 'rgba(255,255,255,0.9)',
  },
  playerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 13,
    width: '100%',
    gap: 12,
  },
  orderNumber: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 19,
    color: '#FFFFFF',
    width: 22,
  },
  playerName: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 17,
    color: colors.text.inverse,
  },
  playerDetail: {
    flex: 1,
    fontFamily: 'Pretendard-Regular',
    fontSize: 13,
    color: 'rgba(255,255,255,0.5)',
    marginLeft: 8,
  },
});
