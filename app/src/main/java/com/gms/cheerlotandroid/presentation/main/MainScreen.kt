package com.gms.cheerlotandroid.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.core.navigation.CheerLotDialog
import com.gms.cheerlotandroid.core.navigation.CheerLotMainTab
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.component.CustomTopAppBarLargeTitle
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.lineup.LineupScreen
import com.gms.cheerlotandroid.presentation.lineup.LineupTapAction
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersScreen
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersViewModel

@Composable
fun MainScreen(
    onOpenBasePlayback: (teamId: TeamId, cheerSongId: String, playerName: String) -> Unit = { _, _, _ -> },
    onOpenLineupPlayback: (startIndex: Int) -> Unit = {},
    onOpenCheerSongMenu: (action: LineupTapAction.ShowSongList) -> Unit = {},
    onOpenLineupChange: (member: PlayerInfo) -> Unit = {},
    onShowDialog: (CheerLotDialog) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTeamId = uiState.selectedTeamId

    // Main 진입 직후 선택 팀 Flow의 첫 값이 도착하기 전에는 TeamTheme 의존 화면을 구성하지 않습니다.
    if (selectedTeamId == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {}
        return
    }

    TeamTheme(teamId = selectedTeamId) {
        MainContent(
            tabColor = TeamTheme.colors.secondary,
            onOpenBasePlayback = onOpenBasePlayback,
            onOpenLineupPlayback = onOpenLineupPlayback,
            onOpenCheerSongMenu = onOpenCheerSongMenu,
            onOpenLineupChange = onOpenLineupChange,
            onShowDialog = onShowDialog,
            onOpenSettings = onOpenSettings,
            modifier = modifier
        )
    }
}

@Composable
private fun MainContent(
    tabColor: Color,
    onOpenBasePlayback: (teamId: TeamId, cheerSongId: String, playerName: String) -> Unit,
    onOpenLineupPlayback: (startIndex: Int) -> Unit,
    onOpenCheerSongMenu: (action: LineupTapAction.ShowSongList) -> Unit,
    onOpenLineupChange: (member: PlayerInfo) -> Unit,
    onShowDialog: (CheerLotDialog) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: tabNavController/currentDestination, MiniPlayerViewModel 상태 구독, 탭 변경 side effect를 Stateful MainScreen으로 옮겨 MainContent는 상태와 콜백만 받는 Stateless UI로 정리합니다.
    val tabNavController = rememberNavController()
    val tabBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = CheerLotMainTab.entries.firstOrNull {
        it.route == tabBackStackEntry?.destination?.route
    } ?: CheerLotMainTab.LINEUP

    val miniPlayerViewModel: MiniPlayerViewModel =
        viewModel(factory = LocalAppContainer.current.viewModelFactory)
    val miniPlayerState by miniPlayerViewModel.uiState.collectAsStateWithLifecycle()

    // iOS MainTabView.showMiniPlayer와 동일하게, 라인업/검색 탭에서는 미니플레이어를 숨깁니다.
    // 라인업은 쇼츠 느낌의 풀스크린 재생이 따로 있고, 검색도 자체 미리듣기 흐름을 가지므로 배경 미니플레이어와 겹치면 안 됩니다.
    val showMiniPlayer = currentDestination != CheerLotMainTab.LINEUP && currentDestination != CheerLotMainTab.SEARCH

    // iOS는 라인업 탭에 들어오면 audioPlayer.pause()를 호출해 전체선수/검색에서 재생 중이던 배경 재생을 멈춥니다.
    val audioPlayer = LocalAppContainer.current.audioPlayer
    LaunchedEffect(currentDestination) {
        if (currentDestination == CheerLotMainTab.LINEUP) {
            audioPlayer.pause()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        // 라인업/전체선수/검색 3개 탭이 각자 자기 Scaffold(topBar = ...)로 상태바를 처리하므로,
        // 여기서는 top을 빼고 좌우/아래(제스처 내비게이션 바 등)만 반영합니다.
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal),
        bottomBar = {
            MainBottomBar(
                selectedDestination = currentDestination,
                onDestinationSelected = { destination ->
                    tabNavController.navigate(destination.route) {
                        popUpTo(tabNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                tabColor = tabColor,
                miniPlayerState = miniPlayerState.takeIf { showMiniPlayer },
                onMiniPlayerClick = {
                    miniPlayerViewModel.reopenTarget()?.let { target ->
                        onOpenBasePlayback(target.teamId, target.cheerSongId, target.playerName)
                    }
                },
                onMiniPlayerPlayClick = miniPlayerViewModel::onPlayClick,
                onMiniPlayerSkipNextClick = miniPlayerViewModel::onSkipNextClick
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = CheerLotMainTab.LINEUP.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(CheerLotMainTab.LINEUP.route) {
                LineupScreen(
                    onOpenSettings = onOpenSettings,
                    onOpenCheerSongMenu = onOpenCheerSongMenu,
                    onOpenLineupPlayback = onOpenLineupPlayback,
                    onOpenLineupChange = onOpenLineupChange,
                    onShowDialog = onShowDialog,
                )
            }
            composable(CheerLotMainTab.TEAM_MEMBERS.route) {
                TeamMembersTab(
                    onOpenSettings = onOpenSettings,
                )
            }
            composable(CheerLotMainTab.SEARCH.route) {
                SearchTab()
            }
        }
    }
}

@Composable
private fun MainBottomBar(
    selectedDestination: CheerLotMainTab,
    onDestinationSelected: (CheerLotMainTab) -> Unit,
    tabColor: Color,
    miniPlayerState: MiniPlayerUiState?,
    onMiniPlayerClick: () -> Unit,
    onMiniPlayerPlayClick: () -> Unit,
    onMiniPlayerSkipNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(Color.White)
    ) {
        if (miniPlayerState != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(GrayScaleColor.Gray100)
            )
            MiniPlayer(
                state = miniPlayerState,
                onClick = onMiniPlayerClick,
                onPlayClick = onMiniPlayerPlayClick,
                onSkipNextClick = onMiniPlayerSkipNextClick
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GrayScaleColor.Gray100)
        )
        MainBottomNavigationBar(
            selectedDestination = selectedDestination,
            onDestinationSelected = onDestinationSelected,
            tabColor = tabColor
        )
    }
}

@Composable
private fun MainBottomNavigationBar(
    selectedDestination: CheerLotMainTab,
    onDestinationSelected: (CheerLotMainTab) -> Unit,
    tabColor: Color,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        CheerLotMainTab.entries.forEach { destination ->
            val selected = destination == selectedDestination

            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = CheerLotTextStyle.SB10
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = tabColor,
                    selectedTextColor = tabColor,
                    indicatorColor = tabColor.copy(alpha = 0.12f),
                    unselectedIconColor = GrayScaleColor.Gray300,
                    unselectedTextColor = GrayScaleColor.Gray300
                )
            )
        }
    }
}

