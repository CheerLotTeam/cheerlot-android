package com.gms.cheerlotandroid.presentation.teammembers.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.theme.TeamTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamInfo

// 호출부가 TeamTheme(teamId) { ... }로 감싸져 있다는 전제로 TeamTheme.colors를 바로 읽습니다.
@Composable
internal fun TeamCard(team: TeamInfo, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    val colors = TeamTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.primary)
            .background(
                Brush.verticalGradient(
                    listOf(
                        colors.primaryPalette.color200.copy(alpha = 0.2f),
                        colors.primaryPalette.color600.copy(alpha = 0.2f)
                    )
                )
            )
            .border(2.dp, colors.primaryPalette.color200, shape)
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
}
