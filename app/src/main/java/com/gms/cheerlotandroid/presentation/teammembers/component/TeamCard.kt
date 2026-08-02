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
import com.gms.cheerlotandroid.design.team.TeamAsset
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamInfo

@Composable
internal fun TeamCard(team: TeamInfo, asset: TeamAsset, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(asset.primaryColor)
            .background(
                Brush.verticalGradient(
                    listOf(
                        asset.primaryPalette.color200.copy(alpha = 0.2f),
                        asset.primaryPalette.color600.copy(alpha = 0.2f)
                    )
                )
            )
            .border(2.dp, asset.primaryPalette.color200, shape)
            .padding(horizontal = 24.dp, vertical = 27.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = team.englishFullName.uppercase(),
            style = CheerLotTextStyle.T2.merge(
                TextStyle(shadow = Shadow(asset.primaryPalette.color600, blurRadius = 8f, offset = Offset(0f, 1f)))
            ),
            color = GrayScaleColor.GrayWhite
        )
        Text(text = team.slogan.uppercase(), style = CheerLotTextStyle.M5, color = asset.primaryPalette.color200)
    }
}
