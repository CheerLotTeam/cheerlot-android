package com.gms.cheerlotandroid.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.component.CustomTopAppBarTitleWithProfile
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamId

// [임시/테스트용] TeamMembersTestViewModel 참고 — 정식 화면이 생기면 대체됩니다.
@Composable
internal fun TeamMembersTestScreen(
    state: TeamMembersTestUiState,
    onRowClick: (Int) -> Unit,
    onSelectTestTeam: (TeamId) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CustomTopAppBarTitleWithProfile(
                title = "전체 선수",
                onProfileClick = onProfileClick
            )
        },
        // 이 화면은 MainScreen의 Scaffold(bottomBar만 정의) 안에 얹혀 있어서 좌우/아래 인셋은
        // 그쪽이 이미 처리합니다. 여기서는 이 화면 고유의 topBar가 상태바를 처리할 수 있게 top만 둡니다.
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) { innerPadding ->
        val contentModifier = Modifier.fillMaxSize().padding(innerPadding)

        if (state.teamId == null) {
            Column(
                modifier = contentModifier.padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "설정에서 팀을 먼저 선택해주세요.",
                    modifier = Modifier.fillMaxWidth(),
                    style = CheerLotTextStyle.R2,
                    color = GrayScaleColor.Gray500,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { onSelectTestTeam(TeamId("KIA")) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("[테스트] KIA로 팀 선택")
                }
            }
        } else if (state.errorMessage != null) {
            Column(
                modifier = contentModifier.padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "선수 목록을 불러오지 못했습니다.\n${state.errorMessage}",
                    modifier = Modifier.fillMaxWidth(),
                    style = CheerLotTextStyle.R2,
                    color = GrayScaleColor.Gray500,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = contentModifier) {
                itemsIndexed(state.rows) { index, row ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRowClick(index) }
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = "${row.playerName} · ${row.song.title}",
                            style = CheerLotTextStyle.SB7,
                            color = GrayScaleColor.Gray900
                        )
                    }
                }
            }
        }
    }
}
