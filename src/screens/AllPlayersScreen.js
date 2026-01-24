import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { colors } from '../constants/colors';

// TODO: 실제 데이터 연동 시 교체
const MOCK_PLAYERS = [
  { id: 1, name: '양현종', number: 54, lyrics: '양현종 양현종\n에이스 양현종\n불꽃같은 직구로\n타자를 제압해' },
  { id: 2, name: '김도영', number: 5, lyrics: '김도영 김도영\n빛나는 김도영\n화려한 플레이로\n팬들을 열광시켜' },
  { id: 3, name: '나성범', number: 47, lyrics: '나성범 나성범\n강타자 나성범\n펜스를 넘겨라\n홈런을 날려라' },
  { id: 4, name: '최형우', number: 34, lyrics: '최형우 최형우\n전설의 최형우\n방망이를 휘둘러\n승리를 가져와' },
  { id: 5, name: '소크라테스', number: 30, lyrics: '소크라테스\n우리의 소크라테스\n강력한 한방으로\n경기를 뒤집어라' },
  { id: 6, name: '김선빈', number: 3, lyrics: '김선빈 김선빈\n안타왕 김선빈\n정확한 타격으로\n출루를 책임져' },
  { id: 7, name: '박찬호', number: 24, lyrics: '박찬호 박찬호\n우리의 박찬호\n오늘도 힘차게\n달려라 박찬호' },
  { id: 8, name: '최원준', number: 53, lyrics: '최원준 최원준\n날아라 최원준\n빠른 발 재빈 손\n수비의 달인' },
  { id: 9, name: '김태군', number: 22, lyrics: '김태군 김태군\n철벽의 김태군\n포수의 자리를\n굳건히 지켜라' },
  { id: 10, name: '박정우', number: 25, lyrics: '박정우 박정우\n힘내라 박정우\n뜨거운 함성과\n함께 달려가자' },
  { id: 11, name: '이의리', number: 29, lyrics: '이의리 이의리\n강속구 이의리\n마운드를 지켜라\n삼진을 잡아라' },
  { id: 12, name: '정해영', number: 19, lyrics: '정해영 정해영\n마무리 정해영\n마지막 아웃을\n책임지는 사나이' },
  { id: 13, name: '네일', number: 43, lyrics: '네일 네일\n파워풀 네일\n강력한 투구로\n승리를 이끌어라' },
  { id: 14, name: '이창진', number: 61, lyrics: '이창진 이창진\n젊은 이창진\n미래의 에이스로\n성장하는 투수' },
  { id: 15, name: '한승택', number: 37, lyrics: '한승택 한승택\n든든한 한승택\n묵묵히 맡은 바\n최선을 다해라' },
];

export default function AllPlayersScreen({ searchQuery = '' }) {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();

  const filteredPlayers = MOCK_PLAYERS.filter((player) => {
    if (!searchQuery) return true;
    const query = searchQuery.toLowerCase();
    return (
      player.name.toLowerCase().includes(query) ||
      String(player.number).includes(query)
    );
  });

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
          <Text style={styles.teamName}>KIA 타이거즈</Text>
          <TouchableOpacity
            onPress={() => navigation.navigate('Profile')}
            activeOpacity={0.7}
          >
            <Ionicons name="person-circle-outline" size={28} color={colors.text.primary} />
          </TouchableOpacity>
        </View>

        <View style={styles.playerList}>
          {filteredPlayers.map((player) => (
            <View key={player.id} style={styles.playerRow}>
              <Text style={styles.playerNumber}>{player.number}</Text>
              <Text style={styles.playerName}>{player.name}</Text>
              <TouchableOpacity
                style={styles.playButton}
                activeOpacity={0.7}
                onPress={() => {
                  navigation.navigate('CheerPlayer', { player });
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
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 20,
  },
  teamName: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 22,
    color: colors.text.primary,
  },
  playerList: {
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
  playerNumber: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 16,
    color: colors.text.tertiary,
    width: 32,
  },
  playerName: {
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 16,
    color: colors.text.primary,
    flex: 1,
  },
  playButton: {
    padding: 4,
  },
});
