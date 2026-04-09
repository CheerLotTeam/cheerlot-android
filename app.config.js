import 'dotenv/config';

export default {
  expo: {
    name: '쳐랏',
    slug: 'cheerlot-android',
    version: '1.0.1',
    orientation: 'portrait',
    icon: './assets/icon.png',
    userInterfaceStyle: 'light',
    newArchEnabled: true,
    splash: {
      image: './src/assets/images/splash-image.png',
      resizeMode: 'contain',
      backgroundColor: '#ffffff',
    },
    ios: {
      supportsTablet: true,
      bundleIdentifier: 'com.gms.cheerlotandroid',
    },
    android: {
      adaptiveIcon: {
        foregroundImage: './assets/adaptive-icon.png',
        backgroundColor: '#ffffff',
      },
      edgeToEdgeEnabled: true,
      package: 'com.gms.cheerlotandroid',
    },
    web: {
      favicon: './assets/favicon.png',
    },
    plugins: [
      'expo-font',
      'expo-audio',
      [
        'expo-build-properties',
        {
          android: {
            usesCleartextTraffic: true,
          },
        },
      ],
    ],
    extra: {
      apiBaseUrl: process.env.API_BASE_URL || '',
      eas: {
        projectId: '76f6d245-078f-47ff-a20c-f6389f3d64cf',
      },
    },
  },
};
