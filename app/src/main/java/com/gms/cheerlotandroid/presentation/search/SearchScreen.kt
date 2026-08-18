package com.gms.cheerlotandroid.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SearchScreen(
    onOpenBasePlayback: (teamId: TeamId, cheerSongId: String, playerName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SearchViewModel =
        viewModel(factory = LocalAppContainer.current.viewModelFactory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // 검색 탭에 들어오면 바로 입력할 수 있도록 검색창에 자동으로 포커스를 주고 키보드를 띄웁니다.
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    // 다른 탭으로 전환되는 등 이 화면이 포커스를 정리할 새 없이 그대로 컴포지션에서 사라지는
    // 모든 경로에 대한 안전장치로, 화면이 사라질 때 키보드를 닫습니다.
    DisposableEffect(Unit) {
        onDispose { keyboardController.hideKeyboard(focusManager) }
    }

    // IME를 back/내림 버튼으로 직접 내리면 포커스가 남아 커서가 깜빡여서, IME가 사라질 때 포커스를 정리합니다.
    val isImeVisible = WindowInsets.isImeVisible
    var hasShownIme by remember { mutableStateOf(false) }
    LaunchedEffect(isImeVisible) {
        if (isImeVisible) {
            hasShownIme = true
        } else if (hasShownIme) {
            hasShownIme = false
            focusManager.clearFocus()
        }
    }

    SearchContent(
        state = uiState,
        onQueryChange = viewModel::onQueryChange,
        focusRequester = focusRequester,
        // iOS SearchView와 동일하게 결과를 탭하면 재생 시작과 동시에 전체화면 재생화면을 엽니다.
        onTapResult = { row ->
            // SearchTextField가 포커스를 쥔 채로 화면이 재생 화면으로 전환되면 키보드가
            // 정리되지 않고 화면 위에 투명하게 남는 문제가 있어, 전환 전에 명시적으로 닫습니다.
            keyboardController.hideKeyboard(focusManager)
            viewModel.onTapResult(row)
            val teamId = uiState.teamId
            val song = row.song
            if (teamId != null && song != null) {
                onOpenBasePlayback(teamId, song.id, row.playerName)
            }
        },
        onDismissToast = viewModel::dismissToast,
        modifier = modifier
    )
}

// 이 화면 안에서 "키보드 내리기"가 여러 지점(결과 탭, 스크롤 시작, 화면 이탈)에서 반복 필요해
// 하나로 모았습니다. hide()만으로는 텍스트필드가 포커스를 계속 쥐고 있어 다시 탭하지 않아도
// 키보드가 바로 재표시되는 경우가 있어 focusManager.clearFocus()를 항상 같이 호출합니다.
private fun SoftwareKeyboardController?.hideKeyboard(focusManager: FocusManager) {
    this?.hide()
    focusManager.clearFocus()
}

@Composable
private fun SearchContent(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onTapResult: (TeamMembersRow) -> Unit,
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
                    .padding(horizontal = 16.dp)
            ) {
                SearchTextField(
                    query = state.query,
                    onQueryChange = onQueryChange,
                    focusRequester = focusRequester,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val teamId = state.teamId
                when {
                    // 팀 로딩 중(teamId=null)이거나 검색어가 비면, 스피너보다 이 안내를 우선해 진입 시 깜빡임을 막습니다.
                    teamId == null || state.query.isBlank() -> SearchMessage(
                        text = "우리 팀 선수를 검색해보세요",
                        imageRes = R.drawable.no_game,
                        modifier = Modifier.weight(1f)
                    )
                    // 검색어가 있는데 아직 로스터를 불러오는 중이면 스피너를 보여줍니다.
                    state.isLoading -> Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    state.results.isEmpty() -> SearchMessage(
                        text = "검색 결과가 없습니다",
                        imageRes = R.drawable.no_season,
                        modifier = Modifier.weight(1f)
                    )
                    else -> {
                        TeamTheme(teamId = teamId) {
                            val primaryColor = TeamTheme.colors.primary
                            val keyboardController = LocalSoftwareKeyboardController.current
                            val focusManager = LocalFocusManager.current
                            val lazyListState = rememberLazyListState()

                            // iOS SearchView의 .scrollDismissesKeyboard(.immediately)와 동일하게,
                            // 결과 목록을 스크롤하기 시작하면 키보드를 바로 닫습니다.
                            LaunchedEffect(lazyListState.isScrollInProgress) {
                                if (lazyListState.isScrollInProgress) {
                                    keyboardController.hideKeyboard(focusManager)
                                }
                            }

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
                                    state = lazyListState,
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
