import { useState, useEffect } from 'react';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, View } from 'react-native';
import { useFonts } from 'expo-font';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import AsyncStorage from '@react-native-async-storage/async-storage';
import LineupScreen from './src/screens/LineupScreen';
import AllPlayersScreen from './src/screens/AllPlayersScreen';
import TeamSelectScreen from './src/screens/TeamSelectScreen';
import LiquidGlassTabBar from './src/components/LiquidGlassTabBar';

const Tab = createBottomTabNavigator();

export default function App() {
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [teamLoaded, setTeamLoaded] = useState(false);

  useEffect(() => {
    // TODO: 개발 테스트 후 아래 줄 제거
    AsyncStorage.removeItem('selectedTeam');
    AsyncStorage.getItem('selectedTeam').then((team) => {
      if (team) setSelectedTeam(team);
      setTeamLoaded(true);
    });
  }, []);

  const [fontsLoaded] = useFonts({
    'RobotoCondensed-Black': require('./src/assets/fonts/RobotoCondensed-Black.ttf'),
    'Pretendard-Thin': require('./src/assets/fonts/Pretendard-Thin.ttf'),
    'Pretendard-ExtraLight': require('./src/assets/fonts/Pretendard-ExtraLight.ttf'),
    'Pretendard-Light': require('./src/assets/fonts/Pretendard-Light.ttf'),
    'Pretendard-Regular': require('./src/assets/fonts/Pretendard-Regular.ttf'),
    'Pretendard-Medium': require('./src/assets/fonts/Pretendard-Medium.ttf'),
    'Pretendard-SemiBold': require('./src/assets/fonts/Pretendard-SemiBold.ttf'),
    'Pretendard-Bold': require('./src/assets/fonts/Pretendard-Bold.ttf'),
    'Pretendard-ExtraBold': require('./src/assets/fonts/Pretendard-ExtraBold.ttf'),
    'Pretendard-Black': require('./src/assets/fonts/Pretendard-Black.ttf'),
  });

  if (!fontsLoaded || !teamLoaded) {
    return null;
  }

  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <View style={styles.container}>
          <Tab.Navigator
            tabBar={(props) => <LiquidGlassTabBar {...props} />}
            screenOptions={{ headerShown: false }}
          >
            <Tab.Screen name="Lineup" component={LineupScreen} />
            <Tab.Screen name="AllPlayers" component={AllPlayersScreen} />
          </Tab.Navigator>
          {!selectedTeam && (
            <TeamSelectScreen onTeamSelect={(team) => setSelectedTeam(team)} />
          )}
          <StatusBar style="light" />
        </View>
      </NavigationContainer>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
