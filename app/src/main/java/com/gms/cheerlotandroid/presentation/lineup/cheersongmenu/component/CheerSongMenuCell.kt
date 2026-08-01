package com.gms.cheerlotandroid.presentation.lineup.cheersongmenu.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.team.TeamId

@Composable
internal fun CheerSongMenuCell(
    cheerSong: CheerSongInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        verticalArrangement = Arrangement.spacedBy(26.dp)
    ) {
        HorizontalDivider(color = GrayScaleColor.Gray100)
        Text(
            text = cheerSong.title,
            style = CheerLotTextStyle.SB6,
            color = TeamTheme.colors.primary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 26.dp)
        )
    }
}

@DevicePreviews
@Composable
private fun CheerSongMenuCellPreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("LOTTE")) {
            CheerSongMenuCell(
                cheerSong = previewCheerSong,
                onClick = {}
            )
        }
    }
}

private val previewCheerSong = CheerSongInfo(
    id = "1",
    title = "전준우 응원가",
    lyrics = "",
    audioUrl = ""
)
