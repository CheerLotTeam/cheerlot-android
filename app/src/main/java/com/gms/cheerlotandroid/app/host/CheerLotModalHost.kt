package com.gms.cheerlotandroid.app.host

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.core.navigation.CheerLotFullScreen
import com.gms.cheerlotandroid.core.navigation.CheerLotPresentationState
import com.gms.cheerlotandroid.core.navigation.CheerLotSheet
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.presentation.lineup.cheersongmenu.CheerSongMenuSheet
import com.gms.cheerlotandroid.presentation.lineupchange.LineupChangeScreen
import com.gms.cheerlotandroid.presentation.lineupplayback.LineupPlaybackScreen
import com.gms.cheerlotandroid.presentation.onboarding.TeamSelectMode
import com.gms.cheerlotandroid.presentation.onboarding.TeamSelectScreen
import com.gms.cheerlotandroid.presentation.playback.PlaybackScreen
import com.gms.cheerlotandroid.presentation.playback.PlaybackViewModel
import com.gms.cheerlotandroid.presentation.settings.component.InquiryWebViewSheet
import kotlinx.coroutines.launch

// presentationState의 modal 상태를 관찰해 실제 modal UI로 그려주는 root host입니다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheerLotModalHost(presentationState: CheerLotPresentationState) {
    val sheet = presentationState.currentSheet
    when (sheet) {
        is CheerLotSheet.CheerSongList -> {
            // TODO: CheerSongMenuScreen을 Stateful 진입점으로 만들고 TeamTheme, 재생 시작, ViewModel/UseCase 연결을 feature 계층으로 옮겨 Host는 Sheet 표시만 담당하게 합니다.
            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()
            val playLineupSongsUseCase = LocalAppContainer.current.playLineupSongsUseCase

            ModalBottomSheet(
                onDismissRequest = presentationState::dismissSheet,
                sheetState = sheetState,
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
                                val targetIndex = sheet.startIndex + songIndex
                                playLineupSongsUseCase(
                                    songs = sheet.queueSongs,
                                    playerNames = sheet.queuePlayerNames,
                                    startAt = targetIndex,
                                    teamId = sheet.member.teamId,
                                    playerIds = sheet.queuePlayerIds,
                                    isGameDay = sheet.isGameDay,
                                )
                                scope.launch {
                                    sheetState.hide()
                                    if (!sheetState.isVisible) {
                                        presentationState.showFullScreen(
                                            CheerLotFullScreen.LineupPlayback(
                                                startIndex = targetIndex
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        is CheerLotSheet.LineupChange -> {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val scope = rememberCoroutineScope()
            val dismissWithAnimation: () -> Unit = {
                scope.launch {
                    sheetState.hide()
                    if (!sheetState.isVisible) {
                        presentationState.dismissSheet()
                    }
                }
            }

            ModalBottomSheet(
                onDismissRequest = presentationState::dismissSheet,
                sheetState = sheetState,
                dragHandle = null,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                LineupChangeScreen(
                    lineupMember = sheet.member,
                    onClose = dismissWithAnimation,
                    onShowDialog = presentationState::showDialog
                )
            }
        }

        is CheerLotSheet.TeamChange -> {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val scope = rememberCoroutineScope()
            val dismissWithAnimation: () -> Unit = {
                scope.launch {
                    sheetState.hide()
                    if (!sheetState.isVisible) {
                        presentationState.dismissSheet()
                    }
                }
            }

            ModalBottomSheet(
                onDismissRequest = presentationState::dismissSheet,
                sheetState = sheetState,
                sheetGesturesEnabled = false,
                dragHandle = null,
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = {
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                }
            ) {
                TeamSelectScreen(
                    mode = TeamSelectMode.CHANGE,
                    onComplete = dismissWithAnimation,
                    onClose = dismissWithAnimation
                )
            }
        }

        is CheerLotSheet.Inquiry -> {
            ModalBottomSheet(
                onDismissRequest = presentationState::dismissSheet,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                InquiryWebViewSheet()
            }
        }

        null -> Unit
    }

    when (val fullScreen = presentationState.currentFullScreen) {
        is CheerLotFullScreen.BasePlayback -> {
            // TODO: PlaybackScreen을 Stateful/Stateless로 분리하고 ViewModel 생성과 상태·이벤트 연결을 feature 계층으로 옮겨 Host는 전체 화면 modal 표시만 담당하게 합니다.
            val viewModel: PlaybackViewModel =
                viewModel(factory = LocalAppContainer.current.viewModelFactory)
            val uiState by viewModel.uiState.collectAsState()
            val closePlayback = {
                viewModel.close(onClosed = presentationState::dismissFullScreen)
            }

            LaunchedEffect(viewModel) {
                viewModel.trackPresented()
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
            val scope = rememberCoroutineScope()
            val dismissWithAnimation: () -> Unit = {
                scope.launch {
                    sheetState.hide()
                    if (!sheetState.isVisible) {
                        presentationState.dismissFullScreen()
                    }
                }
            }

            ModalBottomSheet(
                onDismissRequest = presentationState::dismissFullScreen,
                sheetState = sheetState,
                sheetGesturesEnabled = false,
                dragHandle = null,
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = {
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                }
            ) {
                LineupPlaybackScreen(
                    startIndex = fullScreen.startIndex,
                    onClose = dismissWithAnimation
                )
            }
        }

        null -> Unit
    }
}
