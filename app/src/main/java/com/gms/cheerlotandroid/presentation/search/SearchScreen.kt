package com.gms.cheerlotandroid.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gms.cheerlotandroid.R
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.component.CustomToastMessage
import com.gms.cheerlotandroid.design.component.CustomTopAppBarLargeTitle
import com.gms.cheerlotandroid.design.component.TeamMembersCell
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.search.component.SearchTextField
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersRow

@Composable
internal fun SearchScreen(
    onOpenBasePlayback: (teamId: TeamId, cheerSongId: String, playerName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SearchViewModel =
        viewModel(factory = LocalAppContainer.current.viewModelFactory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchContent(
        state = uiState,
        onQueryChange = viewModel::onQueryChange,
        // iOS SearchView와 동일하게 결과를 탭하면 재생 시작과 동시에 전체화면 재생화면을 엽니다.
        onTapResult = { row ->
            viewModel.onTapResult(row)
            val teamId = uiState.teamId
            val song = row.song
            if (teamId != null && song != null) {
                onOpenBasePlayback(teamId, song.id, row.playerName)
            }
        },
        onRetry = viewModel::retry,
        onDismissToast = viewModel::dismissToast,
        modifier = modifier
    )
}

@Composable
private fun SearchContent(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onTapResult: (TeamMembersRow) -> Unit,
    onRetry: () -> Unit,
    onDismissToast: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CustomTopAppBarLargeTitle(title = "검색") },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier
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
                    teamId == null -> SearchMessage(
                        text = "설정에서 응원 팀을 선택해주세요",
                        modifier = Modifier.weight(1f)
                    )
                    state.errorMessage != null -> SearchMessage(
                        text = state.errorMessage,
                        onRetry = onRetry,
                        modifier = Modifier.weight(1f)
                    )
                    state.query.isBlank() -> SearchMessage(
                        text = "우리 팀 선수를 검색해보세요",
                        imageRes = R.drawable.no_game,
                        modifier = Modifier.weight(1f)
                    )
                    state.results.isEmpty() -> SearchMessage(
                        text = "검색 결과가 없습니다",
                        imageRes = R.drawable.no_season,
                        modifier = Modifier.weight(1f)
                    )
                    else -> {
                        TeamTheme(teamId = teamId) {
                            val primaryColor = TeamTheme.colors.primary

                            // SearchTextField가 이미 쓴 높이를 빼고 남은 공간을 이 Column에 정확히
                            // 할당해야, LazyColumn이 Column의 전체 높이로 측정돼 하단이 잘리는 걸
                            // 막을 수 있습니다(weight 없는 형제는 남은 공간이 아니라 부모의 전체
                            // maxHeight로 측정되는 Compose Column의 특성).
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "총 ${state.totalSongCount}곡",
                                    style = CheerLotTextStyle.M4,
                                    color = GrayScaleColor.Gray400,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
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

            CustomToastMessage(
                message = state.toastMessage,
                isVisible = state.isToastVisible,
                onDismiss = onDismissToast
            )
        }
    }
}

@Composable
private fun SearchMessage(
    text: String,
    imageRes: Int? = null,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
            if (onRetry != null) {
                Text(
                    text = "다시 시도",
                    style = CheerLotTextStyle.SB7,
                    color = GrayScaleColor.Gray400,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clickable(onClick = onRetry)
                )
            }
        }
    }
}
