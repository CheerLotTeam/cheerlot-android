package com.gms.cheerlotandroid.core.di

import androidx.compose.runtime.staticCompositionLocalOf

// Composable 트리에서 AppContainer를 꺼내 쓸 수 있게 하는 진입점입니다.
val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer is not provided.")
}
