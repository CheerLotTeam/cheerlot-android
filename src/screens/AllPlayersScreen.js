import { View, Text, StyleSheet } from 'react-native';
import { colors } from '../constants/colors';

export default function AllPlayersScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>전체선수</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.grayscale.black,
    alignItems: 'center',
    justifyContent: 'center',
  },
  title: {
    fontFamily: 'Pretendard-Bold',
    fontSize: 24,
    color: colors.text.inverse,
  },
});
