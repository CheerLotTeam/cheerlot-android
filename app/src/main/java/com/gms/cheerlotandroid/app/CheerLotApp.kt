package com.gms.cheerlotandroid.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
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
    CompositionLocalProvider(LocalAppContainer provides appContainer) {
        CheerLotNavHost(navigator = navigator)
    }
}

@Preview(showBackground = true)
@Composable
private fun CheerLotAppPreview() {
    CheerLotTheme {
        CheerLotNavHost(navigator = rememberCheerLotNavigator())
    }
}
