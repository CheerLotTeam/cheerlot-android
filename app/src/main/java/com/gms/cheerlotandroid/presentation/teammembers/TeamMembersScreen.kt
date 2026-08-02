package com.gms.cheerlotandroid.presentation.teammembers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.data.source.TeamCatalog
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.presentation.teammembers.component.PlayButton
import com.gms.cheerlotandroid.presentation.teammembers.component.TeamCard
import com.gms.cheerlotandroid.presentation.teammembers.component.TeamMembersCell

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TeamMembersScreen(
    state: TeamMembersUiState,
    onRefresh: () -> Unit,
    onTapPlayAll: () -> Unit,
    onTapSong: (TeamMembersRow) -> Unit,
    onSnackbarShown: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            onSnackbarShown()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = GrayScaleColor.GrayWhite,
        topBar = {
            TopAppBar(
                title = { Text(text = "전체 선수", style = CheerLotTextStyle.B3, color = GrayScaleColor.Gray900) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = "설정")
                    }
                },
                // MainScreen의 상위 Scaffold(topBar 없음)가 이미 상태바 인셋을 innerPadding으로 반영하고 있어서,
                // 여기서 기본 windowInsets를 또 쓰면 상태바 높이가 두 번 반영되어 상단 여백이 과도해집니다.
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(padding)
        ) {
            TeamMembersContent(state = state, onTapPlayAll = onTapPlayAll, onTapSong = onTapSong)
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
