package com.gms.cheerlotandroid.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.R
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.component.CustomTopAppBarLargeTitle
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.presentation.search.component.SearchTextField
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersRow
import com.gms.cheerlotandroid.presentation.teammembers.component.TeamMembersCell

@Composable
internal fun SearchScreen(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onTapResult: (TeamMembersRow) -> Unit,
    onSnackbarShown: () -> Unit,
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
        topBar = { CustomTopAppBarLargeTitle(title = "검색") },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            SearchTextField(
                query = state.query,
                onQueryChange = onQueryChange,
                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
            )

            val teamId = state.teamId
            when {
                teamId == null -> SearchMessage("설정에서 응원 팀을 선택해주세요")
                state.query.isBlank() -> SearchMessage("우리 팀 선수를 검색해보세요", R.drawable.no_game)
                state.results.isEmpty() -> SearchMessage("검색 결과가 없습니다", R.drawable.no_season)
                else -> {
                    TeamTheme(teamId = teamId) {
                        val primaryColor = TeamTheme.colors.primary

                        Text(
                            text = "총 ${state.totalSongCount}곡",
                            style = CheerLotTextStyle.M4,
                            color = GrayScaleColor.Gray400,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            items(state.results, key = { it.id }) { row ->
                                TeamMembersCell(
                                    row = row,
                                    primaryColor = primaryColor,
                                    onClick = { onTapResult(row) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchMessage(text: String, imageRes: Int? = null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (imageRes != null) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(130.dp)
                        .padding(bottom = 28.dp)
                )
            }
            Text(
                text = text,
                style = CheerLotTextStyle.M1,
                color = GrayScaleColor.Gray200,
                textAlign = TextAlign.Center
            )
        }
    }
}
