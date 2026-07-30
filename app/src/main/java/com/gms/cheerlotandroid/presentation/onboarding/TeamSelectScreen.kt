package com.gms.cheerlotandroid.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gms.cheerlotandroid.core.di.LocalAppContainer
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.component.CustomTopAppBarEditMode
import com.gms.cheerlotandroid.design.component.TeamGrid
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.model.team.TeamInfo

@Composable
fun TeamSelectScreen(
    modifier: Modifier = Modifier,
    mode: TeamSelectMode,
    onComplete: () -> Unit,
    onClose: () -> Unit = {}
) {
    val appContainer = LocalAppContainer.current
    val viewModel: TeamSelectViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                TeamSelectViewModel(
                    getAllTeamsUseCase = appContainer.getAllTeamsUseCase,
                    getSelectedTeamUseCase = appContainer.getSelectedTeamUseCase,
                    updateSelectedTeamUseCase = appContainer.updateSelectedTeamUseCase,
                    mode = mode
                )
            }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TeamSelectContent(
        mode = viewModel.mode,
        teams = uiState.teams,
        selectedTeamId = uiState.selectedTeamId,
        onSelect = viewModel::select,
        isCompleteEnabled = uiState.isCompleteEnabled,
        onCompleteClick = { viewModel.complete(onComplete) },
        onClose = onClose,
        modifier = modifier
    )
}

@Composable
private fun TeamSelectContent(
    mode: TeamSelectMode,
    teams: List<TeamInfo>,
    selectedTeamId: TeamId?,
    onSelect: (TeamId) -> Unit,
    isCompleteEnabled: Boolean,
    onCompleteClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        if (mode.showsTopBar) {
            CustomTopAppBarEditMode(
                title = mode.navigationTitle,
                onClose = onClose,
                onCheck = onCompleteClick
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 28.dp)
                .padding(top = if (mode.showsTopBar) 20.dp else 32.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                text = mode.guideText,
                style = if (mode == TeamSelectMode.ONBOARDING) CheerLotTextStyle.SB4 else CheerLotTextStyle.M3,
                color = if (mode == TeamSelectMode.ONBOARDING) GrayScaleColor.GrayBlack else GrayScaleColor.Gray300,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            TeamGrid(
                teams = teams,
                selectedTeamId = selectedTeamId,
                onSelect = onSelect,
                modifier = Modifier.weight(1f)
            )

            if (mode.showsBottomButton) {
                CompleteButton(enabled = isCompleteEnabled, onClick = onCompleteClick)
            }
        }
    }
}

@Composable
private fun CompleteButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(35.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GrayScaleColor.Gray900,
            contentColor = GrayScaleColor.GrayWhite,
            disabledContainerColor = GrayScaleColor.Gray000,
            disabledContentColor = GrayScaleColor.Gray400
        )
    ) {
        Text(text = "완료",
            style = CheerLotTextStyle.SB6,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private val previewTeams = listOf(
    TeamInfo(TeamId("LOTTE"), "롯데", "롯데 자이언츠", "LOTTE GIANTS", "투혼투지, GO HIGH"),
    TeamInfo(TeamId("SAMSUNG"), "삼성", "삼성 라이온즈", "SAMSUNG LIONS", "WIN or WOW!"),
    TeamInfo(TeamId("LG"), "LG", "LG 트윈스", "LG TWINS", "무적 LG! 끝까지 TWINS!"),
    TeamInfo(TeamId("HANWHA"), "한화", "한화 이글스", "HANWHA EAGLES", "IT IS OUR TURN"),
    TeamInfo(TeamId("KIA"), "KIA", "기아 타이거즈", "KIA TIGERS", "다시, 뜨겁게 ALWAYS KIA TIGERS"),
    TeamInfo(TeamId("NC"), "NC", "NC 다이노스", "NC DINOS", "거침없이 가자! 위풍당당"),
    TeamInfo(TeamId("KT"), "KT", "KT 위즈", "KT WIZ", "마법의 시작, 위대한 도약! GREAT KT"),
    TeamInfo(TeamId("SSG"), "SSG", "SSG 랜더스", "SSG LANDERS", "NO LIMITS, AMAZING LANDERS"),
    TeamInfo(TeamId("DOOSAN"), "두산", "두산 베어스", "DOOSAN BEARS", "TIME TO MOVE ON"),
    TeamInfo(TeamId("KIWOOM"), "키움", "키움 히어로즈", "KIWOOM HEROES", "영웅, 도전, 승리")
)

@DevicePreviews
@Preview(showBackground = true, heightDp = 700, name = "Onboarding - Large Font", fontScale = 1.5f)
@Composable
private fun TeamSelectContentOnboardingPreview() {
    CheerLotTheme {
        TeamSelectContent(
            mode = TeamSelectMode.ONBOARDING,
            teams = previewTeams,
            selectedTeamId = TeamId("LOTTE"),
            onSelect = {},
            isCompleteEnabled = true,
            onCompleteClick = {},
            onClose = {}
        )
    }
}

@DevicePreviews
@Preview(showBackground = true, heightDp = 700, name = "Change - Large Font", fontScale = 1.5f)
@Composable
private fun TeamSelectContentChangePreview() {
    CheerLotTheme {
        TeamSelectContent(
            mode = TeamSelectMode.CHANGE,
            teams = previewTeams,
            selectedTeamId = TeamId("LOTTE"),
            onSelect = {},
            isCompleteEnabled = true,
            onCompleteClick = {},
            onClose = {}
        )
    }
}
