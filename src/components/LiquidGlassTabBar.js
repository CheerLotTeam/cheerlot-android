import { useEffect, useRef } from 'react';
import {
  View,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Animated,
  Dimensions,
} from 'react-native';
import { BlurView } from 'expo-blur';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useSearch } from '../contexts/SearchContext';

const TAB_ICONS = {
  Lineup: { active: 'list', inactive: 'list-outline' },
  AllPlayers: { active: 'people', inactive: 'people-outline' },
};

const TAB_LABELS = {
  Lineup: '라인업',
  AllPlayers: '전체선수',
};

const TAB_BAR_HEIGHT = 56;
const TAB_WIDTH = 100;
const SEARCH_BUTTON_SIZE = 44;
const COLLAPSED_TAB_WIDTH = 56;
const BAR_GAP = 12;
const INDICATOR_HORIZONTAL_MARGIN = 6;
const INDICATOR_VERTICAL_MARGIN = 6;
const WRAPPER_HORIZONTAL_PADDING = 20;

const SCREEN_WIDTH = Dimensions.get('window').width;
const ROW_WIDTH = SCREEN_WIDTH - WRAPPER_HORIZONTAL_PADDING * 2;

export default function LiquidGlassTabBar({ state, navigation }) {
  const insets = useSafeAreaInsets();
  const {
    searchQuery,
    setSearchQuery,
    isSearching,
    openSearch: contextOpenSearch,
    closeSearch: contextCloseSearch,
  } = useSearch();
  const tabCount = state.routes.length;
  const tabsContainerWidth = TAB_WIDTH * tabCount;
  const indicatorWidth = TAB_WIDTH - INDICATOR_HORIZONTAL_MARGIN * 2;
  const expandedSearchWidth = ROW_WIDTH - COLLAPSED_TAB_WIDTH - BAR_GAP;

  const inputRef = useRef(null);

  const translateX = useRef(new Animated.Value(0)).current;
  const searchAnim = useRef(new Animated.Value(isSearching ? 1 : 0)).current;

  useEffect(() => {
    Animated.spring(translateX, {
      toValue: state.index * TAB_WIDTH + INDICATOR_HORIZONTAL_MARGIN,
      useNativeDriver: true,
      tension: 68,
      friction: 12,
    }).start();
  }, [state.index]);

  useEffect(() => {
    Animated.spring(searchAnim, {
      toValue: isSearching ? 1 : 0,
      useNativeDriver: false,
      tension: 68,
      friction: 12,
    }).start(() => {
      if (isSearching) {
        inputRef.current?.focus();
      }
    });
  }, [isSearching]);

  const openSearch = () => {
    contextOpenSearch();
    navigation.navigate('AllPlayers');
  };

  const closeSearch = () => {
    inputRef.current?.blur();
    contextCloseSearch();
  };

  const tabsAnimatedWidth = searchAnim.interpolate({
    inputRange: [0, 1],
    outputRange: [tabsContainerWidth, COLLAPSED_TAB_WIDTH],
  });

  const searchAnimatedWidth = searchAnim.interpolate({
    inputRange: [0, 1],
    outputRange: [SEARCH_BUTTON_SIZE, expandedSearchWidth],
  });

  const searchAnimatedHeight = searchAnim.interpolate({
    inputRange: [0, 1],
    outputRange: [SEARCH_BUTTON_SIZE, TAB_BAR_HEIGHT],
  });

  const searchBorderRadius = searchAnim.interpolate({
    inputRange: [0, 1],
    outputRange: [SEARCH_BUTTON_SIZE / 2, TAB_BAR_HEIGHT / 2],
  });

  const tabLabelOpacity = searchAnim.interpolate({
    inputRange: [0, 0.3],
    outputRange: [1, 0],
    extrapolate: 'clamp',
  });

  const allPlayersTabOpacity = searchAnim.interpolate({
    inputRange: [0, 0.3],
    outputRange: [1, 0],
    extrapolate: 'clamp',
  });

  const searchInputOpacity = searchAnim.interpolate({
    inputRange: [0.5, 1],
    outputRange: [0, 1],
    extrapolate: 'clamp',
  });

  return (
    <View style={[styles.wrapper, { paddingBottom: insets.bottom + 12 }]}>
      <View style={styles.barRow}>
        {/* Tabs */}
        <Animated.View style={[styles.tabsGlass, { width: tabsAnimatedWidth }]}>
          <BlurView intensity={50} tint="light" style={styles.blurView}>
            <View style={styles.glassOverlay} />

            {!isSearching && (
              <Animated.View
                style={[
                  styles.indicator,
                  {
                    width: indicatorWidth,
                    height: TAB_BAR_HEIGHT - INDICATOR_VERTICAL_MARGIN * 2,
                    transform: [{ translateX }],
                  },
                ]}
              >
                <LinearGradient
                  colors={['rgba(255,255,255,0.8)', 'rgba(255,255,255,0.4)']}
                  start={{ x: 0, y: 0 }}
                  end={{ x: 0, y: 1 }}
                  style={styles.indicatorGradient}
                />
              </Animated.View>
            )}

            <View style={styles.tabsContainer}>
              {state.routes.map((route, index) => {
                const isFocused = state.index === index;
                const iconName = isFocused
                  ? TAB_ICONS[route.name].active
                  : TAB_ICONS[route.name].inactive;

                const onPress = () => {
                  if (isSearching) {
                    closeSearch();
                    return;
                  }
                  const event = navigation.emit({
                    type: 'tabPress',
                    target: route.key,
                    canPreventDefault: true,
                  });
                  if (!isFocused && !event.defaultPrevented) {
                    navigation.navigate(route.name);
                  }
                };

                if (isSearching && route.name !== 'Lineup') return null;

                return (
                  <TouchableOpacity
                    key={route.key}
                    onPress={onPress}
                    activeOpacity={0.7}
                    style={[styles.tab, isSearching && styles.tabCollapsed]}
                  >
                    <Ionicons
                      name={isSearching ? 'list-outline' : iconName}
                      size={20}
                      color={isFocused ? '#000000' : 'rgba(0,0,0,0.4)'}
                    />
                    {!isSearching && (
                      <Animated.Text
                        style={[
                          styles.label,
                          { color: isFocused ? '#000000' : 'rgba(0,0,0,0.4)' },
                          isFocused && styles.labelActive,
                          route.name === 'AllPlayers'
                            ? { opacity: allPlayersTabOpacity }
                            : { opacity: tabLabelOpacity },
                        ]}
                      >
                        {TAB_LABELS[route.name]}
                      </Animated.Text>
                    )}
                  </TouchableOpacity>
                );
              })}
            </View>
          </BlurView>
        </Animated.View>

        {/* Search */}
        <Animated.View
          style={[
            styles.searchGlass,
            {
              width: searchAnimatedWidth,
              height: searchAnimatedHeight,
              borderRadius: searchBorderRadius,
            },
          ]}
        >
          <BlurView intensity={50} tint="light" style={styles.searchBlur}>
            <View style={styles.glassOverlay} />
            {isSearching ? (
              <Animated.View style={[styles.searchBarContent, { opacity: searchInputOpacity }]}>
                <Ionicons name="search" size={18} color="rgba(0,0,0,0.4)" />
                <TextInput
                  ref={inputRef}
                  style={styles.searchInput}
                  placeholder="선수 검색"
                  placeholderTextColor="rgba(0,0,0,0.35)"
                  value={searchQuery}
                  onChangeText={setSearchQuery}
                  autoCorrect={false}
                />
                <TouchableOpacity onPress={closeSearch} activeOpacity={0.7}>
                  <Ionicons name="close-circle" size={20} color="rgba(0,0,0,0.4)" />
                </TouchableOpacity>
              </Animated.View>
            ) : (
              <TouchableOpacity
                activeOpacity={0.7}
                style={styles.searchButton}
                onPress={openSearch}
              >
                <Ionicons name="search" size={20} color="rgba(0,0,0,0.6)" />
              </TouchableOpacity>
            )}
          </BlurView>
        </Animated.View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    paddingHorizontal: WRAPPER_HORIZONTAL_PADDING,
  },
  barRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  tabsGlass: {
    height: TAB_BAR_HEIGHT,
    borderRadius: TAB_BAR_HEIGHT / 2,
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.15,
    shadowRadius: 24,
    elevation: 12,
  },
  blurView: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
  },
  glassOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(255, 255, 255, 0.7)',
    borderWidth: 1,
    borderColor: 'rgba(0, 0, 0, 0.08)',
    borderRadius: TAB_BAR_HEIGHT / 2,
  },
  indicator: {
    position: 'absolute',
    top: INDICATOR_VERTICAL_MARGIN,
    left: 0,
    borderRadius: (TAB_BAR_HEIGHT - INDICATOR_VERTICAL_MARGIN * 2) / 2,
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.08,
    shadowRadius: 8,
    elevation: 4,
  },
  indicatorGradient: {
    flex: 1,
    borderRadius: (TAB_BAR_HEIGHT - INDICATOR_VERTICAL_MARGIN * 2) / 2,
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.6)',
  },
  tabsContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    zIndex: 1,
  },
  tab: {
    alignItems: 'center',
    justifyContent: 'center',
    flexDirection: 'row',
    gap: 6,
    width: TAB_WIDTH,
    height: '100%',
  },
  tabCollapsed: {
    width: COLLAPSED_TAB_WIDTH,
  },
  label: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 13,
  },
  labelActive: {
    fontFamily: 'Pretendard-SemiBold',
  },
  searchGlass: {
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.15,
    shadowRadius: 24,
    elevation: 12,
  },
  searchBlur: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  searchButton: {
    width: '100%',
    height: '100%',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 1,
  },
  searchBarContent: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    gap: 10,
    width: '100%',
    zIndex: 1,
  },
  searchInput: {
    flex: 1,
    fontFamily: 'Pretendard-Regular',
    fontSize: 15,
    color: '#000000',
    height: 40,
    padding: 0,
  },
});
