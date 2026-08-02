package com.gms.cheerlotandroid.presentation.lineupplayback.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.R
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.preview.DevicePreviews
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.presentation.lineupplayback.LineupPlaybackColors
import com.gms.cheerlotandroid.presentation.lineupplayback.LineupPlaybackItem
import com.gms.cheerlotandroid.presentation.lineupplayback.toLineupPlaybackColors

@Composable
internal fun LineupPlayCard(
    item: LineupPlaybackItem,
    colors: LineupPlaybackColors,
    isPlaying: Boolean,
    onTapPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(shape)
            .background(colors.primaryColor)
            .border(width = 2.dp, color = colors.cardStrokeColor, shape = shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onTapPlayPause
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(brush = colors.cardBackgroundGradient, alpha = 0.2f)
        }
        playCardBackground(item.battingOrder)?.let { backgroundRes ->
            Image(
                painter = painterResource(backgroundRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 0.75f
                        blendMode = BlendMode.Softlight
                        compositingStrategy = CompositingStrategy.Offscreen
                    },
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            CardHeader(item = item, colors = colors, isPlaying = isPlaying)
            Spacer(modifier = Modifier.height(55.dp))
            Lyrics(
                lyrics = item.lyrics,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun CardHeader(
    item: LineupPlaybackItem,
    colors: LineupPlaybackColors,
    isPlaying: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.battingOrder.toString(),
                style = CheerLotTextStyle.SB1,
                color = colors.battingOrderTextColor
            )
            Column {
                Text(
                    text = item.memberName,
                    style = CheerLotTextStyle.SB3,
                    color = GrayScaleColor.GrayWhite,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = item.cheerSongTitle,
                    style = CheerLotTextStyle.R2,
                    color = colors.cardContentsColor,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Icon(
            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = if (isPlaying) "일시 정지" else "재생",
            tint = colors.cardContentsColor,
            modifier = Modifier
                .padding(top = 3.6.dp)
        )
    }
}

@Composable
private fun Lyrics(lyrics: String, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val canScrollBackward = scrollState.canScrollBackward
    val canScrollForward = scrollState.canScrollForward
    val maskStops = remember(canScrollBackward, canScrollForward) {
        lyricsMaskStops(
            canScrollBackward = canScrollBackward,
            canScrollForward = canScrollForward
        )
    }
    val maskModifier = if (canScrollBackward || canScrollForward) {
        Modifier
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        colorStops = maskStops
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
    } else {
        Modifier
    }

    Box(
        modifier = modifier.then(maskModifier),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = lyrics.replace("\\n", "\n"),
            style = CheerLotTextStyle.SB2,
            color = GrayScaleColor.GrayWhite,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        )
    }
}

private fun lyricsMaskStops(
    canScrollBackward: Boolean,
    canScrollForward: Boolean
): Array<Pair<Float, Color>> {
    return when {
        canScrollBackward && canScrollForward -> arrayOf(
            0f to Color.Transparent,
            0.15f to Color.Black,
            0.85f to Color.Black,
            1f to Color.Transparent
        )

        canScrollBackward -> arrayOf(
            0f to Color.Transparent,
            0.15f to Color.Black,
            1f to Color.Black
        )

        else -> arrayOf(
            0f to Color.Black,
            0.85f to Color.Black,
            1f to Color.Transparent
        )
    }
}

@DrawableRes
private fun playCardBackground(battingOrder: Int): Int? {
    return when (battingOrder) {
        1 -> R.drawable.play_card_bg_1
        2 -> R.drawable.play_card_bg_2
        3 -> R.drawable.play_card_bg_3
        4 -> R.drawable.play_card_bg_4
        5 -> R.drawable.play_card_bg_5
        6 -> R.drawable.play_card_bg_6
        7 -> R.drawable.play_card_bg_7
        8 -> R.drawable.play_card_bg_8
        9 -> R.drawable.play_card_bg_9
        else -> null
    }
}

@DevicePreviews
@Preview(showBackground = true, widthDp = 337, heightDp = 538)
@Composable
private fun LineupPlayCardPreview() {
    CheerLotTheme {
        TeamTheme(teamId = TeamId("SAMSUNG")) {
            LineupPlayCard(
                item = previewItem,
                colors = TeamTheme.colors.toLineupPlaybackColors(TeamId("SAMSUNG")),
                isPlaying = false,
                onTapPlayPause = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private val previewItem = LineupPlaybackItem(
    id = "1-1",
    battingOrder = 1,
    memberName = "구자욱",
    cheerSongTitle = "기본 응원가",
    lyrics = "삼성의 구자욱 삼성의 구자욱\n안타를 날려버려 삼성 구자욱\n" +
        "삼성의 구자욱 삼성의 구자욱\n홈런을 날려버려 삼성 구자욱"
)
