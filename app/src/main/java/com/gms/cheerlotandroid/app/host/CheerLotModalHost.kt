package com.gms.cheerlotandroid.app.host

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.core.navigation.CheerLotFullScreen
import com.gms.cheerlotandroid.core.navigation.CheerLotNavigator
import com.gms.cheerlotandroid.core.navigation.CheerLotSheet

// navigator.currentSheet/currentFullScreen을 관찰해 실제 modal UI로 그려주는 root host입니다.
// 실제 화면(CheerSongMenuSheet, PlaybackScreen 등)이 생기면 placeholder를 교체합니다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheerLotModalHost(navigator: CheerLotNavigator) {
    val sheet = navigator.currentSheet
    if (sheet != null) {
        ModalBottomSheet(onDismissRequest = navigator::dismissSheet) {
            SheetPlaceholderContent(sheet)
        }
    }

    val fullScreen = navigator.currentFullScreen
    if (fullScreen != null) {
        FullScreenPlaceholder(fullScreen, onClose = navigator::dismissFullScreen)
    }
}

// 임시뷰
@Composable
private fun SheetPlaceholderContent(sheet: CheerLotSheet) {
    val title = when (sheet) {
        is CheerLotSheet.CheerSongList -> "CheerSongList(playerId=${sheet.playerId})"
        is CheerLotSheet.LineupChange -> "LineupChange(playerId=${sheet.playerId})"
        is CheerLotSheet.TeamChange -> "TeamChange(selectedTeamId=${sheet.selectedTeamId})"
        CheerLotSheet.Inquiry -> "Inquiry"
        CheerLotSheet.ServicePage -> "ServicePage"
    }
    Text(text = "Sheet / $title", modifier = Modifier.padding(24.dp))
}

// 임시뷰
@Composable
private fun FullScreenPlaceholder(
    fullScreen: CheerLotFullScreen,
    onClose: () -> Unit,
) {
    val title = when (fullScreen) {
        is CheerLotFullScreen.LineupPlayback -> "LineupPlayback(startIndex=${fullScreen.startIndex})"
        is CheerLotFullScreen.BasePlayback -> "BasePlayback(playerName=${fullScreen.playerName})"
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                Text(text = "FullScreen / $title")
                Button(onClick = onClose) { Text("닫기") }
            }
        }
    }
}
