import { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation, useRoute } from '@react-navigation/native';
import { colors } from '../constants/colors';

export default function CheerPlayerScreen() {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();
  const route = useRoute();
  const { player } = route.params;

  const [isPlaying, setIsPlaying] = useState(false);

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={styles.topBar}>
        <TouchableOpacity onPress={() => navigation.goBack()} activeOpacity={0.7}>
          <Ionicons name="chevron-back" size={28} color={colors.text.primary} />
        </TouchableOpacity>
        <Text style={styles.topBarTitle}>응원가</Text>
        <View style={styles.topBarSpacer} />
      </View>

      <View style={styles.playerInfo}>
        <View style={styles.numberBadge}>
          <Text style={styles.numberText}>{player.number}</Text>
        </View>
        <Text style={styles.playerName}>{player.name}</Text>
      </View>

      <ScrollView
        style={styles.lyricsScroll}
        contentContainerStyle={styles.lyricsContent}
        showsVerticalScrollIndicator={false}
      >
        <Text style={styles.lyrics}>{player.lyrics}</Text>
      </ScrollView>

      <View style={[styles.controls, { paddingBottom: insets.bottom + 24 }]}>
        <View style={styles.progressBar}>
          <View style={styles.progressFill} />
        </View>
        <View style={styles.timeRow}>
          <Text style={styles.time}>0:00</Text>
          <Text style={styles.time}>0:00</Text>
        </View>

        <TouchableOpacity
          style={styles.playButton}
          activeOpacity={0.7}
          onPress={() => setIsPlaying(!isPlaying)}
        >
          <Ionicons
            name={isPlaying ? 'pause-circle' : 'play-circle'}
            size={72}
            color={colors.text.primary}
          />
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background.primary,
  },
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
  },
  topBarTitle: {
    flex: 1,
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 16,
    color: colors.text.primary,
    textAlign: 'center',
  },
  topBarSpacer: {
    width: 28,
  },
  playerInfo: {
    alignItems: 'center',
    paddingTop: 24,
    paddingBottom: 32,
  },
  numberBadge: {
    width: 64,
    height: 64,
    borderRadius: 32,
    backgroundColor: colors.grayscale.gray200,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 16,
  },
  numberText: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 24,
    color: colors.text.primary,
  },
  playerName: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 24,
    color: colors.text.primary,
  },
  lyricsScroll: {
    flex: 1,
    marginHorizontal: 32,
  },
  lyricsContent: {
    paddingVertical: 8,
  },
  lyrics: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 17,
    color: colors.text.secondary,
    textAlign: 'center',
    lineHeight: 30,
  },
  controls: {
    paddingHorizontal: 32,
    alignItems: 'center',
  },
  progressBar: {
    width: '100%',
    height: 4,
    backgroundColor: colors.grayscale.gray200,
    borderRadius: 2,
  },
  progressFill: {
    width: '0%',
    height: '100%',
    backgroundColor: colors.text.primary,
    borderRadius: 2,
  },
  timeRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '100%',
    marginTop: 8,
  },
  time: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 12,
    color: colors.text.tertiary,
  },
  playButton: {
    marginTop: 16,
  },
});
