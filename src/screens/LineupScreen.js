import { useRef, useCallback } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, Animated, PanResponder } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { BlurView } from 'expo-blur';
import { LinearGradient } from 'expo-linear-gradient';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { colors } from '../constants/colors';

const TEAM_NAMES = {
  doosan: 'DOOSAN BEARS',
  hanwha: 'HANWHA EAGLES',
  kia: 'KIA TIGERS',
  kiwoom: 'KIWOOM HEROES',
  kt: 'KT WIZ',
  lg: 'LG TWINS',
  lotte: 'LOTTE GIANTS',
  nc: 'NC DINOS',
  samsung: 'SAMSUNG LIONS',
  ssg: 'SSG LANDERS',
};

// TODO: 실제 데이터 연동 시 교체
const MOCK_GAME = {
  date: '1월 23일',
  home: 'KIA',
  away: 'LG',
  startingPitcher: '양현종',
};

const MOCK_LINEUP = [
  { order: 1, name: '박찬호', position: '중견수', bats: '우타', lyrics: '박찬호 박찬호\n우리의 박찬호\n오늘도 힘차게\n달려라 박찬호' },
  { order: 2, name: '김도영', position: '유격수', bats: '우타', lyrics: '김도영 김도영\n빛나는 김도영\n화려한 플레이로\n팬들을 열광시켜' },
  { order: 3, name: '나성범', position: '좌익수', bats: '좌타', lyrics: '나성범 나성범\n강타자 나성범\n펜스를 넘겨라\n홈런을 날려라' },
  { order: 4, name: '최형우', position: '지명타자', bats: '좌타', lyrics: '최형우 최형우\n전설의 최형우\n방망이를 휘둘러\n승리를 가져와' },
  { order: 5, name: '소크라테스', position: '1루수', bats: '우타', lyrics: '소크라테스\n우리의 소크라테스\n강력한 한방으로\n경기를 뒤집어라' },
  { order: 6, name: '김선빈', position: '2루수', bats: '우타', lyrics: '김선빈 김선빈\n안타왕 김선빈\n정확한 타격으로\n출루를 책임져' },
  { order: 7, name: '최원준', position: '우익수', bats: '좌타', lyrics: '최원준 최원준\n날아라 최원준\n빠른 발 재빈 손\n수비의 달인' },
  { order: 8, name: '김태군', position: '포수', bats: '우타', lyrics: '김태군 김태군\n철벽의 김태군\n포수의 자리를\n굳건히 지켜라' },
  { order: 9, name: '박정우', position: '3루수', bats: '우타', lyrics: '박정우 박정우\n힘내라 박정우\n뜨거운 함성과\n함께 달려가자' },
];

const SWIPE_THRESHOLD = -60;

function SwipeablePlayerRow({ player, onPress, onSubstitute }) {
  const translateX = useRef(new Animated.Value(0)).current;
  const isOpen = useRef(false);

  const panResponder = useRef(
    PanResponder.create({
      onMoveShouldSetPanResponder: (_, gestureState) =>
        Math.abs(gestureState.dx) > 10 && Math.abs(gestureState.dx) > Math.abs(gestureState.dy),
      onPanResponderMove: (_, gestureState) => {
        if (gestureState.dx < 0) {
          translateX.setValue(Math.max(gestureState.dx, -80));
        } else if (isOpen.current) {
          translateX.setValue(Math.min(gestureState.dx - 80, 0));
        }
      },
      onPanResponderRelease: (_, gestureState) => {
        if (gestureState.dx < SWIPE_THRESHOLD) {
          Animated.spring(translateX, { toValue: -72, useNativeDriver: true, tension: 100, friction: 12 }).start();
          isOpen.current = true;
        } else {
          Animated.spring(translateX, { toValue: 0, useNativeDriver: true, tension: 100, friction: 12 }).start();
          isOpen.current = false;
        }
      },
    })
  ).current;

  const close = useCallback(() => {
    Animated.spring(translateX, { toValue: 0, useNativeDriver: true, tension: 100, friction: 12 }).start();
    isOpen.current = false;
  }, []);

  return (
    <View style={styles.swipeContainer}>
      <Animated.View
        style={{ flexDirection: 'row', transform: [{ translateX }] }}
        {...panResponder.panHandlers}
      >
        <TouchableOpacity
          style={styles.playerRow}
          activeOpacity={0.7}
          onPress={onPress}
        >
          <Text style={styles.orderNumber}>{player.order}</Text>
          <Text style={styles.playerName}>{player.name}</Text>
          <Text style={styles.playerDetail}>{player.position}, {player.bats}</Text>
          <Ionicons name="play" size={14} color="rgba(255,255,255,0.8)" />
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.substituteButton}
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
    </View>
  );
}

