package com.gms.cheerlotandroid.presentation.lineupchange.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.player.PlayerId
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.lineupchange.LineupChangeColors
import com.gms.cheerlotandroid.presentation.lineupchange.toLineupChangeColors

@Composable
internal fun ChangeMemberSelectCell(
    member: PlayerInfo,
    colors: LineupChangeColors,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val shadowColor = if (isSelected) {
        colors.cellShadowColor
    } else {
        GrayScaleColor.Gray500.copy(alpha = 0.15f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 8.dp,
                    color = shadowColor,
                    offset = DpOffset.Zero
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = shape,
        color = if (isSelected) colors.selectedCellFillColor else GrayScaleColor.GrayWhite,
        contentColor = if (isSelected) colors.primaryColor else GrayScaleColor.Gray500,
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isSelected) colors.selectedCellStrokeColor else GrayScaleColor.GrayWhite
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = member.name,
                style = CheerLotTextStyle.SB5,
                color = if (isSelected) colors.primaryColor else GrayScaleColor.Gray500,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@DevicePreviews
@Composable
private fun ChangeMemberSelectCellSelectedPreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("LOTTE")) {
            ChangeMemberSelectCell(
                member = previewMember,
                colors = TeamTheme.colors.toLineupChangeColors(),
                isSelected = true,
                onClick = {}
            )
        }
    }
}

@Preview(name = "Unselected")
@Composable
private fun ChangeMemberSelectCellUnselectedPreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("LOTTE")) {
            ChangeMemberSelectCell(
                member = previewMember,
                colors = TeamTheme.colors.toLineupChangeColors(),
                isSelected = false,
                onClick = {}
            )
        }
    }
}

private val previewMember = PlayerInfo(
    id = PlayerId("1"),
    teamId = TeamId("LOTTE"),
    name = "전준우",
    backNumber = 8,
    position = "외야수",
    batThrow = "우투우타",
    battingOrder = null,
    cheerSongs = emptyList()
)
