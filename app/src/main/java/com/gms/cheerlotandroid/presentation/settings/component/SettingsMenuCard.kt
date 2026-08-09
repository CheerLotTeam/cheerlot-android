package com.gms.cheerlotandroid.presentation.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle

// iOS SettingsMenuCard와 동일한 행 목록 카드입니다. 행이 1개면 더 둥근 모서리(25dp), 2개 이상이면
// 20dp를 씁니다.
@Composable
internal fun SettingsMenuCard(
    titles: List<String>,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(if (titles.size <= 1) 25.dp else 20.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(GrayScaleColor.Gray000)
            .padding(horizontal = 19.dp, vertical = 6.dp)
    ) {
        titles.forEachIndexed { index, title ->
            SettingsMenuRow(title = title, onClick = { onTap(index) })
            if (index < titles.lastIndex) {
                HorizontalDivider(
                    color = GrayScaleColor.Gray100,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
private fun SettingsMenuRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = CheerLotTextStyle.M3,
            color = GrayScaleColor.Gray800,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GrayScaleColor.Gray100,
            modifier = Modifier.height(16.dp)
        )
    }
}
