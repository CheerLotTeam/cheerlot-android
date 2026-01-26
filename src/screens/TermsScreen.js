import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { colors } from '../constants/colors';

const TERMS_CONTENT = [
  {
    title: '제1조 (목적)',
    content:
      "이 약관은 쳐랏(이하 '회사')이 제공하는 야구 응원가 서비스(이하 '서비스')의 이용과 관련하여 회사와 이용자 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.",
  },
  {
    title: '제2조 (정의)',
    content:
      "'이용자'란 본 약관에 따라 회사가 제공하는 서비스를 이용하는 자를 말합니다.",
  },
  {
    title: '제3조 (약관의 효력 및 변경)',
    content:
      "'이용자'란 본 약관에 따라 회사가 제공하는 서비스를 이용하는 자를 말합니다.",
  },
  {
    title: '제4조 (서비스의 제공 및 변경)',
    content:
      '회사는 연중무휴, 1일 24시간 서비스를 제공합니다. 단, 시스템 점검 등 불가피한 사유가 있는 경우 서비스 제공이 일시 중단될 수 있습니다.\n서비스의 내용은 회사의 정책에 따라 변경될 수 있습니다.',
  },
  {
    title: '제5조 (개인정보보호)',
    content:
      '회사는 본 서비스 이용과 관련하여 별도의 개인정보를 수집하지 않습니다.',
  },
  {
    title: '제6조 (저작권)',
    content:
      '서비스 내 제공되는 모든 콘텐츠(응원가 등)의 저작권은 회사 또는 정당한 권리자에게 귀속되며, 이용자는 회사가 정한 범위 내에서만 이를 이용할 수 있습니다.',
  },
  {
    title: '제7조 (면책)',
    content:
      '회사는 천재지변 등 불가항력 사유로 인한 서비스 중단에 대해 책임을 지지 않습니다.\n회사는 이용자의 귀책사유로 인한 서비스 이용 장애에 대해 책임을 지지 않습니다.',
  },
  {
    title: '제8조 (관할법원 및 준거법)',
    content:
      '서비스와 관련된 분쟁은 대한민국 법을 적용하며, 관할법원은 민사소송법에 따릅니다.',
  },
];

export default function TermsScreen() {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={styles.topBar}>
        <TouchableOpacity onPress={() => navigation.goBack()} activeOpacity={0.7}>
          <Ionicons name="chevron-back" size={28} color={colors.text.primary} />
        </TouchableOpacity>
        <Text style={styles.topBarTitle}>이용약관</Text>
        <View style={styles.topBarSpacer} />
      </View>

      <ScrollView
        contentContainerStyle={[styles.scrollContent, { paddingBottom: insets.bottom + 40 }]}
        showsVerticalScrollIndicator={false}
      >
        {TERMS_CONTENT.map((section) => (
          <View key={section.title} style={styles.section}>
            <Text style={styles.sectionTitle}>{section.title}</Text>
            <Text style={styles.sectionContent}>{section.content}</Text>
          </View>
        ))}

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>부칙</Text>
          <Text style={styles.sectionContent}>본 약관은 2025년 7월 11일부터 시행합니다.</Text>
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
  },
  section: {
    marginBottom: 28,
  },
  sectionTitle: {
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 15,
    color: colors.text.primary,
    marginBottom: 8,
  },
  sectionContent: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 14,
    color: colors.text.secondary,
    lineHeight: 22,
  },
});
