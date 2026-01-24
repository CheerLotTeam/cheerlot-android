import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
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

export default function LineupScreen() {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();

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
          <TouchableOpacity
            onPress={() => navigation.navigate('Profile')}
            activeOpacity={0.7}
          >
            <Ionicons name="person-circle-outline" size={28} color={colors.text.primary} />
          </TouchableOpacity>
        </View>

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
                  navigation.navigate('LineupPlayer', {
                    lineup: MOCK_LINEUP,
                    initialIndex: player.order - 1,
                  });
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
    backgroundColor: colors.background.primary,
  },
  scrollContent: {
    paddingHorizontal: 20,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    marginBottom: 12,
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
    color: colors.text.primary,
  },
  pitcherCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.background.tertiary,
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
    color: colors.text.primary,
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
    backgroundColor: colors.background.tertiary,
    borderRadius: 12,
    paddingVertical: 14,
    paddingHorizontal: 16,
  },
  orderBadge: {
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: colors.grayscale.gray200,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 14,
  },
  orderText: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 14,
    color: colors.text.primary,
  },
  playerInfo: {
    flex: 1,
  },
  playerName: {
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 16,
    color: colors.text.primary,
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
