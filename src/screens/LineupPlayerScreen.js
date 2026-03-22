import { useRef, useState, useEffect, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  Dimensions,
  TouchableOpacity,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation, useRoute } from '@react-navigation/native';
import { Audio } from 'expo-av';
import { colors } from '../constants/colors';

const { width: SCREEN_WIDTH, height: SCREEN_HEIGHT } = Dimensions.get('window');
const CARD_HORIZONTAL_MARGIN = 30;
const TOP_BAR_HEIGHT = 48;
const PAGINATION_HEIGHT = 50;

const TEAM_NAMES = {
  ob: '두산', hh: '한화', ht: '기아', wo: '키움', kt: 'KT',
  lg: 'LG', lt: '롯데', nc: 'NC', ss: '삼성', sk: 'SSG',
};

const formatDate = (date) => {
  const d = date || new Date();
  return `${d.getMonth() + 1}월 ${d.getDate()}일`;
};

export default function LineupPlayerScreen() {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();
  const route = useRoute();
  const { lineup, initialIndex = 0, selectedTeam, game } = route.params;

  const [currentIndex, setCurrentIndex] = useState(initialIndex);
  const [isPlaying, setIsPlaying] = useState(false);
  const flatListRef = useRef(null);
  const soundRef = useRef(null);

  const onPlaybackStatusUpdate = useCallback((status) => {
    if (status.isLoaded) {
      setIsPlaying(status.isPlaying);
    }
  }, []);

  const loadAudio = useCallback(async () => {
    if (soundRef.current) {
      await soundRef.current.unloadAsync();
      soundRef.current = null;
    }
    setIsPlaying(false);

    const url = lineup[currentIndex]?.cheerSongs?.[0]?.audioUrl;
    if (!url) return;

    try {
      const { sound } = await Audio.Sound.createAsync(
        { uri: url },
        { shouldPlay: true },
        onPlaybackStatusUpdate
      );
      soundRef.current = sound;
    } catch (err) {
      console.error('Audio load error:', err);
    }
  }, [currentIndex, lineup, onPlaybackStatusUpdate]);

  useEffect(() => {
    Audio.setAudioModeAsync({
      playsInSilentModeIOS: true,
      staysActiveInBackground: false,
    });

    return () => {
      if (soundRef.current) {
        soundRef.current.unloadAsync();
      }
    };
  }, []);

  useEffect(() => {
    loadAudio();
  }, [loadAudio]);

  const handlePlayPause = async () => {
    if (!soundRef.current) return;

    if (isPlaying) {
      await soundRef.current.pauseAsync();
    } else {
      await soundRef.current.playAsync();
    }
  };

  const teamColor = colors.team[selectedTeam]?.primary || colors.grayscale.gray800;
  const cardHeight = SCREEN_HEIGHT - insets.top - TOP_BAR_HEIGHT - PAGINATION_HEIGHT - insets.bottom - 80;

  const onViewableItemsChanged = useRef(({ viewableItems }) => {
    if (viewableItems.length > 0) {
      setCurrentIndex(viewableItems[0].index);
    }
  }).current;

  const renderCard = ({ item }) => {
    const hasCheerSong = item.cheerSongs?.length > 0;

    return (
      <View style={styles.cardWrapper}>
        <View style={styles.cardShadow}>
          <LinearGradient
            colors={[teamColor, `${teamColor}E6`, `${teamColor}B3`]}
            start={{ x: 0, y: 0 }}
            end={{ x: 0.8, y: 1 }}
            style={[styles.card, { height: cardHeight }]}
          >
            <View style={styles.decorCircleLarge} />
            <View style={styles.decorCircleMedium} />
            <View style={styles.decorCircleSmall} />

            <View style={styles.cardHeader}>
              <View style={styles.playerInfo}>
                <Text style={styles.orderNumber}>{item.order}</Text>
                <View style={styles.nameBlock}>
                  <Text style={styles.playerName}>{item.name}</Text>
                  <View style={styles.subtitleRow}>
                    <View style={styles.subtitleDot} />
                    <Text style={styles.subtitle}>
                      {hasCheerSong ? '기본 응원가' : '응원가 준비 중'}
                    </Text>
                  </View>
                </View>
              </View>
              {hasCheerSong && (
                <TouchableOpacity
                  style={styles.playButton}
                  activeOpacity={0.8}
                  onPress={handlePlayPause}
                >
                  <Ionicons
                    name={isPlaying ? 'pause' : 'play'}
                    size={20}
                    color="#fff"
                    style={isPlaying ? {} : { marginLeft: 2 }}
                  />
                </TouchableOpacity>
              )}
            </View>

            <View style={styles.lyricsContainer}>
              {hasCheerSong ? (
                <Text style={styles.lyrics}>{item.lyrics}</Text>
              ) : (
                <View style={styles.emptyCheer}>
                  <Text style={styles.emptyCheerEmoji}>( ' _ ' )</Text>
                  <Text style={styles.emptyCheerText}>
                    아직 응원가가 없어요
                  </Text>
                  <Text style={styles.emptyCheerSub}>
                    곧 멋진 응원가가 등록될 거예요!
                  </Text>
                </View>
              )}
            </View>
          </LinearGradient>
        </View>
      </View>
    );
  };

  return (
    <LinearGradient
      colors={[colors.background.primary, colors.background.primary, `${teamColor}30`, `${teamColor}50`]}
      locations={[0, 0.5, 0.8, 1]}
      style={[styles.container, { paddingTop: insets.top }]}
    >
      <View style={styles.topBar}>
        <TouchableOpacity onPress={() => navigation.goBack()} activeOpacity={0.7}>
          <Ionicons name="close" size={24} color={colors.text.primary} />
        </TouchableOpacity>
        <View style={styles.topBarCenter}>
          <Text style={styles.topBarDate}>{formatDate(new Date())}</Text>
          <Text style={styles.topBarMatch}>
            {TEAM_NAMES[selectedTeam] || ''} vs {TEAM_NAMES[game?.opponentTeamCode] || ''}
          </Text>
        </View>
        <View style={styles.topBarSpacer} />
      </View>

      <View style={styles.cardSection}>
        <FlatList
          ref={flatListRef}
          data={lineup}
          renderItem={renderCard}
          keyExtractor={(item) => String(item.order)}
          horizontal
          pagingEnabled
          showsHorizontalScrollIndicator={false}
          initialScrollIndex={initialIndex}
          getItemLayout={(_, index) => ({
            length: SCREEN_WIDTH,
            offset: SCREEN_WIDTH * index,
            index,
          })}
          onViewableItemsChanged={onViewableItemsChanged}
          viewabilityConfig={{ itemVisiblePercentThreshold: 50 }}
        />
        <View style={styles.pagination}>
          {lineup.map((_, index) => (
            <View
              key={index}
              style={[styles.dot, index === currentIndex && styles.dotActive]}
            />
          ))}
        </View>
      </View>
    </LinearGradient>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
  },
  topBarCenter: {
    flex: 1,
    alignItems: 'center',
  },
  topBarDate: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 12,
    color: colors.text.tertiary,
  },
  topBarMatch: {
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 14,
    color: colors.text.primary,
    marginTop: 2,
  },
  topBarSpacer: {
    width: 24,
  },
  cardSection: {
    flex: 1,
    justifyContent: 'center',
  },
  cardWrapper: {
    width: SCREEN_WIDTH,
    paddingHorizontal: CARD_HORIZONTAL_MARGIN,
  },
  cardShadow: {
    borderRadius: 28,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 12 },
    shadowOpacity: 0.25,
    shadowRadius: 20,
    elevation: 15,
  },
  card: {
    borderRadius: 28,
    padding: 28,
    justifyContent: 'space-between',
    overflow: 'hidden',
  },
  decorCircleLarge: {
    position: 'absolute',
    top: -80,
    right: -80,
    width: 280,
    height: 280,
    borderRadius: 140,
    backgroundColor: 'rgba(255,255,255,0.08)',
  },
  decorCircleMedium: {
    position: 'absolute',
    top: '35%',
    right: -60,
    width: 180,
    height: 180,
    borderRadius: 90,
    backgroundColor: 'rgba(255,255,255,0.05)',
  },
  decorCircleSmall: {
    position: 'absolute',
    bottom: -30,
    left: -30,
    width: 120,
    height: 120,
    borderRadius: 60,
    backgroundColor: 'rgba(255,255,255,0.06)',
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
  },
  playerInfo: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: 14,
  },
  orderNumber: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 52,
    color: 'rgba(255,255,255,0.9)',
    lineHeight: 52,
  },
  nameBlock: {
    paddingTop: 6,
  },
  playerName: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 24,
    color: colors.text.inverse,
    letterSpacing: -0.3,
  },
  subtitleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 6,
    gap: 6,
  },
  subtitleDot: {
    width: 4,
    height: 4,
    borderRadius: 2,
    backgroundColor: 'rgba(255,255,255,0.5)',
  },
  subtitle: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 13,
    color: 'rgba(255,255,255,0.6)',
  },
  playButton: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: 'rgba(255,255,255,0.2)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  lyricsContainer: {
    flex: 1,
    justifyContent: 'flex-end',
    paddingBottom: 12,
  },
  lyrics: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 26,
    color: colors.text.inverse,
    lineHeight: 44,
    letterSpacing: -0.3,
  },
  emptyCheer: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  emptyCheerEmoji: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 36,
    color: 'rgba(255,255,255,0.6)',
    marginBottom: 16,
  },
  emptyCheerText: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 18,
    color: 'rgba(255,255,255,0.8)',
    marginBottom: 8,
  },
  emptyCheerSub: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 14,
    color: 'rgba(255,255,255,0.5)',
  },
  pagination: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    gap: 8,
    marginTop: 20,
    marginBottom: 12,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: colors.grayscale.gray300,
  },
  dotActive: {
    backgroundColor: colors.text.primary,
    width: 24,
  },
});
