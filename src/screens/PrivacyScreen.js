import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { colors } from '../constants/colors';

const PRIVACY_CONTENT = [
  {
    content:
      "쳐랏(이하 '회사')은 이용자의 개인정보를 소중히 생각하며, 관련 법령을 준수합니다. 「개인정보 보호법」, 「정보통신망 이용촉진 및 정보보호 등에 관한 법률」 등 관련 법령에 따라 이용자의 개인정보를 보호하고 관련한 고충을 신속하고 원활하게 처리할 수 있도록 다음과 같이 개인정보처리방침을 수립·공개합니다.\n\n회사는 본 앱에서 이용자의 개인정보를 수집하거나 저장, 처리하지 않습니다. 다만, 서비스 운영상 불가피하게 수집될 수 있는 최소한의 정보(예: 자동 생성 로그 등)는 아래와 같이 처리합니다.",
  },
  {
    title: '제1조 (개인정보의 수집 및 이용)',
    content:
      '회사는 본 애플리케이션(이하 "앱")을 통해 이용자의 개인정보를 직접적으로 수집하지 않습니다.\n\n다만, 서비스 품질 향상, 오류 분석, 통계 분석 등을 위하여 앱 이용 과정에서 자동으로 생성·수집되는 정보(기기 정보, 운영체제 정보, 이용기록 등)가 있을 수 있습니다.\n회사는 이용자의 정확한 위치정보를 수집하거나 이용하지 않습니다.',
  },
  {
    title: '제2조 (자동 수집 정보 및 쿠키)',
    content:
      '회사는 앱의 운영 과정에서 자동으로 생성되는 정보(기기명, OS 버전, IP 주소, 접속 기록, 오류 로그 등)만을 수집할 수 있으며, 이용자가 직접 개인정보를 입력하거나 제공해야 하는 경우는 없습니다.',
  },
  {
    title: '제3조 (개인정보의 제3자 제공 및 위탁)',
    content:
      '회사는 이용자의 동의 없이 개인정보를 제3자에게 제공하지 않습니다. 단, 법령에 특별한 규정이 있는 경우 또는 수사기관의 요청 등 관련 법령에 따라 제공이 필요한 경우에는 예외로 합니다.\n또한, 앱의 서비스 개선 및 오류 분석을 위해 Firebase Crashlytics, Amplitude 등 외부 서비스가 사용될 수 있으며, 이들 서비스는 각자의 개인정보처리방침에 따라 정보를 처리할 수 있습니다.\n• Firebase Crashlytics 개인정보처리방침\n• Amplitude 개인정보처리방침',
  },
  {
    title: '제4조 (개인정보 보호책임자)',
    content:
      '회사는 수집된 정보를 수집·이용 목적 달성 시 또는 이용자의 요청 시 지체 없이 파기하며, 관련 법령에 따라 보존이 필요한 경우에는 해당 기간 동안 안전하게 보관 후 파기합니다.\n이용자의 개인정보 보호와 관련한 문의는 아래로 연락해 주시기 바랍니다.\n개인정보 보호책임자: 신지현\n이메일: cheerlot.business@gmail.com',
  },
  {
    title: '제5조 (권익침해 구제방법)',
    content:
      '이용자는 개인정보와 관련된 문의, 신고, 상담이 필요할 경우 아래 기관에 문의할 수 있습니다.\n개인정보침해신고센터: 국번없이 118 (privacy.kisa.or.kr)\n개인정보분쟁조정위원회: 1833-6972 (www.kopico.go.kr)\n대검찰청: 1301 (www.spo.go.kr)\n경찰청: 182 (ecrm.cyber.go.kr)',
  },
  {
    title: '제6조 (방침의 변경)',
    content:
      '본 방침은 관련 법령 및 회사 정책에 따라 변경될 수 있으며, 변경 시 앱 내에 공지합니다.',
  },
];

export default function PrivacyScreen() {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={styles.topBar}>
        <TouchableOpacity onPress={() => navigation.goBack()} activeOpacity={0.7}>
          <Ionicons name="chevron-back" size={28} color={colors.text.primary} />
        </TouchableOpacity>
        <Text style={styles.topBarTitle}>개인정보처리방침</Text>
        <View style={styles.topBarSpacer} />
      </View>

      <ScrollView
        contentContainerStyle={[styles.scrollContent, { paddingBottom: insets.bottom + 40 }]}
        showsVerticalScrollIndicator={false}
      >
        {PRIVACY_CONTENT.map((section, index) => (
          <View key={index} style={styles.section}>
            {section.title && <Text style={styles.sectionTitle}>{section.title}</Text>}
            <Text style={styles.sectionContent}>{section.content}</Text>
          </View>
        ))}

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>부칙</Text>
          <Text style={styles.sectionContent}>본 방침은 2025년 7월 11일부터 시행합니다.</Text>
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
