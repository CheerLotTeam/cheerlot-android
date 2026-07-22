package com.gms.cheerlotandroid.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.team.TeamAsset
import com.gms.cheerlotandroid.design.theme.CheerLotTheme
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle
import com.gms.cheerlotandroid.domain.model.team.TeamId
import com.gms.cheerlotandroid.domain.model.team.TeamInfo

// 팀 선택 뷰에서의 팀 Cell입니다.
@Composable
fun TeamSelectCell(
    team: TeamInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val asset = TeamAsset.from(team.id)
    val backgroundColor = if (isSelected) asset.primaryColor else GrayScaleColor.GrayWhite
    val borderColor = if (isSelected) asset.primaryColor else GrayScaleColor.Gray000
    val textColor = if (isSelected) GrayScaleColor.GrayWhite else GrayScaleColor.Gray300

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = backgroundColor, shape = RoundedCornerShape(10.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = team.englishFullName.replace(" ", "\n"),
            style = CheerLotTextStyle.T3,
            color = textColor,
            textAlign = TextAlign.Center
        )
        Text(
            text = team.longName,
            style = CheerLotTextStyle.SB9,
            color = textColor,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// 150dp(일반)/100dp(좁은 폭) 셀을 나란히 놓고, longName ellipsis가 실제로 필요한 상황을 확인합니다.
@Preview(showBackground = true, name = "Default")
@Preview(showBackground = true, name = "Large Font", fontScale = 1.5f)
@Composable
private fun TeamSelectCellPreview() {
    val previewTeam = TeamInfo(
        id = TeamId("LOTTE"),
        shortName = "롯데",
        longName = "롯데 자이언츠",
        englishFullName = "LOTTE GIANTS",
        slogan = "투혼투지, GO HIGH"
    )

    CheerLotTheme {
        Column {
            TeamSelectCell(
                team = previewTeam,
                isSelected = true,
                onClick = {},
                modifier = Modifier.width(150.dp).height(80.dp)
            )
            TeamSelectCell(
                team = previewTeam,
                isSelected = false,
                onClick = {},
                modifier = Modifier.width(100.dp).height(80.dp)
            )
        }
    }
}
