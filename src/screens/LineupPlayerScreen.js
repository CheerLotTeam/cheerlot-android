import { useRef, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  Dimensions,
  TouchableOpacity,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation, useRoute } from '@react-navigation/native';
import { colors } from '../constants/colors';

const { width: SCREEN_WIDTH } = Dimensions.get('window');
const CARD_MARGIN = 20;
const CARD_WIDTH = SCREEN_WIDTH - CARD_MARGIN * 2;

export default function LineupPlayerScreen() {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();
  const route = useRoute();
  const { lineup, initialIndex = 0 } = route.params;

  const [currentIndex, setCurrentIndex] = useState(initialIndex);
  const flatListRef = useRef(null);

  const onViewableItemsChanged = useRef(({ viewableItems }) => {
    if (viewableItems.length > 0) {
      setCurrentIndex(viewableItems[0].index);
    }
  }).current;

  const renderCard = ({ item }) => (
    <View style={styles.card}>
      <View style={styles.cardHeader}>
        <Text style={styles.orderNumber}>{item.order}</Text>
        <Text style={styles.position}>{item.position}</Text>
      </View>

      <Text style={styles.playerName}>{item.name}</Text>

      <View style={styles.lyricsContainer}>
        <Text style={styles.lyrics}>{item.lyrics}</Text>
      </View>

      <TouchableOpacity style={styles.playButton} activeOpacity={0.7}>
        <Ionicons name="play-circle" size={64} color={colors.text.primary} />
      </TouchableOpacity>
    </View>
  );

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={styles.topBar}>
        <TouchableOpacity onPress={() => navigation.goBack()} activeOpacity={0.7}>
          <Ionicons name="chevron-back" size={28} color={colors.text.primary} />
        </TouchableOpacity>
        <Text style={styles.topBarTitle}>라인업</Text>
        <View style={styles.topBarSpacer} />
      </View>

      <FlatList
        ref={flatListRef}
        data={lineup}
        renderItem={renderCard}
        keyExtractor={(item) => String(item.order)}
        horizontal
        pagingEnabled
        showsHorizontalScrollIndicator={false}
        snapToInterval={SCREEN_WIDTH}
        decelerationRate="fast"
        initialScrollIndex={initialIndex}
        getItemLayout={(_, index) => ({
          length: SCREEN_WIDTH,
          offset: SCREEN_WIDTH * index,
          index,
        })}
        onViewableItemsChanged={onViewableItemsChanged}
        viewabilityConfig={{ itemVisiblePercentThreshold: 50 }}
        contentContainerStyle={styles.listContent}
      />

      <View style={[styles.pagination, { paddingBottom: insets.bottom + 20 }]}>
        {lineup.map((_, index) => (
          <View
            key={index}
            style={[styles.dot, index === currentIndex && styles.dotActive]}
          />
        ))}
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
  listContent: {
    alignItems: 'center',
  },
  card: {
    width: CARD_WIDTH,
    marginHorizontal: CARD_MARGIN,
    flex: 1,
    backgroundColor: colors.background.tertiary,
    borderRadius: 24,
    padding: 28,
    alignItems: 'center',
    justifyContent: 'center',
  },
  cardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    marginBottom: 8,
  },
  orderNumber: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 20,
    color: colors.text.tertiary,
  },
  position: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 14,
    color: colors.text.secondary,
  },
  playerName: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 28,
    color: colors.text.primary,
    marginBottom: 24,
  },
  lyricsContainer: {
    flex: 1,
    justifyContent: 'center',
    paddingHorizontal: 12,
  },
  lyrics: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 16,
    color: colors.text.secondary,
    textAlign: 'center',
    lineHeight: 28,
  },
  playButton: {
    marginTop: 24,
  },
  pagination: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 6,
  },
  dot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: colors.grayscale.gray200,
  },
  dotActive: {
    backgroundColor: colors.text.primary,
    width: 18,
  },
});
