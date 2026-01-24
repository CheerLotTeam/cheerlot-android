import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { colors } from '../constants/colors';

// TODO: 실제 데이터 연동 시 교체
const MOCK_GAME = {
  date: '2025.01.23 (목)',
  opponent: 'LG 트윈스',
  startingPitcher: {
    name: '양현종',
    throws: '좌투',
  },
};

const MOCK_LINEUP = [
  { order: 1, name: '박찬호', position: '중견수', bats: '우타' },
  { order: 2, name: '김도영', position: '유격수', bats: '우타' },
  { order: 3, name: '나성범', position: '좌익수', bats: '좌타' },
  { order: 4, name: '최형우', position: '지명타자', bats: '좌타' },
  { order: 5, name: '소크라테스', position: '1루수', bats: '우타' },
  { order: 6, name: '김선빈', position: '2루수', bats: '우타' },
  { order: 7, name: '최원준', position: '우익수', bats: '좌타' },
  { order: 8, name: '김태군', position: '포수', bats: '우타' },
  { order: 9, name: '박정우', position: '3루수', bats: '우타' },
];

export default function LineupScreen() {
  const insets = useSafeAreaInsets();

  return (
    <View style={styles.container}>
      <ScrollView
        contentContainerStyle={[
          styles.scrollContent,
          { paddingTop: insets.top + 16, paddingBottom: 100 },
        ]}
        showsVerticalScrollIndicator={false}
      >
        {/* 경기 정보 */}
        <View style={styles.gameInfo}>
          <Text style={styles.date}>{MOCK_GAME.date}</Text>
          <Text style={styles.opponent}>vs {MOCK_GAME.opponent}</Text>
        </View>

        {/* 선발 투수 */}
        <View style={styles.pitcherCard}>
          <Text style={styles.pitcherLabel}>선발투수</Text>
          <View style={styles.pitcherInfo}>
            <Text style={styles.pitcherName}>{MOCK_GAME.startingPitcher.name}</Text>
            <Text style={styles.pitcherThrows}>{MOCK_GAME.startingPitcher.throws}</Text>
          </View>
        </View>

        {/* 타순 리스트 */}
        <View style={styles.lineupList}>
          {MOCK_LINEUP.map((player) => (
            <View key={player.order} style={styles.playerRow}>
              <View style={styles.orderBadge}>
                <Text style={styles.orderText}>{player.order}</Text>
              </View>
              <View style={styles.playerInfo}>
                <Text style={styles.playerName}>{player.name}</Text>
                <Text style={styles.playerDetail}>
                  {player.position} · {player.bats}
                </Text>
              </View>
              <TouchableOpacity
                style={styles.playButton}
                activeOpacity={0.7}
                onPress={() => {
                  // TODO: 응원가 페이지로 이동
                }}
              >
                <Ionicons name="play-circle" size={32} color={colors.grayscale.gray400} />
              </TouchableOpacity>
            </View>
          ))}
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.grayscale.black,
  },
  scrollContent: {
    paddingHorizontal: 20,
  },
  gameInfo: {
    alignItems: 'center',
    marginBottom: 20,
  },
  date: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 14,
    color: colors.text.tertiary,
    marginBottom: 4,
  },
  opponent: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 20,
    color: colors.text.inverse,
  },
  pitcherCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.grayscale.gray900,
    borderRadius: 12,
    paddingVertical: 14,
    paddingHorizontal: 16,
    marginBottom: 24,
  },
  pitcherLabel: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 13,
    color: colors.text.tertiary,
    marginRight: 12,
  },
  pitcherInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  pitcherName: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 16,
    color: colors.text.inverse,
  },
  pitcherThrows: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 13,
    color: colors.text.secondary,
  },
  lineupList: {
    gap: 8,
  },
  playerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.grayscale.gray900,
    borderRadius: 12,
    paddingVertical: 14,
    paddingHorizontal: 16,
  },
  orderBadge: {
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: colors.grayscale.gray800,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 14,
  },
  orderText: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 14,
    color: colors.text.inverse,
  },
  playerInfo: {
    flex: 1,
  },
  playerName: {
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 16,
    color: colors.text.inverse,
    marginBottom: 2,
  },
  playerDetail: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 13,
    color: colors.text.secondary,
  },
  playButton: {
    padding: 4,
  },
});
