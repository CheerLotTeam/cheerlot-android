package com.gms.cheerlotandroid.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.presentation.main.MainTabView

@Composable
fun CheerLotApp() {
    MainTabView()
}

@Preview(showBackground = true)
@Composable
private fun CheerLotAppPreview() {
    CheerLotTheme {
        CheerLotApp()
    }
}
