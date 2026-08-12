package com.gms.cheerlotandroid.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.resource.team.TeamResource
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamId

internal data class MiniPlayerUiState(
    val title: String,
    val teamId: TeamId?,
    val isPlaying: Boolean
)

@Composable
internal fun MiniPlayer(
    state: MiniPlayerUiState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onPlayClick: () -> Unit = {},
    onSkipNextClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MiniPlayerCover(
            teamId = state.teamId,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = state.title,
            modifier = Modifier.weight(1f),
            style = CheerLotTextStyle.SB7,
            color = GrayScaleColor.Gray900,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (state.isPlaying) "일시정지" else "재생",
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onPlayClick),
                tint = GrayScaleColor.GrayBlack
            )
            Icon(
                imageVector = Icons.Filled.FastForward,
                contentDescription = "다음 곡",
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onSkipNextClick),
                tint = GrayScaleColor.GrayBlack
            )
        }
    }
}

// iOS MainTabView의 팀별 커버와 동일한 이미지를 축소한 team_cover_thumb 리소스를 사용합니다.
@Composable
private fun MiniPlayerCover(
    teamId: TeamId?,
    modifier: Modifier = Modifier
) {
    // 원본 커버(594x594)를 40dp로 직접 축소하면 그라데이션에 디더링 노이즈가 생겨서,
    // 미니플레이어 전용으로 미리 축소해둔 썸네일(256x256)을 씁니다.
    val coverResId = teamId?.let(TeamResource::coverThumbnailRes)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(GrayScaleColor.Gray100)
    ) {
        if (coverResId != null) {
            // painterResource()가 반환하는 Painter 오버로드는 filterQuality를 지원하지 않아서,
            // ImageBitmap 오버로드를 씁니다. 594x594 원본을 40dp로 크게 축소하는데 기본 필터링으론
            // 그라데이션에 노이즈가 보여서 High로 고정합니다.
            Image(
                bitmap = ImageBitmap.imageResource(coverResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MiniPlayerPreview() {
    CheerLotTheme {
        MiniPlayer(
            state = MiniPlayerUiState(
                title = "김도영",
                teamId = TeamId("KIA"),
                isPlaying = false
            )
        )
    }
}
