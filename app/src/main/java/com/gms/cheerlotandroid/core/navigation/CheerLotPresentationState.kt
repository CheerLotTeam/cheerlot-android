package com.gms.cheerlotandroid.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// modal 표시 요청을 관리하는 상태 객체입니다.
// 화면 이동은 NavController가 단일 기준으로 관리하고,
// ModalBottomSheet/Dialog 연결은 app layer의 root composable이 담당합니다.
// ViewModel은 이 객체를 직접 들고 있기보다 navigation event를 노출하는 방식을 우선합니다.
class CheerLotPresentationState {
    // 현재 표시 중인 Bottom Sheet입니다.
    var currentSheet by mutableStateOf<CheerLotSheet?>(null)
        private set

    // 현재 표시 중인 전체 화면 modal입니다.
    var currentFullScreen by mutableStateOf<CheerLotFullScreen?>(null)
        private set

    // 현재 표시 중인 Dialog입니다.
    var currentDialog by mutableStateOf<CheerLotDialog?>(null)
        private set

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
}

@Composable
fun rememberCheerLotPresentationState(): CheerLotPresentationState {
    // 일시적인 overlay는 configuration change 또는 프로세스 복원 시 다시 열지 않습니다.
    return remember { CheerLotPresentationState() }
}
