package com.gms.cheerlotandroid.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class CheerLotNavigator(
    initialTab: CheerLotMainTab = CheerLotMainTab.LINEUP,
) {
    var selectedTab by mutableStateOf(initialTab)
        private set

    var routeStack by mutableStateOf<List<CheerLotRoute>>(emptyList())
        private set

    var currentSheet by mutableStateOf<CheerLotSheet?>(null)
        private set

    var currentFullScreen by mutableStateOf<CheerLotFullScreen?>(null)
        private set

    var currentDialog by mutableStateOf<CheerLotDialog?>(null)
        private set

    fun selectTab(tab: CheerLotMainTab) {
        selectedTab = tab
    }

    fun navigate(route: CheerLotRoute) {
        if (routeStack.lastOrNull() == route) return
        routeStack = routeStack + route
    }

    fun popBackStack(): Boolean {
        if (routeStack.isEmpty()) return false
        routeStack = routeStack.dropLast(1)
        return true
    }

    fun popToRoot() {
        routeStack = emptyList()
    }

    fun showSheet(sheet: CheerLotSheet) {
        currentFullScreen = null
        currentSheet = sheet
    }

    fun dismissSheet() {
        currentSheet = null
    }

    fun showFullScreen(fullScreen: CheerLotFullScreen) {
        currentSheet = null
        currentFullScreen = fullScreen
    }

    fun dismissFullScreen() {
        currentFullScreen = null
    }

    fun dismissModal() {
        currentSheet = null
        currentFullScreen = null
    }

    fun showDialog(dialog: CheerLotDialog) {
        currentDialog = dialog
    }

    fun dismissDialog() {
        currentDialog = null
    }
}

@Composable
fun rememberCheerLotNavigator(
    initialTab: CheerLotMainTab = CheerLotMainTab.LINEUP,
): CheerLotNavigator {
    return remember { CheerLotNavigator(initialTab) }
}
