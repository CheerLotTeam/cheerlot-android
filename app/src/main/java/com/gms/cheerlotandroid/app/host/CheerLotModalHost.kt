package com.gms.cheerlotandroid.app.host

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.core.navigation.CheerLotFullScreen
import com.gms.cheerlotandroid.core.navigation.CheerLotPresentationState
import com.gms.cheerlotandroid.core.navigation.CheerLotSheet
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.domain.model.player.PlayerId
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.lineup.cheersongmenu.CheerSongMenuSheet
import com.gms.cheerlotandroid.presentation.lineupchange.LineupChangeSheet
import com.gms.cheerlotandroid.presentation.playback.PlaybackScreen
import com.gms.cheerlotandroid.presentation.playback.PlaybackViewModel

// presentationState의 modal 상태를 관찰해 실제 modal UI로 그려주는 root host입니다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheerLotModalHost(presentationState: CheerLotPresentationState) {
    val sheet = presentationState.currentSheet
    when (sheet) {
        is CheerLotSheet.CheerSongList -> {
            ModalBottomSheet(
                onDismissRequest = presentationState::dismissSheet,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                TeamTheme(teamId = sheet.member.teamId) {
                    CheerSongMenuSheet(
                        memberName = sheet.member.name,
                        cheerSongs = sheet.member.cheerSongs,
                        onSelectCheerSong = { cheerSong ->
                            val songIndex = sheet.member.cheerSongs.indexOfFirst {
                                it.id == cheerSong.id
                            }
                            if (songIndex >= 0) {
                                presentationState.showFullScreen(
                                    CheerLotFullScreen.LineupPlayback(
                                        startIndex = sheet.startIndex + songIndex
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }

        is CheerLotSheet.LineupChange -> {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = presentationState::dismissSheet,
                sheetState = sheetState,
                dragHandle = null,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                LineupChangeDummySheet(
                    sheet = sheet,
                    onDismiss = presentationState::dismissSheet
                )
            }
        }

        null -> Unit

        else -> {
            ModalBottomSheet(onDismissRequest = presentationState::dismissSheet) {
                SheetPlaceholderContent(sheet)
            }
        }
    }

    when (val fullScreen = presentationState.currentFullScreen) {
        is CheerLotFullScreen.BasePlayback -> {
            val viewModel: PlaybackViewModel =
                viewModel(factory = LocalAppContainer.current.viewModelFactory)
            val uiState by viewModel.uiState.collectAsState()
            val closePlayback = {
                viewModel.close(onClosed = presentationState::dismissFullScreen)
            }

            BackHandler(onBack = closePlayback)

            PlaybackScreen(
                state = uiState,
                onClose = closePlayback,
                onTogglePlayback = viewModel::togglePlayback,
                onSeek = viewModel::seek,
                onPlayNext = viewModel::playNext,
                onPlayPrevious = viewModel::playPrevious,
                onToggleShuffle = viewModel::toggleShuffle,
                onToggleRepeatOne = viewModel::toggleRepeatOne
            )
        }

        is CheerLotFullScreen.LineupPlayback -> {
            // 전체 화면 높이로 표시하되 사용자가 드래그해 Sheet를 내릴 수 없도록 합니다.
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = presentationState::dismissFullScreen,
                sheetState = sheetState,
                sheetGesturesEnabled = false,
                dragHandle = null,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                FullScreenPlaceholder(
                    fullScreen = fullScreen,
                    onClose = presentationState::dismissFullScreen
                )
            }
        }

        null -> Unit
    }
}

// 라인업 교체 임시뷰
@Composable
private fun LineupChangeDummySheet(
    sheet: CheerLotSheet.LineupChange,
    onDismiss: () -> Unit
) {
    val benchMembers = remember(sheet.member.teamId) {
        lineupChangeDummyMembers(sheet.member.teamId)
    }
    var selectedMemberId by remember(sheet.member.id) {
        mutableStateOf<PlayerId?>(null)
    }

    TeamTheme(teamId = sheet.member.teamId) {
        LineupChangeSheet(
            lineupMember = sheet.member,
            benchMembers = benchMembers,
            selectedMemberId = selectedMemberId,
            onSelectMember = { member ->
                selectedMemberId = if (selectedMemberId == member.id) null else member.id
            },
            onClose = onDismiss,
            onCheck = {}
        )
    }
}

// TODO: 선수 교체 기능 연결 시 전체 로스터에서 battingOrder가 없는 선수 목록으로 교체합니다.
private fun lineupChangeDummyMembers(teamId: TeamId): List<PlayerInfo> {
    return listOf("전준우", "윤동희", "황성빈", "나승엽", "고승민", "손호영")
        .mapIndexed { index, name ->
            PlayerInfo(
                id = PlayerId("lineup-change-dummy-${index + 1}"),
                teamId = teamId,
                name = name,
                backNumber = 0,
                position = "교체선수",
                batThrow = "",
                battingOrder = null,
                cheerSongs = emptyList()
            )
        }
}

// 임시뷰
@Composable
private fun SheetPlaceholderContent(sheet: CheerLotSheet) {
    val title = when (sheet) {
        is CheerLotSheet.CheerSongList -> "CheerSongList(memberId=${sheet.member.id})"
        is CheerLotSheet.LineupChange -> "LineupChange(memberId=${sheet.member.id})"
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
