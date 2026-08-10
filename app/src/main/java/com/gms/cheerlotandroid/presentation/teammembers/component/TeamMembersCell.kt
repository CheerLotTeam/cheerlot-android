package com.gms.cheerlotandroid.presentation.teammembers.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.presentation.teammembers.TeamMembersRow

@Composable
internal fun TeamMembersCell(
    row: TeamMembersRow,
    primaryColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 이름과 등번호는 폰트 크기가 달라 CenterVertically로만 맞추면 서로 어긋나 보여서,
        // 두 텍스트만 따로 Row로 묶어 baseline 기준으로 정렬합니다.
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = row.playerName, style = CheerLotTextStyle.SB4, color = GrayScaleColor.GrayBlack)
            Spacer(Modifier.width(3.dp))
            Text(text = row.backNumber.toString(), style = CheerLotTextStyle.M3, color = GrayScaleColor.Gray400)
        }
        Spacer(Modifier.weight(1f))
        row.titleText?.let {
            Text(text = it, style = CheerLotTextStyle.M4, color = GrayScaleColor.Gray300)
        }
        Spacer(Modifier.width(16.dp))
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = if (row.hasSong) "재생" else "응원가 없음",
            modifier = Modifier.size(22.dp),
            tint = if (row.hasSong) primaryColor else GrayScaleColor.Gray200
        )
    }
}
