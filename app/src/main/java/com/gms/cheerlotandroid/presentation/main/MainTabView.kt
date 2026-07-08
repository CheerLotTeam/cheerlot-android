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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.color.semantic.CheerLotColor
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle

@Composable
fun MainTabView(
    modifier: Modifier = Modifier
) {
    var selectedDestination by rememberSaveable {
        mutableStateOf(MainTabDestination.Lineup)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            MainBottomBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = { selectedDestination = it },
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
    selectedDestination: MainTabDestination,
    onDestinationSelected: (MainTabDestination) -> Unit,
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
        MiniPlayerView(state = miniPlayerState)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GrayScaleColor.Gray100)
        )
        MainBottomNavigationBar(
            selectedDestination = selectedDestination,
            onDestinationSelected = onDestinationSelected
        )
    }
}

@Composable
private fun MainBottomNavigationBar(
    selectedDestination: MainTabDestination,
    onDestinationSelected: (MainTabDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = GrayScaleColor.GrayWhite,
        tonalElevation = 0.dp
    ) {
        MainTabDestination.entries.forEach { destination ->
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
                    selectedIconColor = CheerLotColor.AppSecondary,
                    selectedTextColor = CheerLotColor.AppSecondary,
                    indicatorColor = CheerLotColor.AppSecondary.copy(alpha = 0.12f),
                    unselectedIconColor = GrayScaleColor.Gray400,
                    unselectedTextColor = GrayScaleColor.Gray400
                )
            )
        }
    }
}

@Composable
private fun MainTabContent(
    destination: MainTabDestination,
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
    destination: MainTabDestination,
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
private fun MainTabViewPreview() {
    CheerLotTheme {
        MainTabView()
    }
}
