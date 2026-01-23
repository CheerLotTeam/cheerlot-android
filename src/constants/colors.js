// Color Primitives
const GRAYSCALE = {
  white: '#FFFFFF',
  gray50: '#F9FAFB',
  gray100: '#F3F4F6',
  gray200: '#E5E7EB',
  gray300: '#D1D5DB',
  gray400: '#9CA3AF',
  gray500: '#6B7280',
  gray600: '#4B5563',
  gray700: '#374151',
  gray800: '#1F2937',
  gray900: '#111827',
  black: '#000000',
};

const TEAM_COLORS = {
  kia: {
    primary: '#EA0029',
    secondary: '#000000',
  },
  samsung: {
    primary: '#074CA1',
    secondary: '#FFFFFF',
  },
  lg: {
    primary: '#C30452',
    secondary: '#000000',
  },
  doosan: {
    primary: '#131230',
    secondary: '#FFFFFF',
  },
  kt: {
    primary: '#000000',
    secondary: '#EB1F28',
  },
  ssg: {
    primary: '#CE0E2D',
    secondary: '#B8964B',
  },
  nc: {
    primary: '#315288',
    secondary: '#B99E67',
  },
  hanwha: {
    primary: '#FF6600',
    secondary: '#000000',
  },
  lotte: {
    primary: '#002955',
    secondary: '#D21F37',
  },
  kiwoom: {
    primary: '#820024',
    secondary: '#000000',
  },
};

// Color Semantics
export const colors = {
  background: {
    primary: GRAYSCALE.white,
    secondary: GRAYSCALE.gray50,
    tertiary: GRAYSCALE.gray100,
  },
  text: {
    primary: GRAYSCALE.gray900,
    secondary: GRAYSCALE.gray600,
    tertiary: GRAYSCALE.gray400,
    inverse: GRAYSCALE.white,
  },
  border: {
    default: GRAYSCALE.gray200,
    strong: GRAYSCALE.gray300,
  },
  grayscale: GRAYSCALE,
  team: TEAM_COLORS,
};
