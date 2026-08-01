package com.gms.cheerlotandroid.presentation.lineupchange

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.component.CustomTopAppBarEditMode
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.player.PlayerId
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.lineupchange.component.ChangeMemberSelectCell

@Composable
internal fun LineupChangeSheet(
    lineupMember: PlayerInfo,
    benchMembers: List<PlayerInfo>,
    selectedMemberId: PlayerId?,
    onSelectMember: (PlayerInfo) -> Unit,
    onClose: () -> Unit,
    onCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TeamTheme.colors.toLineupChangeColors()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CustomTopAppBarEditMode(
                title = "선수 교체",
                onClose = onClose,
                onCheck = onCheck,
                checkColor = if (selectedMemberId != null) colors.primaryColor else GrayScaleColor.Gray800
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ChangeMemberHeader(
                memberName = lineupMember.name,
                modifier = Modifier.padding(top = 10.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 20.dp,
                    vertical = 12.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(benchMembers, key = { it.id.value }) { member ->
                    ChangeMemberSelectCell(
                        member = member,
                        colors = colors,
                        isSelected = member.id == selectedMemberId,
                        onClick = { onSelectMember(member) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChangeMemberHeader(memberName: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "교체 선수",
            style = CheerLotTextStyle.M3,
            color = GrayScaleColor.Gray300,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = memberName,
            style = CheerLotTextStyle.B3,
            color = GrayScaleColor.GrayBlack,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@DevicePreviews
@Preview(showBackground = true, heightDp = 700, name = "Selected Member")
@Composable
private fun LineupChangeSheetPreview() {
    val teamId = TeamId("SAMSUNG")
    val lineupMember = previewMember(id = "lineup", name = "김민석", teamId = teamId)
    val benchMembers = listOf(
        previewMember(id = "1", name = "전준우", teamId = teamId),
        previewMember(id = "2", name = "윤동희", teamId = teamId),
        previewMember(id = "3", name = "황성빈", teamId = teamId),
        previewMember(id = "4", name = "나승엽", teamId = teamId),
        previewMember(id = "5", name = "고승민", teamId = teamId),
        previewMember(id = "6", name = "손호영", teamId = teamId)
    )

    CheerLotTheme {
        TeamTheme(teamId = teamId) {
            LineupChangeSheet(
                lineupMember = lineupMember,
                benchMembers = benchMembers,
                selectedMemberId = benchMembers.first().id,
                onSelectMember = {},
                onClose = {},
                onCheck = {}
            )
        }
    }
}

private fun previewMember(id: String, name: String, teamId: TeamId): PlayerInfo {
    return PlayerInfo(
        id = PlayerId(id),
        teamId = teamId,
        name = name,
        backNumber = 0,
        position = "교체선수",
        batThrow = "",
        battingOrder = null,
        cheerSongs = emptyList()
    )
}
