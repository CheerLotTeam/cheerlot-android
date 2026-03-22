import { useEffect, useRef, useState, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Animated,
  Dimensions,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation, useRoute } from '@react-navigation/native';
import { Audio } from 'expo-av';
import { colors } from '../constants/colors';

const { width: SCREEN_WIDTH } = Dimensions.get('window');

const formatTime = (millis) => {
  if (!millis || millis < 0) return '0:00';
  const totalSeconds = Math.floor(millis / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
};

export default function CheerPlayerScreen() {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();
  const route = useRoute();
  const { player, players = [], currentIndex = 0, selectedTeam = 'ss' } = route.params;

  const [playerIndex, setPlayerIndex] = useState(currentIndex);
  const [isPlaying, setIsPlaying] = useState(false);
  const [progress, setProgress] = useState(0);
  const [position, setPosition] = useState(0);
  const [duration, setDuration] = useState(0);

  const soundRef = useRef(null);

  const currentPlayer = players.length > 0 ? players[playerIndex] : player;
  const hasPrevious = players.length > 0 && playerIndex > 0;
  const hasNext = players.length > 0 && playerIndex < players.length - 1;

  const audioUrl = currentPlayer?.cheerSongs?.[0]?.audioUrl || null;

  const onPlaybackStatusUpdate = useCallback((status) => {
    if (status.isLoaded) {
      setPosition(status.positionMillis || 0);
      setDuration(status.durationMillis || 0);
      setProgress(status.durationMillis ? status.positionMillis / status.durationMillis : 0);
      setIsPlaying(status.isPlaying);

      if (status.didJustFinish && hasNext) {
        setPlayerIndex((prev) => prev + 1);
      }
    }
  }, [hasNext]);

  const loadAudio = useCallback(async () => {
    if (soundRef.current) {
      await soundRef.current.unloadAsync();
      soundRef.current = null;
    }

    if (!audioUrl) return;

    try {
      const { sound } = await Audio.Sound.createAsync(
        { uri: audioUrl },
        { shouldPlay: false },
        onPlaybackStatusUpdate
      );
      soundRef.current = sound;
    } catch (err) {
      console.error('Audio load error:', err);
    }
  }, [audioUrl, onPlaybackStatusUpdate]);

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

  const handlePrevious = async () => {
    if (hasPrevious) {
      if (soundRef.current) {
        await soundRef.current.stopAsync();
      }
      setPlayerIndex(playerIndex - 1);
    }
  };

  const handleNext = async () => {
    if (hasNext) {
      if (soundRef.current) {
        await soundRef.current.stopAsync();
      }
      setPlayerIndex(playerIndex + 1);
    }
  };

  const anim1 = useRef(new Animated.Value(0)).current;
  const anim2 = useRef(new Animated.Value(0)).current;
  const anim3 = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    const createLoop = (animValue, dur) => {
      return Animated.loop(
        Animated.sequence([
          Animated.timing(animValue, {
            toValue: 1,
            duration: dur,
            useNativeDriver: true,
          }),
          Animated.timing(animValue, {
            toValue: 0,
            duration: dur,
            useNativeDriver: true,
          }),
        ])
      );
    };

    const loop1 = createLoop(anim1, 4000);
    const loop2 = createLoop(anim2, 5000);
    const loop3 = createLoop(anim3, 3500);

    loop1.start();
    loop2.start();
    loop3.start();

    return () => {
      loop1.stop();
      loop2.stop();
      loop3.stop();
    };
  }, [anim1, anim2, anim3]);

  const teamColor = colors.team[selectedTeam]?.primary || colors.grayscale.gray800;

  const wave1Style = {
    transform: [
      { translateY: anim1.interpolate({ inputRange: [0, 1], outputRange: [0, 30] }) },
    ],
  };

  const wave2Style = {
    transform: [
      { translateY: anim2.interpolate({ inputRange: [0, 1], outputRange: [0, -25] }) },
    ],
  };

  const wave3Style = {
    transform: [
      { translateY: anim3.interpolate({ inputRange: [0, 1], outputRange: [0, 20] }) },
    ],
  };

  return (
    <LinearGradient
      colors={[teamColor, `${teamColor}E6`, `${teamColor}CC`]}
      start={{ x: 0, y: 0 }}
      end={{ x: 0.3, y: 1 }}
      style={[styles.container, { paddingTop: insets.top, paddingBottom: insets.bottom }]}
    >
      <Animated.View style={[styles.waveWrapper1, wave1Style]}>
        <View style={styles.wave1} />
      </Animated.View>
      <Animated.View style={[styles.waveWrapper2, wave2Style]}>
        <View style={styles.wave2} />
      </Animated.View>
      <Animated.View style={[styles.waveWrapper3, wave3Style]}>
        <View style={styles.wave3} />
      </Animated.View>

      <View style={styles.header}>
        <TouchableOpacity
          style={styles.closeButton}
          onPress={() => navigation.goBack()}
          activeOpacity={0.7}
        >
          <Ionicons name="chevron-down" size={28} color="rgba(255,255,255,0.8)" />
        </TouchableOpacity>
        <View style={styles.handle} />
        <View style={styles.headerSpacer} />
      </View>

      <View style={styles.playerInfo}>
        <Text style={styles.playerName}>{currentPlayer?.name}</Text>
        <Text style={styles.subtitle}>
          {currentPlayer?.cheerSongs?.[0]?.title || '응원가'}
        </Text>
      </View>

      <View style={styles.lyricsContainer}>
        <Text style={styles.lyrics}>{currentPlayer?.lyrics}</Text>
      </View>

      <View style={styles.controlsContainer}>
        <View style={styles.progressContainer}>
          <View style={styles.progressBar}>
            <View style={[styles.progressFill, { width: `${progress * 100}%` }]} />
            <View style={[styles.progressThumb, { left: `${progress * 100}%` }]} />
          </View>
          <View style={styles.timeRow}>
            <Text style={styles.timeText}>{formatTime(position)}</Text>
            <Text style={styles.timeText}>{formatTime(duration)}</Text>
          </View>
        </View>

        <View style={styles.controls}>
          <TouchableOpacity
            style={[styles.controlButton, !hasPrevious && styles.controlButtonDisabled]}
            activeOpacity={0.7}
            onPress={handlePrevious}
            disabled={!hasPrevious}
          >
            <Ionicons name="play-back" size={36} color={hasPrevious ? '#fff' : 'rgba(255,255,255,0.3)'} />
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.playButton}
            activeOpacity={0.8}
            onPress={handlePlayPause}
          >
            <Ionicons
              name={isPlaying ? 'pause' : 'play'}
              size={40}
              color="#fff"
              style={isPlaying ? {} : { marginLeft: 4 }}
            />
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.controlButton, !hasNext && styles.controlButtonDisabled]}
            activeOpacity={0.7}
            onPress={handleNext}
            disabled={!hasNext}
          >
            <Ionicons name="play-forward" size={36} color={hasNext ? '#fff' : 'rgba(255,255,255,0.3)'} />
          </TouchableOpacity>
        </View>
      </View>
    </LinearGradient>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 28,
  },
  waveWrapper1: {
    position: 'absolute',
    top: -150,
    left: -200,
  },
  wave1: {
    width: SCREEN_WIDTH * 2,
    height: 400,
    backgroundColor: 'rgba(255,255,255,0.06)',
    borderRadius: 200,
    transform: [{ rotate: '-25deg' }],
  },
  waveWrapper2: {
    position: 'absolute',
    top: '25%',
    right: -250,
  },
  wave2: {
    width: SCREEN_WIDTH * 1.8,
    height: 350,
    backgroundColor: 'rgba(255,255,255,0.05)',
    borderRadius: 175,
    transform: [{ rotate: '-30deg' }],
  },
  waveWrapper3: {
    position: 'absolute',
    bottom: '5%',
    left: -180,
  },
  wave3: {
    width: SCREEN_WIDTH * 1.6,
    height: 300,
    backgroundColor: 'rgba(255,255,255,0.04)',
    borderRadius: 150,
    transform: [{ rotate: '-20deg' }],
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingTop: 12,
    paddingBottom: 24,
  },
  closeButton: {
    width: 40,
    height: 40,
    justifyContent: 'center',
  },
  handle: {
    width: 40,
    height: 5,
    borderRadius: 3,
    backgroundColor: 'rgba(255,255,255,0.4)',
  },
  headerSpacer: {
    width: 40,
  },
  playerInfo: {
    alignItems: 'center',
    marginBottom: 48,
  },
  playerName: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 28,
    color: colors.text.inverse,
    marginBottom: 8,
    letterSpacing: -0.3,
  },
  subtitle: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 15,
    color: 'rgba(255,255,255,0.55)',
  },
  lyricsContainer: {
    flex: 1,
    justifyContent: 'flex-start',
  },
  lyrics: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 28,
    color: colors.text.inverse,
    lineHeight: 50,
    letterSpacing: -0.3,
  },
  controlsContainer: {
    paddingBottom: 32,
  },
  progressContainer: {
    marginBottom: 28,
  },
  progressBar: {
    height: 4,
    backgroundColor: 'rgba(255,255,255,0.3)',
    borderRadius: 2,
    position: 'relative',
  },
  progressFill: {
    height: '100%',
    backgroundColor: '#fff',
    borderRadius: 2,
  },
  progressThumb: {
    position: 'absolute',
    top: -5,
    width: 14,
    height: 14,
    borderRadius: 7,
    backgroundColor: '#fff',
    marginLeft: -7,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 4,
  },
  timeRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 12,
  },
  timeText: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 13,
    color: 'rgba(255,255,255,0.55)',
  },
  controls: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 52,
  },
  controlButton: {
    padding: 8,
  },
  controlButtonDisabled: {
    opacity: 0.5,
  },
  playButton: {
    width: 72,
    height: 72,
    borderRadius: 36,
    backgroundColor: 'rgba(255,255,255,0.15)',
    justifyContent: 'center',
    alignItems: 'center',
  },
});
