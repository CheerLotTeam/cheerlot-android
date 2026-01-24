import { useState, useEffect } from 'react';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, View } from 'react-native';
import { useFonts } from 'expo-font';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import AsyncStorage from '@react-native-async-storage/async-storage';
import LineupScreen from './src/screens/LineupScreen';
import AllPlayersScreen from './src/screens/AllPlayersScreen';
import ProfileScreen from './src/screens/ProfileScreen';
import LineupPlayerScreen from './src/screens/LineupPlayerScreen';
import CheerPlayerScreen from './src/screens/CheerPlayerScreen';
import TeamSelectScreen from './src/screens/TeamSelectScreen';
import LiquidGlassTabBar from './src/components/LiquidGlassTabBar';

const Tab = createBottomTabNavigator();
const Stack = createNativeStackNavigator();

export default function App() {
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [teamLoaded, setTeamLoaded] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

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

  function HomeTabs() {
    return (
      <Tab.Navigator
        tabBar={(props) => (
          <LiquidGlassTabBar
            {...props}
            searchQuery={searchQuery}
            onSearchChange={setSearchQuery}
          />
        )}
        screenOptions={{ headerShown: false }}
      >
        <Tab.Screen name="Lineup" component={LineupScreen} />
        <Tab.Screen name="AllPlayers">
          {() => <AllPlayersScreen searchQuery={searchQuery} />}
        </Tab.Screen>
      </Tab.Navigator>
    );
  }

  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <View style={styles.container}>
          <Stack.Navigator screenOptions={{ headerShown: false }}>
            <Stack.Screen name="HomeTabs" component={HomeTabs} />
            <Stack.Screen name="Profile" component={ProfileScreen} />
            <Stack.Screen name="LineupPlayer" component={LineupPlayerScreen} />
            <Stack.Screen name="CheerPlayer" component={CheerPlayerScreen} />
          </Stack.Navigator>
          {!selectedTeam && (
            <TeamSelectScreen onTeamSelect={(team) => setSelectedTeam(team)} />
          )}
          <StatusBar style="dark" />
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
