import { useEffect, useRef } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  Pressable,
  StyleSheet,
  Animated,
  Dimensions,
  Easing,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { colors } from '../constants/colors';
import { useTeam, TEAMS } from '../contexts/TeamContext';

const { height: SCREEN_HEIGHT, width: SCREEN_WIDTH } = Dimensions.get('window');
const CARD_GAP = 8;
const HORIZONTAL_PADDING = 20;
const CARD_WIDTH = (SCREEN_WIDTH - HORIZONTAL_PADDING * 2 - CARD_GAP) / 2;

export default function TeamSelectScreen() {
  const insets = useSafeAreaInsets();
  const { selectTeam, selectedTeam, closeTeamSelect } = useTeam();
  const translateY = useRef(new Animated.Value(SCREEN_HEIGHT)).current;

  useEffect(() => {
    Animated.timing(translateY, {
      toValue: 0,
      duration: 280,
      easing: Easing.out(Easing.cubic),
      useNativeDriver: true,
    }).start();
  }, []);

  const handleSelect = (teamId) => {
    selectTeam(teamId);
  };

  const handleClose = () => {
    closeTeamSelect();
  };

  return (
    <View style={styles.overlay}>
      <Animated.View
        style={[styles.modal, { transform: [{ translateY }] }]}
      >
        <View style={[styles.content, { paddingTop: insets.top + 20, paddingBottom: insets.bottom + 16 }]}>
          <Text style={styles.title}>응원 팀을 선택하세요</Text>

          <View style={styles.grid}>
            {TEAMS.map((team) => {
              const teamColor = colors.team[team.id].primary;
              return (
                <Pressable
                  key={team.id}
                  style={({ pressed }) => [styles.cardOuter, pressed && { opacity: 0.7 }]}
                  onPress={() => handleSelect(team.id)}
                  android_disableSound={false}
                  hitSlop={4}
                  accessibilityLabel={`${team.nameKo} 선택`}
                  accessibilityRole="button"
                >
                  <LinearGradient
                    colors={[teamColor, teamColor]}
                    start={{ x: 0, y: 0 }}
                    end={{ x: 0, y: 1 }}
                    style={styles.card}
                  >
                    <LinearGradient
                      colors={['rgba(255,255,255,0.2)', 'rgba(255,255,255,0)']}
                      start={{ x: 0, y: 0 }}
                      end={{ x: 0, y: 1 }}
                      style={styles.cardShine}
                      pointerEvents="none"
                    />
                    <Text style={styles.teamNameEn}>{team.nameEn.replace(' ', '\n')}</Text>
                    <Text style={styles.teamNameKo}>{team.nameKo}</Text>
                  </LinearGradient>
                </Pressable>
              );
            })}
          </View>

          {selectedTeam && (
            <TouchableOpacity style={styles.cancelButton} onPress={handleClose} activeOpacity={0.7}>
              <Text style={styles.cancelText}>취소</Text>
            </TouchableOpacity>
          )}
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
    backgroundColor: colors.background.primary,
  },
  content: {
    flex: 1,
    paddingHorizontal: HORIZONTAL_PADDING,
    justifyContent: 'center',
  },
  title: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 18,
    color: colors.text.primary,
    textAlign: 'center',
    marginBottom: 16,
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: CARD_GAP,
  },
  cardOuter: {
    width: CARD_WIDTH,
    borderRadius: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.25,
    shadowRadius: 16,
    elevation: 8,
  },
  card: {
    width: '100%',
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 16,
    paddingHorizontal: 10,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: 'rgba(255,255,255,0.15)',
  },
  cardShine: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: 50,
    borderTopLeftRadius: 14,
    borderTopRightRadius: 14,
  },
  teamNameEn: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 18,
    color: colors.text.inverse,
    textAlign: 'center',
    lineHeight: 22,
    marginBottom: 6,
  },
  teamNameKo: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 12,
    color: 'rgba(255,255,255,0.8)',
    textAlign: 'center',
  },
  cancelButton: {
    marginTop: 20,
    alignSelf: 'center',
    paddingVertical: 12,
    paddingHorizontal: 32,
  },
  cancelText: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 15,
    color: colors.text.secondary,
  },
});
