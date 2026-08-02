package com.gms.cheerlotandroid.presentation.lineup.cheersongmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.gms.cheerlotandroid.presentation.lineup.cheersongmenu.component.CheerSongMenuCell

@Composable
internal fun CheerSongMenuSheet(
    memberName: String,
    cheerSongs: List<CheerSongInfo>,
    onSelectCheerSong: (CheerSongInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = memberName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 26.dp),
            style = CheerLotTextStyle.SB6,
            color = GrayScaleColor.GrayBlack,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        cheerSongs.forEach { cheerSong ->
            CheerSongMenuCell(
                cheerSong = cheerSong,
                onClick = { onSelectCheerSong(cheerSong) }
            )
        }
    }
}

@DevicePreviews
@Preview(showBackground = true, name = "Two Cheer Songs")
@Composable
private fun CheerSongMenuSheetPreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("LOTTE")) {
            CheerSongMenuSheet(
                memberName = "전준우",
                cheerSongs = previewCheerSongs,
                onSelectCheerSong = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Three Cheer Songs")
@Composable
private fun CheerSongMenuSheetThreeSongsPreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("LOTTE")) {
            CheerSongMenuSheet(
                memberName = "전준우",
                cheerSongs = previewCheerSongs + previewCheerSongs.first().copy(
                    id = "3",
                    title = "전준우 응원가 3"
                ),
                onSelectCheerSong = {}
            )
        }
    }
}

private val previewCheerSongs = listOf(
    CheerSongInfo(
        id = "1",
        title = "전준우 응원가 1",
        lyrics = "",
        audioUrl = ""
    ),
    CheerSongInfo(
        id = "2",
        title = "전준우 응원가 2",
        lyrics = "",
        audioUrl = ""
    )
)
