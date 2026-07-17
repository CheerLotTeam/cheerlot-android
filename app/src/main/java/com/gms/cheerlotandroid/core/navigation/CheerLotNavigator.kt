package com.gms.cheerlotandroid.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

// 화면 이동과 modal 표시 요청을 한 곳에서 관리하는 상태 객체입니다.
// Composable은 NavController를 직접 다루지 않고 이 Navigator에 이동을 요청하며,
// 실제 NavController/ModalBottomSheet/Dialog 연결은 app layer의 root composable이 담당합니다.
// ViewModel은 이 객체를 직접 들고 있기보다 navigation event를 노출하는 방식을 우선합니다.
class CheerLotNavigator(
    initialTab: CheerLotMainTab = CheerLotMainTab.LINEUP,
) {
    // 메인 화면 내부 탭 상태입니다.
    var selectedTab by mutableStateOf(initialTab)
        private set

    // iOS Coordinator의 paths처럼 push 화면 stack을 표현합니다.
    var routeStack by mutableStateOf<List<CheerLotRoute>>(emptyList())
        private set

    // 현재 표시 중인 Bottom Sheet입니다.
    var currentSheet by mutableStateOf<CheerLotSheet?>(null)
        private set

    // 현재 표시 중인 전체 화면 modal입니다.
    var currentFullScreen by mutableStateOf<CheerLotFullScreen?>(null)
        private set

    // 현재 표시 중인 Dialog입니다.
    var currentDialog by mutableStateOf<CheerLotDialog?>(null)
        private set

    fun selectTab(tab: CheerLotMainTab) {
        selectedTab = tab
    }

    fun navigate(route: CheerLotRoute) {
        // 같은 route를 연속으로 push하지 않도록 막습니다.
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
        // sheet와 full screen은 동시에 표시하지 않습니다.
        currentFullScreen = null
        currentSheet = sheet
    }

    fun dismissSheet() {
        currentSheet = null
    }

    fun showFullScreen(fullScreen: CheerLotFullScreen) {
        // full screen modal을 띄울 때 열려 있던 sheet는 정리합니다.
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

    companion object {
        // selectedTab과 routeStack만 route 문자열 리스트로 직렬화합니다.
        // Sheet/FullScreen/Dialog는 configuration change 시 자연스럽게 닫히는 것을 의도한 동작으로 보고 저장하지 않습니다. (TODO: 추후 수정 가능)
        val Saver: Saver<CheerLotNavigator, List<String>> = Saver(
            save = { navigator ->
                listOf(navigator.selectedTab.name) + navigator.routeStack.map { it.route }
            },
            restore = { saved ->
                val tab = CheerLotMainTab.valueOf(saved.first())
                val routeStack = saved.drop(1).mapNotNull(CheerLotRoute::fromRoute)
                CheerLotNavigator(initialTab = tab).apply {
                    routeStack.forEach(::navigate)
                }
            }
        )
    }
}

@Composable
fun rememberCheerLotNavigator(
    initialTab: CheerLotMainTab = CheerLotMainTab.LINEUP,
): CheerLotNavigator {
    return rememberSaveable(saver = CheerLotNavigator.Saver) { CheerLotNavigator(initialTab) }
}
