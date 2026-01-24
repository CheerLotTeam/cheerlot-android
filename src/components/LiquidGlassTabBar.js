import { useEffect, useRef } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Animated,
} from 'react-native';
import { BlurView } from 'expo-blur';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

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
const INDICATOR_HORIZONTAL_MARGIN = 6;
const INDICATOR_VERTICAL_MARGIN = 6;
const WRAPPER_HORIZONTAL_PADDING = 20;

export default function LiquidGlassTabBar({ state, navigation }) {
  const insets = useSafeAreaInsets();
  const tabCount = state.routes.length;
  const tabsContainerWidth = TAB_WIDTH * tabCount;
  const indicatorWidth = TAB_WIDTH - INDICATOR_HORIZONTAL_MARGIN * 2;

  const translateX = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.spring(translateX, {
      toValue: state.index * TAB_WIDTH + INDICATOR_HORIZONTAL_MARGIN,
      useNativeDriver: true,
      tension: 68,
      friction: 12,
    }).start();
  }, [state.index]);

  return (
    <View style={[styles.wrapper, { paddingBottom: insets.bottom + 12 }]}>
      <View style={styles.barRow}>
        {/* Tabs */}
        <View style={[styles.tabsGlass, { width: tabsContainerWidth }]}>
          <BlurView intensity={50} tint="dark" style={styles.blurView}>
            <View style={styles.glassOverlay} />

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
                colors={['rgba(255,255,255,0.3)', 'rgba(255,255,255,0.12)']}
                start={{ x: 0, y: 0 }}
                end={{ x: 0, y: 1 }}
                style={styles.indicatorGradient}
              />
            </Animated.View>

            <View style={styles.tabsContainer}>
              {state.routes.map((route, index) => {
                const isFocused = state.index === index;
                const iconName = isFocused
                  ? TAB_ICONS[route.name].active
                  : TAB_ICONS[route.name].inactive;

                const onPress = () => {
                  const event = navigation.emit({
                    type: 'tabPress',
                    target: route.key,
                    canPreventDefault: true,
                  });

                  if (!isFocused && !event.defaultPrevented) {
                    navigation.navigate(route.name);
                  }
                };

                return (
                  <TouchableOpacity
                    key={route.key}
                    onPress={onPress}
                    activeOpacity={0.7}
                    style={[styles.tab, { width: TAB_WIDTH }]}
                  >
                    <Ionicons
                      name={iconName}
                      size={20}
                      color={isFocused ? '#ffffff' : 'rgba(255,255,255,0.5)'}
                    />
                    <Text
                      style={[
                        styles.label,
                        { color: isFocused ? '#ffffff' : 'rgba(255,255,255,0.5)' },
                        isFocused && styles.labelActive,
                      ]}
                    >
                      {TAB_LABELS[route.name]}
                    </Text>
                  </TouchableOpacity>
                );
              })}
            </View>
          </BlurView>
        </View>

        {/* Search Button */}
        <View style={styles.searchGlass}>
          <BlurView intensity={50} tint="dark" style={styles.searchBlur}>
            <View style={styles.glassOverlay} />
            <TouchableOpacity
              activeOpacity={0.7}
              style={styles.searchButton}
              onPress={() => {
                // TODO: 검색 기능 연결
              }}
            >
              <Ionicons name="search" size={20} color="rgba(255,255,255,0.8)" />
            </TouchableOpacity>
          </BlurView>
        </View>
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
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.2)',
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
    borderColor: 'rgba(255, 255, 255, 0.25)',
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
    height: '100%',
  },
  label: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 13,
  },
  labelActive: {
    fontFamily: 'Pretendard-SemiBold',
  },
  searchGlass: {
    width: SEARCH_BUTTON_SIZE,
    height: SEARCH_BUTTON_SIZE,
    borderRadius: SEARCH_BUTTON_SIZE / 2,
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
});
