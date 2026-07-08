package com.gms.cheerlotandroid.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gms.cheerlotandroid.core.navigation.CheerLotNavigator
import com.gms.cheerlotandroid.core.navigation.rememberCheerLotNavigator
import com.gms.cheerlotandroid.design.theme.CheerLotTheme

@Composable
fun CheerLotApp(
    navigator: CheerLotNavigator = rememberCheerLotNavigator(),
) {
    CheerLotNavHost(navigator = navigator)
}

@Preview(showBackground = true)
@Composable
private fun CheerLotAppPreview() {
    CheerLotTheme {
        CheerLotApp()
    }
}