export default function LineupScreen({ selectedTeam }) {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();

  const teamColor = colors.team[selectedTeam]?.primary || colors.grayscale.gray800;
  const teamName = TEAM_NAMES[selectedTeam] || '';

  return (
    <View style={styles.container}>
      <ScrollView
        contentContainerStyle={[
          styles.scrollContent,
          { paddingTop: insets.top + 16, paddingBottom: 100 },
        ]}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.header}>
          <Text style={styles.headerTitle}>선발 라인업</Text>
          <TouchableOpacity
            style={styles.profileButton}
            onPress={() => navigation.navigate('Profile')}
            activeOpacity={0.7}
          >
            <BlurView intensity={50} tint="light" style={styles.profileBlur}>
              <View style={styles.profileGlass} />
              <Ionicons name="person" size={16} color={colors.text.primary} />
            </BlurView>
          </TouchableOpacity>
        </View>

        <View style={styles.cardOuter}>
          {/* 입체감을 위한 외곽 그림자 레이어 */}
          <LinearGradient
            colors={[`${teamColor}`, `${teamColor}CC`]}
            start={{ x: 0, y: 0 }}
            end={{ x: 0, y: 1 }}
            style={styles.card}
          >
            {/* 상단 하이라이트 (빛 반사 효과) */}
            <LinearGradient
              colors={['rgba(255,255,255,0.25)', 'rgba(255,255,255,0)']}
              start={{ x: 0, y: 0 }}
              end={{ x: 0, y: 1 }}
              style={styles.cardShine}
            />

            <Text style={styles.teamName}>{teamName}</Text>

            <View style={styles.gameBadge}>
              <Ionicons name="calendar-outline" size={13} color="rgba(255,255,255,0.85)" />
              <Text style={styles.gameBadgeText}>
                {MOCK_GAME.date} | {MOCK_GAME.home} vs {MOCK_GAME.away}
              </Text>
              <View style={styles.gameBadgeDivider} />
              <Ionicons name="baseball-outline" size={13} color="rgba(255,255,255,0.85)" />
              <Text style={styles.gameBadgeText}>{MOCK_GAME.startingPitcher}</Text>
            </View>

            <View style={styles.lineupList}>
            {MOCK_LINEUP.map((player) => (
              <SwipeablePlayerRow
                key={player.order}
                player={player}
                onPress={() => {
                  navigation.navigate('LineupPlayer', {
                    lineup: MOCK_LINEUP,
                    initialIndex: player.order - 1,
                    selectedTeam,
                    game: MOCK_GAME,
                  });
                }}
                onSubstitute={(p) => {
                  navigation.navigate('Substitute', { player: p });
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
  cardOuter: {
    borderRadius: 28,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 16 },
    shadowOpacity: 0.35,
    shadowRadius: 32,
    elevation: 16,
  },
  card: {
    borderRadius: 24,
    paddingTop: 28,
    paddingBottom: 20,
    paddingHorizontal: 24,
    alignItems: 'center',
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: 'rgba(255,255,255,0.2)',
  },
  cardShine: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: 120,
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
  },
  teamName: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 34,
    color: colors.text.inverse,
    textAlign: 'center',
    marginBottom: 12,
  },
  gameBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(255,255,255,0.15)',
    borderRadius: 20,
    borderWidth: 1,
    borderColor: 'rgba(255,255,255,0.25)',
    paddingVertical: 10,
    paddingHorizontal: 16,
    gap: 6,
    marginBottom: 24,
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
    width: 72,
    alignItems: 'center',
    justifyContent: 'center',
    paddingLeft: 8,
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
    paddingVertical: 16,
    width: '100%',
    gap: 12,
  },
  orderNumber: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 20,
    color: '#FFFFFF',
    width: 24,
  },
  playerName: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 18,
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
