package com.gms.cheerlotandroid.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.model.team.TeamInfo

private const val COLUMN_COUNT = 2
private val ROW_SPACING = 9.dp
private val COLUMN_SPACING = 17.dp

// 팀을 2열 grid로 보여줍니다. 행 개수와 무관하게 남은 세로 공간을 행마다 균등하게 채웁니다.
@Composable
fun TeamGrid(
    teams: List<TeamInfo>,
    selectedTeamId: TeamId?,
    onSelect: (TeamId) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(ROW_SPACING)
    ) {
        teams.chunked(COLUMN_COUNT).forEach { rowTeams ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(COLUMN_SPACING)
            ) {
                rowTeams.forEach { team ->
                    TeamSelectCell(
                        team = team,
                        isSelected = team.id == selectedTeamId,
                        onClick = { onSelect(team.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Preview(showBackground = true, heightDp = 500, name = "Large Font", fontScale = 1.5f)
@Composable
private fun TeamGridPreview() {
    val teams = listOf(
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

    CheerLotTheme {
        TeamGrid(
            teams = teams,
            selectedTeamId = TeamId("LOTTE"),
            onSelect = {}
        )
    }
}
