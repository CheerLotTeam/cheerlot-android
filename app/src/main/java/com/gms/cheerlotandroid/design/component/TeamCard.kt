package com.gms.cheerlotandroid.design.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.R
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamInfo

// 호출부가 TeamTheme(teamId) { ... }로 감싸져 있다는 전제로 TeamTheme.colors를 바로 읽습니다.
// 배경 텍스처(team_card_bg, 소프트라이트 블렌드)는 LineupCard와 동일한 에셋/방식을 재사용합니다.
// showMoreIcon: iOS의 설정 화면 전용 TeamCardButton과 동일하게, 설정 화면(나의 팀)에서만
// 우측 상단에 ellipsis 아이콘을 보여줍니다. 전체선수 탭 헤더에서는 쓰지 않습니다.
@Composable
fun TeamCard(
    team: TeamInfo,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showMoreIcon: Boolean = false
) {
    val shape = RoundedCornerShape(12.dp)
    val colors = TeamTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(colors.primary)
            .border(2.dp, colors.primaryPalette.color200, shape)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(colors.primaryPalette.color200, colors.primaryPalette.color600)
                ),
                alpha = 0.2f
            )
        }
        Image(
            painter = painterResource(R.drawable.team_card_bg),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    alpha = 0.75f
                    blendMode = BlendMode.Softlight
                    compositingStrategy = CompositingStrategy.Offscreen
                },
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 27.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = team.englishFullName.uppercase(),
                style = CheerLotTextStyle.T2.merge(
                    TextStyle(shadow = Shadow(colors.primaryPalette.color600, blurRadius = 8f, offset = Offset(0f, 1f)))
                ),
                color = GrayScaleColor.GrayWhite
            )
            Text(text = team.slogan.uppercase(), style = CheerLotTextStyle.M5, color = colors.primaryPalette.color200)
        }

        if (showMoreIcon) {
            Icon(
                imageVector = Icons.Filled.MoreHoriz,
                contentDescription = null,
                tint = colors.primaryPalette.color200,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 14.dp)
                    .size(16.dp)
            )
        }
    }
}
