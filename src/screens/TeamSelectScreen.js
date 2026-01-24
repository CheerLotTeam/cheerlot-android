import { useEffect, useRef } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Animated,
  Dimensions,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { colors } from '../constants/colors';

const { height: SCREEN_HEIGHT } = Dimensions.get('window');

const TEAMS = [
  { id: 'kia', name: 'KIA 타이거즈' },
  { id: 'samsung', name: '삼성 라이온즈' },
  { id: 'lg', name: 'LG 트윈스' },
  { id: 'doosan', name: '두산 베어스' },
  { id: 'kt', name: 'KT 위즈' },
  { id: 'ssg', name: 'SSG 랜더스' },
  { id: 'nc', name: 'NC 다이노스' },
  { id: 'hanwha', name: '한화 이글스' },
  { id: 'lotte', name: '롯데 자이언츠' },
  { id: 'kiwoom', name: '키움 히어로즈' },
];

export default function TeamSelectScreen({ onTeamSelect }) {
  const translateY = useRef(new Animated.Value(SCREEN_HEIGHT)).current;

  useEffect(() => {
    Animated.spring(translateY, {
      toValue: 0,
      useNativeDriver: true,
      tension: 65,
      friction: 11,
    }).start();
  }, []);

  const handleSelect = async (teamId) => {
    await AsyncStorage.setItem('selectedTeam', teamId);
    Animated.timing(translateY, {
      toValue: SCREEN_HEIGHT,
      duration: 250,
      useNativeDriver: true,
    }).start(() => {
      onTeamSelect(teamId);
    });
  };

  return (
    <View style={styles.overlay}>
      <Animated.View
        style={[styles.modal, { transform: [{ translateY }] }]}
      >
        <Text style={styles.title}>응원 팀을 선택하세요</Text>
        <View style={styles.grid}>
          {TEAMS.map((team) => {
            const teamColor = colors.team[team.id].primary;
            return (
              <TouchableOpacity
                key={team.id}
                style={[styles.card, { backgroundColor: teamColor }]}
                activeOpacity={0.8}
                onPress={() => handleSelect(team.id)}
              >
                <Text style={styles.teamName}>{team.name}</Text>
              </TouchableOpacity>
            );
          })}
        </View>
      </Animated.View>
    </View>
  );
}

const styles = StyleSheet.create({
  overlay: {
    ...StyleSheet.absoluteFillObject,
    zIndex: 100,
  },
  modal: {
    flex: 1,
    backgroundColor: colors.grayscale.black,
    paddingHorizontal: 20,
    justifyContent: 'center',
  },
  title: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 20,
    color: colors.text.inverse,
    textAlign: 'center',
    marginBottom: 24,
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
    justifyContent: 'center',
  },
  card: {
    width: '47%',
    paddingVertical: 18,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
  },
  teamName: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 14,
    color: colors.text.inverse,
  },
});
