package com.gms.cheerlotandroid.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.core.navigation.CheerLotMainTab
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.color.semantic.CheerLotColor
import com.gms.cheerlotandroid.design.team.TeamAsset
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamId

@Composable
fun MainScreen(
    selectedDestination: CheerLotMainTab,
    onDestinationSelected: (CheerLotMainTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(factory = LocalAppContainer.current.viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // 팀 미선택 상태는 이 화면에 정상적으로 진입할 수 없는 조건이지만, 방어적으로 앱 기본 색을 fallback합니다.
    val tabColor = uiState.selectedTeamId?.let { TeamAsset.from(it).secondaryColor }
        ?: CheerLotColor.AppSecondary

    MainContent(
        selectedDestination = selectedDestination,
        onDestinationSelected = onDestinationSelected,
        tabColor = tabColor,
        modifier = modifier
    )
}

@Composable
private fun MainContent(
    selectedDestination: CheerLotMainTab,
    onDestinationSelected: (CheerLotMainTab) -> Unit,
    tabColor: Color,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            MainBottomBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                tabColor = tabColor,
                miniPlayerState = MiniPlayerUiState(
                    title = "김도영",
                    teamInitial = "KIA",
                    isPlaying = false
                )
            )
        }
    ) { innerPadding ->
        MainTabContent(
            destination = selectedDestination,
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun MainBottomBar(
    selectedDestination: CheerLotMainTab,
    onDestinationSelected: (CheerLotMainTab) -> Unit,
    tabColor: Color,
    miniPlayerState: MiniPlayerUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(GrayScaleColor.GrayWhite)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GrayScaleColor.Gray100)
        )
        MiniPlayer(state = miniPlayerState)
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
        containerColor = GrayScaleColor.GrayWhite,
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
private fun MainTabContent(
    destination: CheerLotMainTab,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        MainTabPlaceholder(destination = destination)
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

@Preview(showBackground = true)
@Composable
private fun MainContentPreview() {
    var selectedDestination by rememberSaveable { mutableStateOf(CheerLotMainTab.LINEUP) }

    CheerLotTheme {
        MainContent(
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it },
            tabColor = TeamAsset.from(TeamId("LOTTE")).secondaryColor
        )
    }
}
