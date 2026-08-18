package com.gms.cheerlotandroid.presentation.teammembers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.data.source.TeamCatalog
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.component.CustomToastMessage
import com.gms.cheerlotandroid.design.component.CustomTopAppBarTitleWithProfile
import com.gms.cheerlotandroid.design.component.TeamCard
import com.gms.cheerlotandroid.design.component.TeamMembersCell
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.presentation.teammembers.component.PlayButton

@Composable
internal fun TeamMembersScreen(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TeamMembersViewModel =
        viewModel(factory = LocalAppContainer.current.viewModelFactory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TeamMembersContent(
        state = uiState,
        onRefresh = viewModel::refresh,
        onTapPlayAll = viewModel::onTapPlayAll,
        // iOS TeamMembersViewModel.didTapSong과 동일하게 재생만 시작하고 화면 전환은 하지 않습니다.
        // 재생 중인 곡은 하단 MiniPlayer로 노출되고, 전체화면 PlaybackView는 MiniPlayer를 탭했을 때만 엽니다.
        onTapSong = viewModel::onTapSong,
        onDismissToast = viewModel::dismissToast,
        onOpenSettings = onOpenSettings,
        modifier = modifier
    )
}

@Composable
private fun TeamMembersContent(
    state: TeamMembersUiState,
    onRefresh: () -> Unit,
    onTapPlayAll: () -> Unit,
    onTapSong: (TeamMembersRow) -> Unit,
    onDismissToast: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CustomTopAppBarTitleWithProfile(title = "전체 선수", onProfileClick = onOpenSettings)
        },
        // MainScreen의 상위 Scaffold가 이미 bottom(제스처 내비게이션 바 등)을 innerPadding으로 반영하고 있어서,
        // 여기서는 top만 반영합니다(Lineup/Search 탭과 동일한 패턴).
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TeamMembersList(state = state, onTapPlayAll = onTapPlayAll, onTapSong = onTapSong)

            CustomToastMessage(
                message = state.toastMessage,
                isVisible = state.isToastVisible,
                onDismiss = onDismissToast
            )
        }
    }
}

@Composable
private fun TeamMembersList(
    state: TeamMembersUiState,
    onTapPlayAll: () -> Unit,
    onTapSong: (TeamMembersRow) -> Unit
) {
    val teamId = state.teamId
    val team = teamId?.let(TeamCatalog::findById)

    // 팀을 읽어오는 짧은 순간 teamId가 null이라, 안내 문구 대신 스피너를 보여줍니다.
    if (teamId == null || team == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    TeamTheme(teamId = teamId) {
        val primaryColor = TeamTheme.colors.primary

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item { TeamCard(team = team) }
            item {
                // TeamCard와의 간격(16dp)만 spacedBy 대신 여기서 직접 줍니다 — 셀 간 간격(24dp)은
                // 그대로 두고 이 두 헤더 사이 간격만 좁혀야 해서, LazyColumn 전체에 걸리는
                // verticalArrangement 하나로는 구간별로 다르게 줄 수 없습니다.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "총 ${state.totalSongCount}곡", style = CheerLotTextStyle.M4, color = GrayScaleColor.Gray400)
                    PlayButton(primaryColor = primaryColor, onClick = onTapPlayAll)
                }
            }

            // 헤더(TeamCard+전체재생)는 팀이 정해지면 항상 보여주고, 목록 부분만 로딩/에러/실제 목록으로 나뉩니다.
            // 헤더 바로 아래(첫 항목)는 16dp, 그 아래로는 셀 간 기존 간격(24dp)을 유지합니다.
            when {
                // iOS TeamMembersView의 ProgressView()와 동일하게 스피너로 로딩 상태를 보여줍니다.
                state.isLoading -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> itemsIndexed(state.rows, key = { _, row -> row.id }) { index, row ->
                    TeamMembersCell(
                        row = row,
                        primaryColor = primaryColor,
                        onClick = { onTapSong(row) },
                        modifier = Modifier.padding(top = if (index == 0) 16.dp else 24.dp)
                    )
                }
            }
        }
    }
}
