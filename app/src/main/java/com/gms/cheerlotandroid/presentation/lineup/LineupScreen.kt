package com.gms.cheerlotandroid.presentation.lineup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.core.navigation.CheerLotDialog
import com.gms.cheerlotandroid.design.component.CustomToastMessage
import com.gms.cheerlotandroid.design.component.CustomTopAppBarTitleWithProfile
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.player.PlayerId
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.lineup.component.LineupCard

private val cardHorizontalPadding = 20.dp
private val cardBottomPadding = 10.dp

@Composable
fun LineupScreen(
    onOpenSettings: () -> Unit,
    onChangePlayer: (PlayerInfo) -> Unit,
    onShowDialog: (CheerLotDialog) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LineupViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.errorMessage) {
        val errorMessage = uiState.errorMessage ?: return@LaunchedEffect
        onShowDialog(
            CheerLotDialog.Error(
                message = errorMessage,
                onRetry = viewModel::refresh
            )
        )
        viewModel.dismissError()
    }

    LineupContent(
        state = uiState,
        onPlayerClick = { player ->
            // ShowSongList/GoToPlayback은 실제 네비게이션에 연결합니다.
            viewModel.onPlayerTap(player)
        },
        onToggleShowLineup = viewModel::toggleShowLineup,
        onChangePlayer = onChangePlayer,
        onRefresh = viewModel::refresh,
        onDismissToast = viewModel::dismissToast,
        onProfileClick = onOpenSettings,
        modifier = modifier
    )
}

@Composable
private fun LineupContent(
    state: LineupUiState,
    onPlayerClick: (PlayerInfo) -> Unit,
    onToggleShowLineup: () -> Unit,
    onChangePlayer: (PlayerInfo) -> Unit,
    onRefresh: () -> Unit,
    onDismissToast: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CustomTopAppBarTitleWithProfile(
                title = "선발 라인업",
                onProfileClick = onProfileClick
            )
        },
        // 이 화면은 MainScreen의 Scaffold(bottomBar만 정의) 안에 얹혀 있습니다.
        // 여기서는 이 화면 고유의 topBar가 상태바를 처리할 수 있게 top만 둡니다.
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val viewportHeight = maxHeight

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(viewportHeight)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = TeamTheme.colors.primary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            val teamColors = TeamTheme.colors
                            val lineupColors = remember(teamColors) {
                                teamColors.toLineupColors()
                            }

                            LineupCard(
                                state = state,
                                colors = lineupColors,
                                onPlayerClick = onPlayerClick,
                                onChangePlayer = onChangePlayer,
                                onToggleShowLineup = onToggleShowLineup,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        start = cardHorizontalPadding,
                                        end = cardHorizontalPadding,
                                        bottom = cardBottomPadding
                                    )
                            )
                        }
                    }
                }
            }

            CustomToastMessage(
                message = state.toastMessage,
                isVisible = state.isToastVisible,
                onDismiss = onDismissToast
            )
        }
    }
}

private val previewPlayers = listOf(
    PlayerInfo(
        id = PlayerId("1"),
        teamId = TeamId("LOTTE"),
        name = "김민석",
        backNumber = 51,
        position = "외야수",
        batThrow = "좌투좌타",
        battingOrder = 1,
        cheerSongs = listOf(CheerSongInfo(id = "1", title = "안타송", lyrics = "", audioUrl = ""))
    ),
    PlayerInfo(
        id = PlayerId("2"),
        teamId = TeamId("LOTTE"),
        name = "황성빈",
        backNumber = 47,
        position = "외야수",
        batThrow = "좌투좌타",
        battingOrder = 2,
        cheerSongs = emptyList()
    )
)

@DevicePreviews
@Preview(showBackground = true, name = "Playing Today")
@Composable
private fun LineupContentPlayingTodayPreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("LOTTE")) {
            LineupContent(
                state = LineupUiState(
                    teamId = TeamId("LOTTE"),
                    teamEnglishName = "LOTTE GIANTS",
                    teamShortName = "롯데",
                    opponentTeamName = "삼성",
                    players = previewPlayers,
                    isLoading = false
                ),
                onPlayerClick = {},
                onToggleShowLineup = {},
                onChangePlayer = {},
                onRefresh = {},
                onDismissToast = {},
                onProfileClick = {}
            )
        }
    }
}

@DevicePreviews
@Preview(showBackground = true, name = "Off Day")
@Composable
private fun LineupContentOffDayPreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("LOTTE")) {
            LineupContent(
                state = LineupUiState(
                    teamId = TeamId("LOTTE"),
                    teamEnglishName = "LOTTE GIANTS",
                    teamShortName = "롯데",
                    isLoading = false
                ),
                onPlayerClick = {},
                onToggleShowLineup = {},
                onChangePlayer = {},
                onRefresh = {},
                onDismissToast = {},
                onProfileClick = {}
            )
        }
    }
}
