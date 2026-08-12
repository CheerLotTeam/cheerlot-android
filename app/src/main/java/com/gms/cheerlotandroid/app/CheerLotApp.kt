package com.gms.cheerlotandroid.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.gms.cheerlotandroid.app.host.AppFlowRoot
import com.gms.cheerlotandroid.app.host.CheerLotNavHost
import com.gms.cheerlotandroid.core.di.AppContainer
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.core.navigation.CheerLotPresentationState
import com.gms.cheerlotandroid.core.navigation.rememberCheerLotPresentationState
import com.gms.cheerlotandroid.design.theme.CheerLotTheme

@Composable
fun CheerLotApp(
    appContainer: AppContainer,
    onExitApp: () -> Unit,
    onOpenStore: () -> Unit,
    presentationState: CheerLotPresentationState = rememberCheerLotPresentationState(),
) {
    // 하위 Composable은 LocalAppContainer.current로 앱 전역 의존성에 접근합니다.
    CompositionLocalProvider(LocalAppContainer provides appContainer) {
        AppFlowRoot(
            presentationState = presentationState,
            onExitApp = onExitApp,
            onOpenStore = onOpenStore,
        )
    }
}

// CheerLotApp 전체가 아니라 CheerLotNavHost만 렌더링하는 프리뷰입니다.
// LocalAppContainer가 제공되지 않으므로, LocalAppContainer.current에 접근하는
// 화면이 추가되면 이 프리뷰는 크래시납니다 — 그 시점에 화면 단위 프리뷰로 대응합니다.
@Preview(showBackground = true)
@Composable
private fun CheerLotNavHostPreview() {
    CheerLotTheme {
        CheerLotNavHost(presentationState = rememberCheerLotPresentationState())
    }
}