@Composable
private fun TeamMembersTab(
    onOpenSettings: () -> Unit
) {
    // TODO: TeamMembersScreen을 Stateful 진입점으로, 현재 TeamMembersScreen UI를 private TeamMembersContent로 분리한 뒤 이 중간 Tab composable을 제거합니다.
    val viewModel: TeamMembersViewModel =
        viewModel(factory = LocalAppContainer.current.viewModelFactory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TeamMembersScreen(
        state = uiState,
        onRefresh = viewModel::refresh,
        onTapPlayAll = viewModel::onTapPlayAll,
        // iOS TeamMembersViewModel.didTapSong과 동일하게 재생만 시작하고 화면 전환은 하지 않습니다.
        // 재생 중인 곡은 하단 MiniPlayer로 노출되고, 전체화면 PlaybackView는 MiniPlayer를 탭했을 때만 엽니다.
        onTapSong = { row ->
            viewModel.onTapSong(row)
        },
        onSnackbarShown = viewModel::onSnackbarShown,
        onOpenSettings = onOpenSettings
    )
}

@Composable
private fun SearchTab() {
    // TODO: 검색 기능 구현 시 SearchScreen을 Stateful 진입점으로 만들고 실제 UI는 private SearchContent로 분리한 뒤 이 placeholder Tab composable을 제거합니다.
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopAppBarLargeTitle(title = "검색")
        },
        contentWindowInsets =
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            MainTabPlaceholder(
                destination = CheerLotMainTab.SEARCH
            )
        }
    }
}

@Composable
private fun MainTabPlaceholder(
    destination: CheerLotMainTab,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = destination.label,
            style = CheerLotTextStyle.B3,
            color = GrayScaleColor.Gray900,
            textAlign = TextAlign.Center
        )
        Text(
            text = "화면 구현 예정",
            style = CheerLotTextStyle.R2,
            color = GrayScaleColor.Gray500,
            textAlign = TextAlign.Center
        )
    }
}

// MainTabScreen 전체는 LocalAppContainer(DI)가 필요해 프리뷰에서 크래시나므로,
// DI 없이도 그릴 수 있는 MainBottomBar만 목업 상태로 미리보기합니다.
@Preview(showBackground = true)
@Composable
private fun MainContentPreview() {
    var selectedDestination by rememberSaveable { mutableStateOf(CheerLotMainTab.LINEUP) }

    CheerLotTheme {
        TeamTheme(teamId = TeamId("KIA")) {
            MainBottomBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = { selectedDestination = it },
                tabColor = TeamTheme.colors.secondary,
                miniPlayerState = MiniPlayerUiState(
                    title = "김도영",
                    teamId = TeamId("KIA"),
                    isPlaying = false
                ),
                onMiniPlayerClick = {},
                onMiniPlayerPlayClick = {},
                onMiniPlayerSkipNextClick = {}
            )
        }
    }
}
