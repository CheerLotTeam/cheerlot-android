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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.core.navigation.CheerLotFullScreen
import com.gms.cheerlotandroid.core.navigation.CheerLotPresentationState
import com.gms.cheerlotandroid.core.navigation.CheerLotSheet
import com.gms.cheerlotandroid.presentation.playback.PlaybackScreen
import com.gms.cheerlotandroid.presentation.playback.PlaybackViewModel

// presentationState의 modal 상태를 관찰해 실제 modal UI로 그려주는 root host입니다.
// 실제 화면(CheerSongMenuSheet 등)이 생기면 placeholder를 교체합니다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheerLotModalHost(presentationState: CheerLotPresentationState) {
    val sheet = presentationState.currentSheet
    if (sheet != null) {
        ModalBottomSheet(onDismissRequest = presentationState::dismissSheet) {
            SheetPlaceholderContent(sheet)
        }
    }

    when (val fullScreen = presentationState.currentFullScreen) {
        is CheerLotFullScreen.BasePlayback -> {
            val viewModel: PlaybackViewModel =
                viewModel(factory = LocalAppContainer.current.viewModelFactory)
            val uiState by viewModel.uiState.collectAsState()

            PlaybackScreen(
                state = uiState,
                onClose = {
                    viewModel.close(onClosed = presentationState::dismissFullScreen)
                },
                onTogglePlayback = viewModel::togglePlayback,
                onSeek = viewModel::seek,
                onPlayNext = viewModel::playNext,
                onPlayPrevious = viewModel::playPrevious,
                onToggleShuffle = viewModel::toggleShuffle,
                onToggleRepeatOne = viewModel::toggleRepeatOne
            )
        }

        is CheerLotFullScreen.LineupPlayback -> {
            FullScreenPlaceholder(fullScreen, onClose = presentationState::dismissFullScreen)
        }

        null -> Unit
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
