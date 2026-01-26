import { useCallback } from 'react';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, View } from 'react-native';
import { useFonts } from 'expo-font';
import * as SplashScreen from 'expo-splash-screen';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import LineupScreen from './src/screens/LineupScreen';
import AllPlayersScreen from './src/screens/AllPlayersScreen';
import ProfileScreen from './src/screens/ProfileScreen';
import LineupPlayerScreen from './src/screens/LineupPlayerScreen';
import CheerPlayerScreen from './src/screens/CheerPlayerScreen';
import SubstituteScreen from './src/screens/SubstituteScreen';
import TeamSelectScreen from './src/screens/TeamSelectScreen';
import TermsScreen from './src/screens/TermsScreen';
import PrivacyScreen from './src/screens/PrivacyScreen';
import CopyrightScreen from './src/screens/CopyrightScreen';
import LiquidGlassTabBar from './src/components/LiquidGlassTabBar';
import { SearchProvider } from './src/contexts/SearchContext';
import { TeamProvider, useTeam } from './src/contexts/TeamContext';

const Tab = createBottomTabNavigator();
const Stack = createNativeStackNavigator();

SplashScreen.preventAutoHideAsync();

function HomeTabs() {
  const { selectedTeam } = useTeam();

  return (
    <Tab.Navigator
      tabBar={(props) => <LiquidGlassTabBar {...props} />}
      screenOptions={{ headerShown: false }}
    >
      <Tab.Screen name="Lineup">
        {() => <LineupScreen selectedTeam={selectedTeam} />}
      </Tab.Screen>
      <Tab.Screen name="AllPlayers">
        {() => <AllPlayersScreen selectedTeam={selectedTeam} />}
      </Tab.Screen>
    </Tab.Navigator>
  );
}

function AppContent() {
  const { isLoaded, showTeamSelect } = useTeam();

  if (!isLoaded) {
    return null;
  }

  return (
    <NavigationContainer>
      <View style={styles.container}>
        <Stack.Navigator screenOptions={{ headerShown: false }}>
          <Stack.Screen name="HomeTabs" component={HomeTabs} />
          <Stack.Screen name="Profile" component={ProfileScreen} />
          <Stack.Screen name="LineupPlayer" component={LineupPlayerScreen} />
          <Stack.Screen name="CheerPlayer" component={CheerPlayerScreen} />
          <Stack.Screen name="Substitute" component={SubstituteScreen} />
          <Stack.Screen name="Terms" component={TermsScreen} />
          <Stack.Screen name="Privacy" component={PrivacyScreen} />
          <Stack.Screen name="Copyright" component={CopyrightScreen} />
        </Stack.Navigator>
        {showTeamSelect && <TeamSelectScreen />}
        <StatusBar style="dark" />
      </View>
    </NavigationContainer>
  );
}

export default function App() {
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

  const onLayoutRootView = useCallback(async () => {
    if (fontsLoaded) {
      await SplashScreen.hideAsync();
    }
  }, [fontsLoaded]);

  if (!fontsLoaded) {
    return null;
  }

  return (
    <SafeAreaProvider onLayout={onLayoutRootView}>
      <TeamProvider>
        <SearchProvider>
          <AppContent />
        </SearchProvider>
      </TeamProvider>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
