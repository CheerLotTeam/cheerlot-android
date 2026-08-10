package com.gms.cheerlotandroid.presentation.teammembers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        containerColor = GrayScaleColor.GrayWhite,
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
            TeamMembersContent(state = state, onTapPlayAll = onTapPlayAll, onTapSong = onTapSong)

            CustomToastMessage(
                message = state.toastMessage,
                isVisible = state.isToastVisible,
                onDismiss = onDismissToast
            )
        }
    }
}

@Composable
private fun TeamMembersContent(
    state: TeamMembersUiState,
    onTapPlayAll: () -> Unit,
    onTapSong: (TeamMembersRow) -> Unit
) {
    val teamId = state.teamId
    val team = teamId?.let(TeamCatalog::findById)

    if (teamId == null || team == null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(text = "설정에서 응원 팀을 선택해주세요.", style = CheerLotTextStyle.M4, color = GrayScaleColor.Gray400)
            }
        }
        return
    }

    TeamTheme(teamId = teamId) {
        val primaryColor = TeamTheme.colors.primary

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { TeamCard(team = team) }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "총 ${state.totalSongCount}곡", style = CheerLotTextStyle.M4, color = GrayScaleColor.Gray400)
                    PlayButton(primaryColor = primaryColor, onClick = onTapPlayAll)
                }
            }

            // 헤더(TeamCard+전체재생)는 팀이 정해지면 항상 보여주고, 목록 부분만 로딩/에러/실제 목록으로 나뉩니다.
            when {
                state.isLoading -> item {
                    Text(text = "선수 목록을 불러오는 중이에요", style = CheerLotTextStyle.M4, color = GrayScaleColor.Gray400)
                }

                state.errorMessage != null -> item {
                    Text(text = state.errorMessage, style = CheerLotTextStyle.M4, color = GrayScaleColor.Gray400)
                }

                else -> items(state.rows, key = { it.id }) { row ->
                    TeamMembersCell(row = row, primaryColor = primaryColor, onClick = { onTapSong(row) })
                }
            }
        }
    }
}
