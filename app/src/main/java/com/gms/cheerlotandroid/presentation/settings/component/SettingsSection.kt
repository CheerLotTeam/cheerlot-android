package com.gms.cheerlotandroid.presentation.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.gms.cheerlotandroid.design.color.grayscale.GrayScaleColor
import com.gms.cheerlotandroid.design.typography.CheerLotTextStyle

// 설정 화면들의 "나의 팀"/"지원"/"쳐랏 소개"처럼, 라벨 하나 + 컨텐츠로 구성된 섹션 공통 레이아웃입니다.
@Composable
internal fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = title, style = CheerLotTextStyle.SB8, color = GrayScaleColor.Gray500)
        content()
    }
}
