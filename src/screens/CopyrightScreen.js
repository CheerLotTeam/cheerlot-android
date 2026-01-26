import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { colors } from '../constants/colors';

export default function CopyrightScreen() {
  const insets = useSafeAreaInsets();
  const navigation = useNavigation();

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={styles.topBar}>
        <TouchableOpacity onPress={() => navigation.goBack()} activeOpacity={0.7}>
          <Ionicons name="chevron-back" size={28} color={colors.text.primary} />
        </TouchableOpacity>
        <Text style={styles.topBarTitle}>저작권 법적고지</Text>
        <View style={styles.topBarSpacer} />
      </View>

      <ScrollView
        contentContainerStyle={[styles.scrollContent, { paddingBottom: insets.bottom + 40 }]}
        showsVerticalScrollIndicator={false}
      >
        <Text style={styles.paragraph}>
          쳐랏(이하 "회사")은 야구 응원 문화를 보다 쉽게 즐길 수 있도록 돕기 위한 비영리적 목적의 팬 중심 정보 제공 서비스입니다.
        </Text>

        <Text style={styles.paragraph}>
          본 애플리케이션(이하 "앱") 내에서 제공되는 응원가의 음원, 가사, 이미지 등은 어떠한 가공 없이 원본 그대로 표시되며, 그 모든 저작권은 해당 원저작권자(한국야구위원회(KBO), 각 구단, 창작자 등)에게 귀속됩니다. 앱은 상업적 목적 없이 운영되며, 응원가 관련 자료는 단순 정보 제공을 목적으로 사용되고 있습니다.
        </Text>

        <Text style={styles.paragraph}>
          따라서 본 앱의 모든 콘텐츠는 무단 복제, 배포, 공유, 2차 가공을 금지합니다.
        </Text>

        <Text style={styles.paragraph}>
          앱 내 이미지, 음원 등은 각 콘텐츠의 출처와 저작권을 존중하며 사용하였으며, 자료를 무단 공유하거나 배포, 수정의 행위는 저작권 침해에 해당할 수 있습니다.
        </Text>

        <Text style={styles.paragraph}>
          또한, 본 앱에서 제공하는 외부 콘텐츠는 해당 플랫폼의 정책 및 저작권 기준에 따릅니다. 저희는 타인의 저작권을 존중하며, 저작권 보유자의 요청이 있을 경우 즉시 수정 또는 삭제 조치를 취하겠습니다.
        </Text>

        <Text style={styles.sectionTitle}>책임 제한</Text>

        <Text style={styles.paragraph}>
          본 앱은 사용자 편의를 위해 정보를 제공하며, 외부 콘텐츠 사용이나 제3자의 권리에 관련된 법적 분쟁에 대해 법적 책임을 지지 않습니다.
        </Text>

        <Text style={styles.contact}>
          저작권 관련 문의: cheerlot.business@gmail.com
        </Text>
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
  paragraph: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 14,
    color: colors.text.secondary,
    lineHeight: 22,
    marginBottom: 20,
  },
  sectionTitle: {
    fontFamily: 'Pretendard-SemiBold',
    fontSize: 15,
    color: colors.text.primary,
    marginTop: 8,
    marginBottom: 12,
  },
  contact: {
    fontFamily: 'Pretendard-Regular',
    fontSize: 14,
    color: colors.text.secondary,
    marginTop: 8,
  },
});
