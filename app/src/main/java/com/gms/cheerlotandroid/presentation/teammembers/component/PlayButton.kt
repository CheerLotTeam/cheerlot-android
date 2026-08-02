package com.gms.cheerlotandroid.presentation.teammembers.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle

@Composable
internal fun PlayButton(primaryColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(28.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(primaryColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "전체 재생", style = CheerLotTextStyle.M5, color = GrayScaleColor.GrayWhite)
        Icon(
            imageVector = Icons.Filled.MusicNote,
            contentDescription = "전체 재생",
            modifier = Modifier
                .padding(start = 4.dp)
                .size(width = 15.dp, height = 14.dp),
            tint = GrayScaleColor.GrayWhite
        )
    }
}
