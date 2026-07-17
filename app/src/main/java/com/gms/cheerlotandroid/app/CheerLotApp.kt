package com.gms.cheerlotandroid.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.gms.cheerlotandroid.app.host.CheerLotDialogHost
import com.gms.cheerlotandroid.app.host.CheerLotModalHost
import com.gms.cheerlotandroid.app.host.CheerLotNavHost
import com.gms.cheerlotandroid.core.di.AppContainer
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.core.navigation.CheerLotNavigator
import com.gms.cheerlotandroid.core.navigation.rememberCheerLotNavigator
import com.gms.cheerlotandroid.design.theme.CheerLotTheme

@Composable
fun CheerLotApp(
    appContainer: AppContainer,
    navigator: CheerLotNavigator = rememberCheerLotNavigator(),
) {
    // 하위 Composable은 LocalAppContainer.current로 앱 전역 의존성에 접근합니다.
    CompositionLocalProvider(LocalAppContainer provides appContainer) {
        CheerLotNavHost(navigator = navigator)
        CheerLotDialogHost(navigator = navigator)
        CheerLotModalHost(navigator = navigator)
    }
}

// CheerLotApp 전체가 아니라 CheerLotNavHost만 렌더링하는 프리뷰입니다.
// LocalAppContainer가 제공되지 않으므로, LocalAppContainer.current에 접근하는
// 화면이 추가되면 이 프리뷰는 크래시납니다 — 그 시점에 화면 단위 프리뷰로 대응합니다.
@Preview(showBackground = true)
@Composable
private fun CheerLotNavHostPreview() {
    CheerLotTheme {
        CheerLotNavHost(navigator = rememberCheerLotNavigator())
    }
}
