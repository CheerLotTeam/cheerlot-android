import { useRef, useEffect } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, Linking } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { colors } from '../constants/colors';
import { useTeam } from '../contexts/TeamContext';

const SECTIONS = [
  {
    title: '쳐랏팀',
    items: [
      { label: '쳐랏 인스타그램', icon: 'logo-instagram', url: 'https://www.instagram.com/cheer.lot/' },
      { label: '개발자 응원하기', icon: 'heart-outline', url: 'market://details?id=com.gms.cheerlotandroid' },
    ],
  },
  {
    title: '쳐랏 소개',
    items: [
      { label: '대표 페이지', icon: 'globe-outline', url: 'https://thrilling-chatter-055.notion.site/cheerlot' },
    ],
  },
  {
    title: '서비스 약관',
    items: [
      { label: '이용약관', icon: 'document-text-outline', screen: 'Terms' },
      { label: '개인정보처리방침', icon: 'shield-outline', screen: 'Privacy' },
      { label: '저작권 법적고지', icon: 'alert-circle-outline', screen: 'Copyright' },
    ],
  },
];

export default function ProfileScreen() {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();
  const { selectedTeam, teamInfo, openTeamSelect } = useTeam();
  const isChangingTeam = useRef(false);

  const teamColor = colors.team[selectedTeam]?.primary || colors.grayscale.gray800;

  useEffect(() => {
    if (isChangingTeam.current) {
      isChangingTeam.current = false;
      navigation.popToTop();
    }
  }, [selectedTeam]);

  const handleChangeTeam = () => {
    isChangingTeam.current = true;
    openTeamSelect();
  };

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={styles.topBar}>
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          activeOpacity={0.7}
          hitSlop={{ top: 12, bottom: 12, left: 12, right: 12 }}
          accessibilityLabel="뒤로"
          accessibilityRole="button"
        >
          <Ionicons name="chevron-back" size={28} color={colors.text.primary} />
        </TouchableOpacity>
        <Text style={styles.topBarTitle}>설정</Text>
        <View style={styles.topBarSpacer} />
      </View>

      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        {/* 나의 팀 */}
        <Text style={styles.sectionTitle}>나의 팀</Text>
        <View style={styles.teamCardOuter}>
          <TouchableOpacity activeOpacity={0.9} onPress={handleChangeTeam}>
            <LinearGradient
              colors={[teamColor, teamColor]}
              start={{ x: 0, y: 0 }}
              end={{ x: 0, y: 1 }}
              style={styles.teamCard}
            >
              <LinearGradient
                colors={['rgba(255,255,255,0.28)', 'rgba(255,255,255,0)']}
                start={{ x: 0, y: 0 }}
                end={{ x: 0, y: 1 }}
                style={styles.teamCardShine}
              />
              <LinearGradient
                colors={['rgba(0,0,0,0)', 'rgba(0,0,0,0.15)']}
                start={{ x: 0, y: 0 }}
                end={{ x: 0, y: 1 }}
                style={styles.teamCardDepth}
                pointerEvents="none"
              />
              <View style={styles.teamCardContent}>
                <Text style={styles.teamName}>{teamInfo?.nameEn || ''}</Text>
                <Text style={styles.teamSlogan}>{teamInfo?.slogan || ''}</Text>
              </View>
              <TouchableOpacity style={styles.moreButton} onPress={handleChangeTeam} activeOpacity={0.7}>
                <Ionicons name="ellipsis-horizontal" size={20} color="rgba(255,255,255,0.8)" />
              </TouchableOpacity>
            </LinearGradient>
          </TouchableOpacity>
        </View>

        {/* 나머지 섹션 */}
        {SECTIONS.map((section) => (
          <View key={section.title}>
            <Text style={styles.sectionTitle}>{section.title}</Text>
            <View style={styles.section}>
              {section.items.map((item, index) => (
                <TouchableOpacity
                  key={item.label}
                  style={[
                    styles.row,
                    index < section.items.length - 1 && styles.rowBorder,
                  ]}
                  activeOpacity={0.7}
                  onPress={() => {
                    if (item.screen) navigation.navigate(item.screen);
                    if (item.url) Linking.openURL(item.url);
                  }}
                >
                  <Text style={styles.rowLabel}>{item.label}</Text>
                  <Ionicons name="chevron-forward" size={18} color={colors.text.tertiary} />
                </TouchableOpacity>
              ))}
            </View>
          </View>
        ))}
      </ScrollView>
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
  scrollContent: {
    paddingHorizontal: 20,
    paddingBottom: 40,
  },
  sectionTitle: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 13,
    color: colors.text.tertiary,
    marginTop: 24,
    marginBottom: 8,
    marginLeft: 4,
  },
  teamCardOuter: {
    borderRadius: 28,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 20 },
    shadowOpacity: 0.28,
    shadowRadius: 40,
    elevation: 16,
  },
  teamCard: {
    borderRadius: 28,
    paddingVertical: 26,
    paddingHorizontal: 20,
    overflow: 'hidden',
  },
  teamCardShine: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: 100,
    borderTopLeftRadius: 28,
    borderTopRightRadius: 28,
  },
  teamCardDepth: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    height: 140,
    borderBottomLeftRadius: 28,
    borderBottomRightRadius: 28,
  },
  teamCardContent: {
    alignItems: 'center',
  },
  teamName: {
    fontFamily: 'RobotoCondensed-Black',
    fontSize: 26,
    color: colors.text.inverse,
    textAlign: 'center',
    marginBottom: 4,
  },
  teamSlogan: {
    fontFamily: 'Pretendard-Medium',
    fontSize: 13,
    color: 'rgba(255,255,255,0.7)',
    textAlign: 'center',
  },
  moreButton: {
    position: 'absolute',
    top: 16,
    right: 16,
    width: 32,
    height: 32,
    alignItems: 'center',
    justifyContent: 'center',
  },
  section: {
    backgroundColor: colors.background.tertiary,
    borderRadius: 12,
    overflow: 'hidden',
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 15,
    paddingHorizontal: 16,
  },
  rowBorder: {
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border.default,
  },
  rowLabel: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 15,
    color: colors.text.primary,
  },
});
